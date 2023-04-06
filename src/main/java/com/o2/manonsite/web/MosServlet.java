/* Modification  History :
* Date          Version  Modified by     Brief Description of Modification
* 04-Sep-2007	 1.0					 Base version
* 01-Sep-2014    1.1       Indra         OSC 1054 - Improvements to PTW
* 29-OCt-2015	 1.2	   Indra 		 OSC1113 - Message Queue in MOS (and PTW)	                                                                                                   
*/
package com.o2.manonsite.web;
 
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.ParameterParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.common.Configuration;
import com.o2.common.Constants;
import com.o2.manonsite.broker.wintel.WintelBrokerConnection;
import com.o2.manonsite.mrs.MRSMessageHelper;
import com.o2.manonsite.mrs.MRSRequestInfo;
import com.o2.manonsite.mrs.MRSRequestInfoException;
import com.o2.manonsite.mrs.MRServiceClientStub;
import com.o2.manonsite.mrs.RequestContent;

/**
 * @author aademij1
 *
 * * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MosServlet extends HttpServlet{	 
	//public static final String MANONSITE = "manonsite";
	public static final String ENC = "UTF-8";
	private static final String ServletContext = "/maxim";
	/**
	 * 
	 */
	protected org.apache.xpath.CachedXPathAPI xpathAPI;
	private static final long serialVersionUID = 6912472015969175505L;
	private static final String MOS_PROXY_PASSWORD = "mos.proxy.password";
	private static final String MOS_PROXY_USER = "mos.proxy.user";
	private static final String MOS_PROXY_PORT = "mos.proxy.port";
	private static final String MOS_PROXY_HOST = "mos.proxy.host";
	private static final String MOS_PROXY_HOST_BACKUP = "mos.proxy.host.backup";
	private static final int MOS_PROXY_PORT_DEF = 80;
	private static final String MOS_PROXY_PASSWORD_DEF = "default";
	private static final String MOS_PROXY_USER_DEF = "default"; 
	public static final String GENERIC_RESPONSE_V1_DTD="response.dtd";	 
	private static final String MOS_TD_PASSWORD = "mos.tp.password";
	private static final String MOS_TD_USER = "mos.tp.user";
	/** 
	 * 
	 */    
	private static final Logger log = LoggerFactory.getLogger(MosServlet.class);	
    private static final String CONFIG_DIR = "./cfg";
    public static final String DTD_DIR="./dtd";
    private static final String ORB_NAME = "manonsite";
    private static final String DOMAIN_NAME = "artix";	
    private static final String WEB_PROPERTIES_FILE = "mos-servlets.properties";
  
	protected String mosUrlString;
    static String ServletRealPath;
    Properties  servletProps  = new Properties();
    static Properties netIdProps = new Properties();
    static Properties rtnCodeList = new Properties();
	ResourceBundle mosProperties;
	private Enumeration penum;	
	private String port;
	private String hostsList; 	
	final String HOSTDELIM = "=";
	private HashMap hostToMRSClientMap,hostnameToIPMap;
	private static final String FORWARDTO = "/o2mossender";
	final static String FRESPONSE = "ForwardingResponse";
	private WintelBrokerConnection brokerConnection;
	private Object mutex =  new Object(); 
	
	public void init(ServletConfig config) throws ServletException 
	{
		super.init(config); 
		log.debug("MosServlet.init()"); 
		ServletRealPath = getServletContext().getRealPath(DTD_DIR);
		log.debug("ServletRealPath = " + ServletRealPath); 

		//Get Resources
		/*  
        Use the method getResourceAsStream() from the ServletContext class. 
        This allows you update the file without having to reload the webapp as required by ResourceBundle
		 */
		InputStream is = getServletContext().getResourceAsStream(CONFIG_DIR + System.getProperty("file.separator") + WEB_PROPERTIES_FILE);
		InputStream netIDis = getServletContext().getResourceAsStream(CONFIG_DIR + System.getProperty("file.separator") + Constants.NetID_PROPERTIES_FILE);
		InputStream rtnIs = getServletContext().getResourceAsStream(CONFIG_DIR + System.getProperty("file.separator") + Constants.RtnCode_PROPERTIES_FILE);
		
		
		try { 
			//Upload the servlet properties file
			servletProps.load(is);	
			log.debug("servletProps = " + servletProps);
			is.close();		

			//UpLoad Network IDs 
			BufferedReader br=new BufferedReader(new InputStreamReader(netIDis)); 
			String inputLine;
			while ((inputLine = br.readLine()) != null)
			{
				//log.debug(inputLine);
				if(!inputLine.startsWith("#"))
				{	 
					String s [] = inputLine.split(",");
					netIdProps.setProperty(s[0], s[1]);
				}
			}
			
			log.debug("netIdProps = " + netIdProps);
			br.close(); 
			
			//Upload the return Codes file
			br=new BufferedReader(new InputStreamReader(rtnIs));  
			while ((inputLine = br.readLine()) != null)
			{
				//log.debug(inputLine);
				if(!inputLine.startsWith("#"))
				{	 
					String s [] = inputLine.split("=");
					rtnCodeList.setProperty(s[0].trim(), s[1].trim());
				}
			}
			
			log.debug("rtnCodeList = " + rtnCodeList);
			br.close();
					
			port = servletProps.getProperty(Configuration.MRS_WSDL_PORT);
			hostsList = servletProps.getProperty(Configuration.MRS_HOSTS);
			hostToMRSClientMap = new HashMap();
			hostnameToIPMap = new HashMap();
			String [] hostarr =hostsList.split(" ");
			log.debug("hostarr length:" + hostarr.length);
			
			for(int i = 0; i < hostarr.length; i++)
			{
				log.debug("splitting : " + hostarr[i]);
				if(hostarr[i].length() == 0 || hostarr[i].equals(" "))
				{
					continue;
				}					
				String [] tmpStr = hostarr[i].split(HOSTDELIM);
				log.debug("Creating an MRS remote Client for " + tmpStr[0].toUpperCase() + ":" + tmpStr[1]);
				try {		
					hostnameToIPMap.put(tmpStr[0],tmpStr[1]);
					hostToMRSClientMap.put(tmpStr[0], createMrsClient(tmpStr[1]));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					log.error("MRSRequestInfoException",e); 
				} 			
			}
			 
			brokerConnection = new WintelBrokerConnection(servletProps.getProperty(Configuration.WINTEL_BROKER_PASSWORD),
    		servletProps.getProperty(Configuration.WINTEL_BROKER_USER),servletProps.getProperty(Configuration.WINTEL_BROKER_URL));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ServletException(e);
		}  
	}  

	
	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	public void destroy() 
	{
		super.destroy();
		log.debug("MosServlet.destroy()");
		Collection coll = hostToMRSClientMap.keySet(); 
		
		Iterator  collectorIterator =coll.iterator();
		while(collectorIterator.hasNext()) 
		{
			/*Not sure if i need to close or deregister the Client. 
			  But can't find a situation method in the BUS API
			*/
			String akey = (String)collectorIterator.next();
			MRServiceClientStub tempMrs = (MRServiceClientStub)hostToMRSClientMap.get(akey); 
			hostToMRSClientMap.remove(akey);
		}
		hostToMRSClientMap=null;
		String id="MRSService";
		
		try {
			File tmp_dir = new File((String) (getServletContext().getAttribute("javax.servlet.context.tempdir")));  
			File [] tmp_files = tmp_dir.listFiles();
			String fname=null;
			for(int i=0;i < tmp_files.length;i++)
			{
				fname = tmp_files[i].getName();
				if(fname.startsWith(id))
				{ 
					log.debug("Removing " + fname);
					tmp_files[i].delete();
				}
			} 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Error:",e);
		}
	}


	protected Properties getPropertiesFromQueryString(HttpServletRequest request)
	{
		Properties attributes = new Properties();
		String messageId;
		String reqXML;
		String reqId;
		String username;
		String passwd;

		Enumeration aenum = request.getParameterNames();
		while (aenum.hasMoreElements())
		{
			String name = (String) aenum.nextElement();
			String value = request.getParameter(name); 
			attributes.put(name, value);
		}        
		return  attributes;
	}
	
	protected Properties getPropertiesAsXML(HttpServletRequest request) throws Exception
    {
		Properties attributes = new Properties();
         
        String messageId;
        String messageType;
        String sendDate;

        try
        {
            attributes = new Properties(); 
            StringBuffer sb = new StringBuffer();
            DataInputStream myInStream = new DataInputStream(request.getInputStream());            
			//Get the response from the servlet page.
            int ch;                
            while ((ch = myInStream.read()) != -1) {
                sb.append((char)ch);
            }
            myInStream.close(); 
            //log.debug("sb=" + URLDecoder.decode(sb.toString(),"UTF-8"));
            log.debug(" RAW request recived = " + sb.toString());
            String stmp = URLDecoder.decode(sb.toString(),MosServlet.ENC);
            log.debug(" Decoaded request  = " + stmp);
            stmp = stmp.trim();
            if(stmp.startsWith("?"))
            {   
            	log.debug("stmp:" + stmp);   
            	stmp=new StringBuffer(stmp).deleteCharAt(stmp.indexOf('?')).toString();
            	//log.debug("sb:" + sb);
            }
            //log.debug("stmp=" + stmp);
            ParameterParser parser =new ParameterParser() ; 
            java.util.Map params = parser.parse(stmp, '&');
            
            if(params == null)
            	log.debug("params is null");
            else            	 
            { 
            	log.debug("params == " + params);
            	Set keys = params.keySet();         // The set of keys in the map.
                Iterator keyIter = keys.iterator();
                /*System.out.println("The map contains the following associations:");
                while (keyIter.hasNext()) {
                	Object key = keyIter.next();  // Get the next key.
                	Object value = params.get(key);  // Get the value for that key.
                	log.debug( "   (" + key + "," + value + ")" );
                }*/

            	attributes.putAll(params);
            	attributes.list(System.out);
            }	
        }
        catch (Exception e)
        {
        	log.error("Error :",e);
            throw new Exception("Error extracting properties from XML input; details: " + e.getMessage(), e);
        }
        return  attributes;
    }
	
	boolean validateCredentials(Properties attributes)
	{		 
		if((((String)attributes.get(Constants.USER_PARAM))).equals(servletProps.getProperty(MOS_TD_USER)) && 
		 ((String)attributes.get(Constants.PASS_PARAM)).equals(servletProps.getProperty(MOS_TD_PASSWORD)))
		{
			return true;
		} 
		return false;
	}
	private void processInputStream(HttpServletRequest req, HttpServletResponse res,Properties attributes ) throws Exception
	{
		
	    log.debug("req: " +req.toString());	
		penum = servletProps.propertyNames();
	    String sessionId = req.getSession().getId(); 	
		MosExpert be =null;
		res.setContentType("text/xml");
		res.setCharacterEncoding(ENC);
		PrintWriter out = res.getWriter();
	//	log.debug("Estoy en processInputStream en mosServlet. ABI");
		be = new MosExpert(sessionId);
		String tpResponse =""; //HTTP Response in format response_generic_v1.dtd used to acknowledge receipt of request from WINTEL
		
		boolean successRetry = false;
		int maxNumberRetries = 4;
		int numberRetries = 1;
		int retryTimeInterval = 5000; //milliseconds
		
		if(!validateCredentials(attributes))
		{ 
			tpResponse=be.generateGenericXMLResponse(302);
			out.println(tpResponse);
			out.flush();
		}
		else
		{
			String xmlInput = ((String)attributes.get(Constants.TP_XML)).replaceAll("> *<","><");
			//log.debug("[XML Request] : " + xmlInput);
			
			List mySMSes  = be.DOMValidateDTD(xmlInput);
			log.debug("Called DOMValidateDTD");	
			String smsResponse=""; //XML Payload Response sent from WINTEL
			try
			{
				if(mySMSes != null)
				{ 
					if(be.getXmlStatusCode() != 0)
					{
						tpResponse =be.generateGenericXMLResponse(be.getXmlStatusCode());
						out.println(tpResponse);
						//out.println(URLEncoder.encode(tpResponse,MosServlet.ENC));
						out.flush();
					}
					else
					{
						log.debug("No of SMS Messages '" + mySMSes.size() + "'.");						
						Iterator it = mySMSes.iterator();
						while(it.hasNext()) 
						{
							Smstotp smsPkt=(Smstotp)it.next(); 
							log.debug(smsPkt.toString());

							log.debug("[BEGIN Process] : " + smsPkt.getWintransID());
							
							//Reset From int to textual Description for NetworkID
							String netIDStr = netIdProps.getProperty(smsPkt.getNetworkID());			
							smsPkt.setNetworkID(netIDStr.toUpperCase());

							//TODO This should be based on the value of DTD SystemId as both  tpbound_messages_v1.dtd 
							//     and tpbound_receipts_v2.dtd are possible to be sent.
							if(smsPkt.getServID() == Integer.parseInt(WintelBrokerConnection.ServiceDesp[0][0]))
							{
								log.debug("This should be WIN Test Service");  								 
							}							  
							String tmpStr = smsPkt.getText();
							if(tmpStr.toLowerCase().startsWith(WintelBrokerConnection.ServiceDesp[1][1].toLowerCase()))
							{
								log.debug("This a confirmed ManOnsite Test Message"); 								
								smsPkt.setText(tmpStr.substring(WintelBrokerConnection.ServiceDesp[1][1].length()).trim());
							}
							tpResponse=be.generateGenericXMLResponse(0);
							log.debug("TPBOUND Response:" + tpResponse);							
							out.println(/*URLEncoder.encode(*/tpResponse/*,MosServlet.ENC)*/);
							out.flush();
							
							
							synchronized (mutex ) {
								
								log.debug("[BEGIN synchronized block] : " + smsPkt.getWintransID());
								log.debug("Get MRS Client"); 
								MRServiceClientStub mrsService = getMrsClient(smsPkt.getText());
								String repltTxt= "Cannot connect to the ManOnsite Application at the moment; Please try again later";							
								if(smsPkt.getText() != null && mrsService != null)
								{ 		
									
									try
									{
										log.debug("[BEGIN MRSService.getContent() <" + smsPkt.getWintransID() + ">]");
										
										
										
										RequestContent var = MRSMessageHelper.createRequest(smsPkt.toProperties());
																				
										RequestContent resp = mrsService.getContent(var);

										log.debug("[END MRSService.getContent() <" + smsPkt.getWintransID() + ">]");
										
										repltTxt = MRSMessageHelper.getRequestMessage(resp);
										
										log.debug("MRSMessageHelper.getRequestMessage(resp): "+repltTxt);
										
									}
									catch(RemoteException re)
									{										 
										log.error("Getting a Remote Connection Exception. Is the ManOnsite App Dead?",re); 	
										log.error("ReCreating Client Proxy");
										
										while(!successRetry && numberRetries <= maxNumberRetries){
											log.error("sleeping ...attempt: "+String.valueOf(numberRetries));
		                                    Thread.sleep(retryTimeInterval);
		                                    log.error("waking up retryng connections");
		                                    
		                                    setMrsClient(smsPkt.getText());									
											log.error("Getting Client Proxy again");
											mrsService = getMrsClient(smsPkt.getText());
											try{
															
												log.debug("[BEGIN RETRY MRSService.getContent() <" + smsPkt.getWintransID() + ">]");
												
												RequestContent var = MRSMessageHelper.createRequest(smsPkt.toProperties());

												
												RequestContent resp = mrsService.getContent(var);

												log.debug("[END RETRY MRSService.getContent() <" + smsPkt.getWintransID() + ">]");
												
												repltTxt = MRSMessageHelper.getRequestMessage(resp);
												
												log.debug("RETRY: MRSMessageHelper.getRequestMessage(resp): "+repltTxt);
												
												
												successRetry = true;
											}
											catch(Exception re_r)
											{	
												log.error("Getting a Remote Connection Exception...attempt: "+String.valueOf(numberRetries)+". Is the ManOnsite App Dead?",re_r); 	
												log.error("ReCreating Client Proxy...again");
												
												numberRetries++;
											}
										}
									}
									finally
									{
										try
										{
											
											log.debug("BEGIN DISPATCHING TO SERVLET, ID:"  + smsPkt.getWintransID());
											smsPkt.setReplyText(repltTxt);
											req.setAttribute(MosServlet.FRESPONSE,smsPkt);							    	    
											dispatchToServlet(req, res);
											log.debug("END DISPATCHING TO SERVLET, ID:"  + smsPkt.getWintransID());
											//return;
											//smsResponse = brokerConnection.sendSimpleSMS(smsPkt.getSource_addr(), repltTxt);
											//log.debug("Response from Wintel: " + smsResponse);
										}
										catch(Exception e)
										{
											log.error("Error while sending SMS to Broker",e);
										}
									}															
								}
								else
								{
									if(smsPkt.getText() == null)
										log.error("Message is null");								
									if(mrsService == null)
										log.error("Can't retrieve a remote Client");
								}
								log.debug("[END synchronized block] : " + smsPkt.getWintransID());
							}// synchronized
							
							log.debug("[END Process] : " + smsPkt.getWintransID());
						}
					}
				}
				else
				{
					log.debug("Unable to retrieve TP XML");
					tpResponse=be.generateGenericXMLResponse(2003);
					out.println(tpResponse);
					out.flush();
				}
			}			
			catch(Exception e)
			{
				log.error("Whats up",e);
			}			
		}
	}
	

	
	/*
	 Forward will transfer the control to the target resource (servlet or jsp), 
	 without returning the control to the caller
	 Include will transfer the control to the target resource (servlet or jsp), 
	 but then returning the control to the caller after the execution.
	*/
	private void dispatchToServlet(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException, ServletException
	{
    	if(this.getServletContext() == null)
        {
    		String str="Can't get Servlet context";
    		log.error(str);
        	throw new ServletException(str);
        }
    	
        ServletContext scc = this.getServletContext().getContext(ServletContext);
        RequestDispatcher dispatcher = null;
        if(scc != null)
        {     
        	dispatcher =scc.getRequestDispatcher(FORWARDTO);
    	    if (dispatcher != null) 
        		dispatcher.include(request, response);
    	    else
    	    {
    	    	String str="Can't get contextRequestDispatcher";
            	log.error(str);
            	throw new ServletException(str);
    	    }
        }
        else
        {
        	String str="Can't get context, ensure that have maxim.xml is copied into " +
        			"$CATALINA_BASE/conf/Catalina/localhost/"; 
        	log.error(str);
        	throw new ServletException(str);
        }
	}
	
	
	MRServiceClientStub getMrsClient(String text) throws MRSRequestInfoException 
	{		 				           
		MRSRequestInfo requestInfo = new MRSRequestInfo(text);        	
		String hostprefix = requestInfo.getPrefix(); 
		if(hostprefix == null || hostprefix.trim().length() <= 0)
		{
			log.info("Defaulting to LIVE");
			hostprefix="liv";
		}
				
		//Get Artix Client		
		log.info("Get MRS Client Proxy for " + hostprefix);
		MRServiceClientStub mrsServiceRemote  =(MRServiceClientStub) hostToMRSClientMap.get(hostprefix.toLowerCase());
		return mrsServiceRemote;
	}
	 
	void setMrsClient(String text) throws MRSRequestInfoException 
	{		 				           
		MRSRequestInfo requestInfo = new MRSRequestInfo(text);        	
		String hostprefix = requestInfo.getPrefix(); 
		if(hostprefix == null)
		{
			log.info("Defaulting to LIVE");
			hostprefix="liv";
		}				
		
		hostprefix = hostprefix.toLowerCase(); 
		//Set Artix Client
		log.info("Set MRS Client Proxy for" + hostprefix);
		try {
			hostToMRSClientMap.put(hostprefix, (MRServiceClientStub)createMrsClient((String)hostnameToIPMap.get(hostprefix)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("MalformedURLException");
		} 
	}
	
	
	
	private MRServiceClientStub createMrsClient(String ipStr) 
	{
		
		MRServiceClientStub mrServiceClientStub = new MRServiceClientStub(ipStr , port);
		return mrServiceClientStub;
	}
	
	public void doPost( HttpServletRequest req, HttpServletResponse res ) 
	throws IOException, ServletException
	{
		
		try
		{
			Properties attributes = getPropertiesAsXML(req);
			/*Enumeration headerNames = req.getHeaderNames();
		    while(headerNames.hasMoreElements()) {
		      String headerName = (String)headerNames.nextElement();
		      log.debug(headerName + "=" + req.getHeader(headerName));
		    }*/

			
			processInputStream(req,res,attributes);				
		}
		catch(Exception e1 )
		
		{
		log.error("Error:",e1);
		throw new ServletException(e1);
		}	
	}	 
	 
	public void doGet( HttpServletRequest req, HttpServletResponse res ) 
	throws IOException, ServletException
	{
		
		try
		{
			Properties attributes = getPropertiesFromQueryString(req);
			processInputStream(req,res,attributes);		
		}
		catch(Exception e1 )
		
		{
		log.error("Error:",e1);
		throw new ServletException(e1);
		}	
	}	 
}
