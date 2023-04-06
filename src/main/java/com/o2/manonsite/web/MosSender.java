/*
 * Created on 04-Sep-2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.o2.manonsite.web;
 
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.o2.common.Configuration;
import com.o2.manonsite.broker.wintel.WintelBrokerConnection;
/**
 * @author aademij1
 *
 * * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MosSender extends HttpServlet{	
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
	private static final String MOS_REMOTESERVICE_URL = "mos.remoteservice.url";  
	public static final String GENERIC_RESPONSE_V1_DTD="response.dtd";	 
	private static final String MOS_TD_PASSWORD = "mos.tp.password";
	private static final String MOS_TD_USER = "mos.tp.user";
	/** \
	 * 
	 */    
	private static final Logger log = LoggerFactory.getLogger(MosSender.class);	
    private static final String CONFIG_DIR = "./cfg";
    public static final String DTD_DIR="./dtd";
    private static final String ORB_NAME = "manonsite";
    private static final String DOMAIN_NAME = "artix";	
    private static final String WEB_PROPERTIES_FILE = "mos-servlets.properties";
    
     
    static String ServletRealPath;
    Properties  servletProps  = new Properties();    
	Properties systemProperties;
	private WintelBrokerConnection winBroker;
	
	public void init(ServletConfig config) throws ServletException 
	{
		super.init(config); 
		systemProperties = System.getProperties();
		log.debug("MosServlet.init()"); 
		ServletRealPath = getServletContext().getRealPath(DTD_DIR);

		
		//Get Resources
		/*  
        Use the method getResourceAsStream() from the ServletContext class. 
        This allows you update the file without having to reload the webapp as required by ResourceBundle
		 */
		InputStream is = getServletContext().getResourceAsStream(CONFIG_DIR + System.getProperty("file.separator") + WEB_PROPERTIES_FILE);

		try { 
			servletProps.load(is);	
			is.close();		

			//Set WINTEL Config
			String password = servletProps.getProperty(Configuration.WINTEL_BROKER_PASSWORD);
			String host = servletProps.getProperty(Configuration.WINTEL_BROKER_URL);
			String username=servletProps.getProperty(Configuration.WINTEL_BROKER_USER);
			winBroker = new WintelBrokerConnection(password,username,host);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.error("IOException:" ,e );
			throw new ServletException(e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("Exception:" ,e );
		}       

	}    

		
	protected String getStreamData(HttpServletRequest request) throws Exception
	{
		String sbuffer =null;

		try
		{     
			log.debug("getStreamData(HttpServletRequest request) ");
			
			 ByteArrayOutputStream bOut = new ByteArrayOutputStream();
			 InputStream in = request.getInputStream();
				byte[] buffer = new byte[4096];
             int totalSizse=0;
				int length;
				while((length = in.read(buffer)) > 0) {
					bOut.write(buffer, 0, length);
					totalSizse = totalSizse+ length;
				}
				in.close();
			log.debug(" Number of bytes recived  = " + totalSizse);	
			sbuffer  = new String(bOut.toByteArray());			 			
			log.debug("Request Recieved from ManOnSite Application : " + URLDecoder.decode(sbuffer,"UTF-8"));             
		}
		catch (Exception e)
		{
			log.error("Error :",e);
			throw new Exception("Error extracting properties from XML input; details: " + e.getMessage(), e);
		}
		return  sbuffer;
	}	
	
	private String processInputStream(Smstotp smsPKT) throws Exception
	{
		//Send request to Wintel
		String response = null;
		try
		{    
			log.debug("Phone number: " + smsPKT.getSource_addr());
			log.debug("message: " + smsPKT.getReplyText());
			log.debug("shortcode: " + smsPKT.getDest_addr());
			 response = winBroker.sendSimpleSMS(smsPKT.getSource_addr(), smsPKT.getReplyText(), smsPKT.getDest_addr());			
			 		 
		}
		catch(Exception e)
		{
			log.error("Error:",e);
		}
		return response;
	}
	
	private String processInputStream(String sbuffer) throws Exception
	{
		//Send request to Wintel
		String response = null;
		try
		{
			//response = winBroker.SendHttpRequest(sbuffer);	
			response = winBroker.sendApacheCommonsHTTPClientReq(sbuffer);
		}
		catch(Exception e)
		{
			log.error("Error in MosSender processInputStream:",e);
		}
		return response;
	}
	public void doPost( HttpServletRequest req, HttpServletResponse res ) 
	throws IOException, ServletException
	{

		try
		{
			Smstotp smsPKT= (Smstotp)req.getAttribute(MosServlet.FRESPONSE);
			String smsResponse=null;
			String smsReply = null;
			if(smsPKT == null)
			{
				log.debug("getStreamData()" );
			 	smsResponse = getStreamData(req);
			 	smsReply= processInputStream(smsResponse);
			}
			else
			{
				log.debug("processInputStream" );
				smsReply= processInputStream(smsPKT);
			}
			if(Pattern.matches(".*<REQUESTID>(.*)</REQUESTID>.*", smsReply))
			{ 
				int wTblNo=0;
				int wcolDb=0; 
				int wRetCode=0;
				//log.debug("found");
				Pattern	httpPtm = Pattern.compile("HTTP_T(\\d)_ID(\\d+)+_R(\\d+)");
				Matcher m = httpPtm.matcher(smsReply); 
				try
				{
					/*
					 * 	x = table number (32bit int)
	   								y = Identity column on WIN database 
	         						( x and y uniquely identifies the submission to WIN) (32 bit integer)
	   								z = 'R' Return Status Code (32bit int)
					 */
					if(m.find())
					{
						wTblNo = Integer.parseInt(m.group(1));
						wcolDb= Integer.parseInt(m.group(2)); 
						wRetCode=Integer.parseInt(m.group(3));	 
						if(wRetCode != 0)
						{
							String s =(String)(MosServlet.rtnCodeList.getProperty(Integer.toString(wRetCode)));
							log.error("IMPORTANT:SMS" + 
									" from Table " +  wTblNo + " and Column " + wcolDb + " on the WIN database has reported errorcode " + wRetCode +
									" which translates to \"" + s + "\"") ;
						}	
						else 
							log.debug("SMS Send Successful");
					}
				}
				catch(Exception ne)
				{
					log.error("Exception ",ne); 
				} 
			}							
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
			
			String sbuffer = getStreamData(req);
			log.debug("sbuffer: " + sbuffer);
			processInputStream(sbuffer);		
		}
		catch(Exception e1 )
		
		{
		log.error("Error:",e1);
		throw new ServletException(e1);
		}	
	}
}
