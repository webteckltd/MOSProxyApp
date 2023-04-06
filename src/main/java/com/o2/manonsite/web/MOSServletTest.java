package com.o2.manonsite.web;

import java.io.IOException;
 
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.UnknownHostException;
 
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import  com.o2.common.Constants;
import com.o2.manonsite.broker.wintel.WintelBrokerConnection;
/**
 * @author 
 */
public class MOSServletTest
{
    //private static Logger log = Logger.getLogger(MOSServletTest.class);

    private static final String SUCCESS = "Success";
    private static final String FAILURE = "Failed"; 
    private static final String MOS_PAGE= "/maxim/o2mosservlet";
    private String sUser=null;
    private String sPwd=null;
    private String webServer=null;
    private String sourceLoc=null;
    private SimpleDateFormat troubleTicketDateFormat;

    HttpURLConnection connection;
    String theUrl="http://"; 
     
    String Qstr ="?USER=abc&Password=def&" +
    		"TP_XML=%3c%3fxml+version%3d%221.0%22+encoding%3d%22utf-8%22+standalone%3d%22no%22%3f%3e%3c!DOCTYPE+WIN_RECEIPTS+SYSTEM+%22tpbound_receipts_v2.dtd%22%3e%3cWIN_RECEIPTS%3e%3cSMSRECEIPT%3e%3cSERVICEID%3e5%3c%2fSERVICEID%3e%3cSOURCE_ADDR%3e%2b447740630138%3c%2fSOURCE_ADDR%3e%3cTRANSACTIONID%3e111222333%3c%2fTRANSACTIONID%3e%3cSTATUSID%3e3%3c%2fSTATUSID%3e%3cSTATUSDATETIME%3e%3cDD%3e19%3c%2fDD%3e%3cMMM%3eNOV%3c%2fMMM%3e%3cYYYY%3e2004%3c%2fYYYY%3e%3cHH%3e10%3c%2fHH%3e%3cMM%3e16%3c%2fMM%3e%3cSS%3e7%3c%2fSS%3e%3c%2fSTATUSDATETIME%3e%3cTOTALFRAGMENTNO%3e1%3c%2fTOTALFRAGMENTNO%3e%3cFRAGMENTID%3e1%3c%2fFRAGMENTID%3e%3c%2fSMSRECEIPT%3e%3c%2fWIN_RECEIPTS%3e&RequestID=1_5836067&";
    
    /* String Qstr ="?USER=ABC&Password=DEF&" +
	     "TP_XML=%3c%3fxml+version%3d%221.0%22+encoding%3d%22iso-8859-1%22+standalone%3d%22no%22%3f%3e%3c!DOCTYPE+WIN_TPBOUND_MESSAGES+SYSTEM+%22tpbound_messages_v1.dtd%22%3e%3cWIN_TPBOUND_MESSAGES%3e%3cSMSTOTP%3e%3cSOURCE_ADDR%3e%2b447834571893%3c%2fSOURCE_ADDR%3e%3cTEXT%3e33+in%3c%2fTEXT%3e%3cWINTRANSACTIONID%3e357613334%3c%2fWINTRANSACTIONID%3e%3cDESTINATION_ADDR%3e82222%3c%2fDESTINATION_ADDR%3e%3cSERVICEID%3e2%3c%2fSERVICEID%3e%3cNETWORKID%3e2%3c%2fNETWORKID%3e%3cARRIVALDATETIME%3e%3cDD%3e19%3c%2fDD%3e%3cMMM%3eNOV%3c%2fMMM%3e%3cYYYY%3e2004%3c%2fYYYY%3e%3cHH%3e10%3c%2fHH%3e%3cMM%3e41%3c%2fMM%3e%3c%2fARRIVALDATETIME%3e%3c%2fSMSTOTP%3e%3c%2fWIN_TPBOUND_MESSAGES%3e&RequestID=1_5837272&";
	*/
    
    
    private Properties systemProperties;
     
    public static void printUsageAndExit(String exeName)
    {
        System.out.println("Usage: " + exeName);
        System.out.println("    -user <username>  Specify the username already registered with Tomcat container");
        System.out.println("    -pwd  <password>  password");
        System.out.println("    -host <Tomcat host server>");
        System.out.println("    -source <File/Directory of XML>");
        System.exit(1); 
    }
	/**
	 * @return the sUser
	 */
	public String getSUser() {
		return sUser;
	}
	/**
	 * @param user the sUser to set
	 */
	public void setSUser(String user) {
		sUser = user;
	}
	/**
	 * @return the sPwd
	 */
	public String getSPwd() {
		return sPwd;
	}
	/**
	 * @param pwd the sPwd to set
	 */
	public void setSPwd(String pwd) {
		sPwd = pwd;
	}
	/**
	 * @return the webServer
	 */
	public String getWebServer() {
		return webServer;
	}
	/**
	 * @param webServer the webServer to set
	 */
	public void setWebServer(String webServer) {
		this.webServer = webServer;
	}
	/**
	 * @return the theUrl
	 */
	public String getTheUrl() {
		return theUrl;
	}
	/**
	 * @param theUrl the theUrl to set
	 */
	public void setTheUrl(String theUrl) {
		this.theUrl+=webServer + MOS_PAGE; 
	}
	/**
	 * @return the qstr
	 */
	public String getQstr() {
		return Qstr;
	}
	/**
	 * @param qstr the qstr to set
	 */
	public void setQstr(String qstr) {
		Qstr = qstr;
	}
	 
	 
    /**
     * 
     */
    public  MOSServletTest()
    {
        super(); 
        systemProperties = System.getProperties();        
    }

    public static void main(String args[])
    {
    	/* Try with BASIC authentication */
    	String user=null;
    	String pwd=null;
    	String wServer=null;
		try 
		{
			 int i = 0; 
			 MOSServletTest mosTest = new MOSServletTest(); 
		     while (i < args.length) 
		     {
		    	 //System.out.println("Arg[" + i + "] = " + args[i]);
			     if (args[i].equals("-user")) 
			     {
			         i++;
			         if (i <= args.length) {
			        	 mosTest.setSUser(args[i]);
			             user = args[i];
			         }
			     }
			     else if (args[i].equals("-pwd")) 
			     {
			         i++;
			         if (i <= args.length) {
			        	 mosTest.setSPwd(args[i]);
			             pwd = args[i];
			         }
			     }
			     else if (args[i].equals("-host")) 
			     {
			         i++;
			         if (i <= args.length) {
			        	mosTest.setWebServer(args[i]);
			         	wServer = args[i];
			         }
			     }
			     else if (args[i].equals("-source")) 
			     {
			         i++;
			         if (i <= args.length) {			        	 
			        	 mosTest.setSourceLoc(args[i]);
			         }
			     }
			     else if (args[i].equals("-h") || args[i].equals("-help")){
	                printUsageAndExit("MOSServletTest");
	            }
			     //System.out.println("Arg[" + i + "] = " + args[i]);
			     i++;
		     }
		     if(mosTest.getWebServer() == null)
		     {
		    	 System.out.println("Wed Host Server is not set, using isisint as default");
		    	 mosTest.setWebServer("isisdev:6294");
		     }
		         
		        //connectProxyServer(); 
		        mosTest.setTheUrl(mosTest.getWebServer());
				//theUrl="http://gateway3.go2mobile.net:10030/gateway/v3/gateway.aspx";
				//theUrl="http://www.o2.co.uk/";
				System.out.println(mosTest.getTheUrl());
		     
			    mosTest.processQuery();
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			System.out.println("MalformedURLException:" + e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Exception:" + e);
		}      		 
    }
        
    static String proxyIP = "";
	static int proxyPort = 8080;
	static boolean autoDetectProxy = false;  // If SSL version, assume autoDetect, else do not
	static boolean useProxy = false;
	
	
	void detectProxy(URL sampleURL) {
	    String javaVers =systemProperties.getProperty("java.version");
	    System.out.println("About to attempt auto proxy detection under Java version:"+javaVers);
	    boolean invokeFailover = false; // If specific, known detection methods fail may try fallback detection method
	     
	    if (javaVers.startsWith("1.4"))  {
	        try {
	            //  Look around for the 1.4.X plugin proxy detection class... Without it, cannot autodetect...
	            Class t = Class.forName("com.sun.java.browser.net.ProxyService");
	            
	            com.sun.java.browser.net.ProxyInfo[] pi = com.sun.java.browser.net.ProxyService.getProxyInfo(sampleURL);
	            if (pi == null || pi.length == 0) {
	            	System.out.println("1.4.X reported NULL proxy (no proxy assumed)");
	                useProxy = false;
	            }
	            else {
	            	System.out.println("1.4.X Proxy info geProxy:"+pi[0].getHost()+ " get Port:"+pi[0].getPort()+" isSocks:"+pi[0].isSocks());
	                useProxy = true;
	                proxyIP = pi[0].getHost();
	                proxyPort = pi[0].getPort();
	                System.out.println("proxy " + proxyIP+" port " + proxyPort);
	            }	            
	        }
	        catch (Exception ee) {
	        	ee.printStackTrace();
	        	System.out.println("Sun Plugin 1.4.X proxy detection class not found, will try failover detection, e:"+ee);
	            invokeFailover = true;
	        }
	    }
	    else {
	    	System.out.println("Sun Plugin reported java version not 1.3.X or 1.4.X, trying failover detection...");
	        invokeFailover = true;
	    }
	    if (invokeFailover) {
	    	System.out.println("Using failover proxy detection...");
	         try {
	            String proxyList = ((String)System.getProperties().getProperty("javaplugin.proxy.config.list")).toUpperCase();
	            System.out.println("Plugin Proxy Config List Property:"+proxyList);
	            useProxy = (proxyList != null);
	            if (useProxy) {     
	            	//  Using HTTP proxy as proxy for HTTP proxy tunnelled SSL socket (should be listed FIRST)....
	                // 6.0.0 1/14/03 1.3.1_06 appears to omit HTTP portion of reported proxy list... Mod to accomodate this...
	                // Expecting proxyList of "HTTP=XXX.XXX.XXX.XXX:Port" OR "XXX.XXX.XXX.XXX:Port" & assuming HTTP...
	                if (proxyList.indexOf("HTTP=") > -1)
	                     proxyIP = proxyList.substring(proxyList.indexOf("HTTP=")+5, proxyList.indexOf(":"));
	                else proxyIP = proxyList.substring(0, proxyList.indexOf(":"));
	                int endOfPort = proxyList.indexOf(",");
	                if (endOfPort < 1) endOfPort = proxyList.length();
	                proxyPort = Integer.parseInt(proxyList.substring(proxyList.indexOf(":")+1,endOfPort));
	                System.out.println("proxy " + proxyIP+" port " + proxyPort);
	            }
	        }
	        catch (Exception e) {
	            System.out.println("Exception during failover auto proxy detection, autoDetect disabled, e:"+e);
	            autoDetectProxy = false;
	        }
	    }
	}
	public String pingHttp(String host, int port) throws Exception 
    { 
        PrintWriter output; 
        InputStream input; 
        StringBuffer response = new StringBuffer(); 
        try 
        { 
        	if(port== -1)
        		port=80;
            Socket httpSocket = new Socket(host, port); 
            // Timeout after 5 seconds of trying to talk over socket. 
            httpSocket.setSoTimeout(5000); 
            output = new PrintWriter(httpSocket.getOutputStream(), false); 
            input = httpSocket.getInputStream(); 
            output.print("OPTIONS * HTTP/1.1\nHost: " + host + "\nUser-Agent: ManOnSite Diagnostics\r\n\r\n"); 
            output.flush(); 
             
            // Read maximum of 1k of data as we don't really care what this says. 
            byte[] b = new byte[1024]; 
            int n = input.read(b); 
            response.append(new String(b, 0, n)); 
            output.close(); 
            input.close(); 
            httpSocket.close(); 
            System.out.println("Response:" + response); 
        } 
        catch (UnknownHostException e) 
        { 
            throw new Exception("Could not resolve the host \"" + host + "\"",e); 
        } 
        catch (SocketTimeoutException e) 
        { 
            throw new Exception("The host \"" + host + "\" did not respond in a timely manner"); 
        } 
        catch (IOException e) 
        { 
            throw new Exception("Could not connect to port " + port +" on \"" + host + "\"",e); 
        } 
        return response.toString(); 
    } 
	void connectProxyServer()
	{		
		//http.nonProxyHosts		 
		systemProperties.setProperty("http.proxySet","true");
		systemProperties.setProperty("http.proxyHost","bluecoat.uk.pri.o2.com"); 			 
		systemProperties.setProperty("http.proxyPort","80"); 
		systemProperties.setProperty("http.nonProxyHosts","localhost|isisdev");	
		// systemProperties.list(System.out);
	}
	/**
	 * Establish and set up the URL connection
	 *
	 * @param theUrl java.lang.String = the POST URL
	 * @return HttpURLConnection
	 */
	private HttpURLConnection getUrlConnection(String theUrl) {
		
		//Create a URL connection 
		java.net.HttpURLConnection conn = null;
		java.net.URL conwayUrl;
		try {
			conwayUrl = new java.net.URL(theUrl);
			conn = (HttpURLConnection) conwayUrl.openConnection();
		    //Setup connection parameters
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());			
		}
		
		// Set the headers correctly (URL encoding, authentication) 
		conn.setRequestProperty("Content-type", "text/html");
		/*if(sUser != null && sPwd != null)
        {
			String authString = sUser + ":" + sPwd; //change this to bssservlet			 
		    //String auth = "Basic " + new sun.misc.BASE64Encoder().encode(authString.getBytes());
			conn.setRequestProperty("Authorization", authString); 
        }	*/	
		return conn;
		
	}

	private void processQuery( ) throws Exception
	{

		String respStr []=null;
		if(this.getSourceLoc() != null)
		{
			File sFile = new File(this.getSourceLoc());
			String[] ffs = null;
			if(sFile.exists() && sFile.isDirectory() && sFile.canRead())
			{
				System.out.println("Source Location is a Directory and its readable");
				ffs = sFile.list();

			}
			else if(sFile.exists() && sFile.isFile() && sFile.canRead())
			{
				System.out.println(this.getSourceLoc() +  " its readable");
				ffs = new String[1];
				ffs[0]=this.getSourceLoc();				 
			}
			else
			{
				System.out.println("Cannot process Souce Location. Does it exists and Is it either a directory/file and readable?");
				return;
			}
			
			String inputLine;
			respStr = new String[ffs.length];
			for (int i=0; ffs != null  &&  i<ffs.length; i++) 
			{
				if(ffs[i].toLowerCase().endsWith(".xml"))
				{
					System.out.println(new File(ffs[i]).getName());
					InputStream is =ClassLoader.getSystemClassLoader().getResourceAsStream(new File(ffs[i]).getName());
					if (is == null) {
			          throw new IOException("Can't read file xml on classpath: " + ffs[i]);
					}
					BufferedReader br=new BufferedReader(new InputStreamReader(is)); 				
					StringBuffer inStr=new StringBuffer();
					while ((inputLine = br.readLine()) != null)				
					{				 
					    inStr.append(inputLine); 
					}
					respStr[i]=URLEncoder.encode("?USER=O55app&Password=o2fm!&" + "TP_XML=" + inStr.toString(),MosServlet.ENC); 
					br.close();
					is.close();
				}
			}
		}
		else
		{
			respStr=new String[1];
			Qstr ="?USER=O55app&Password=o2fm!&" +
            "TP_XML=%3c%3fxml+version%3d%221.0%22+encoding%3d%22utf-8%22+standalone%3d%22no%22%3f%3e%3c!DOCTYPE+WIN_TPBOUND_MESSAGES+SYSTEM+%22tpbound_messages_v1.dtd%22%3e%3cWIN_TPBOUND_MESSAGES%3e%3cSMSTOTP%3e%3cSOURCE_ADDR%3e%2b447778045512%3c%2fSOURCE_ADDR%3e%3cTEXT%3eManonsite%3c%2fTEXT%3e%3cWINTRANSACTIONID%3e488146111%3c%2fWINTRANSACTIONID%3e%3cDESTINATION_ADDR%3e62946%3c%2fDESTINATION_ADDR%3e%3cSERVICEID%3e3%3c%2fSERVICEID%3e%3cNETWORKID%3e1%3c%2fNETWORKID%3e%3cARRIVALDATETIME%3e%3cDD%3e22%3c%2fDD%3e%3cMMM%3eJUN%3c%2fMMM%3e%3cYYYY%3e2008%3c%2fYYYY%3e%3cHH%3e10%3c%2fHH%3e%3cMM%3e40%3c%2fMM%3e%3c%2fARRIVALDATETIME%3e%3c%2fSMSTOTP%3e%3c%2fWIN_TPBOUND_MESSAGES%3e";
   
			System.out.println("sb=" + URLDecoder.decode(Qstr,"UTF-8"));
			respStr[0] = new String(Qstr);
		}

		HttpURLConnection conwayConn = getUrlConnection(theUrl);

		//detectProxy( conwayConn.getURL()); 
		DataOutputStream myOutStream = null;
		DataInputStream myInStream = null;
		StringBuffer sb = new StringBuffer();
		for (int i=0; respStr != null  &&  i<respStr.length; i++) 
		{
			try {
				
				myOutStream = new DataOutputStream(conwayConn.getOutputStream()); 
				myOutStream.writeBytes(respStr[i]);
				myOutStream.flush();
				myOutStream.close();
				// Test the URL Connection for success
				System.out.println("host/port=" + conwayConn.getURL().getHost() + "/" + conwayConn.getURL().getPort());			
				if (!conwayConn.getResponseMessage().equals("OK")) {
					String expStr = "Error" + "URL " + conwayConn.getURL().toExternalForm()  
					+ ": " + conwayConn.getResponseMessage() + "(" +conwayConn.getResponseCode() + ")";				
					System.out.println(expStr);
				}
				else
				{
					myInStream = new DataInputStream(conwayConn.getInputStream()); 
					//Get the response from the servlet page.
					int ch;                
					while ((ch = myInStream.read()) != -1) {
						sb.append((char)ch);
					}
					myInStream.close();  
					System.out.println("OUTPUT[" + i + "]"+ sb);
				}
				//pingHttp(conwayConn.getURL().getHost(), conwayConn.getURL().getPort()); 
			} 
			catch (UnknownHostException e) 
			{  
				System.out.println("Could not resolve the host : " + e.getMessage()); 
			}
			catch (IOException ioe) 
			{
				System.out.println("IOException" +ioe.getMessage()); 
			}

			
		}		 
	}
	 
	 String[] monthName = {"JAN", "FEB","MAR", "APR", "MAY", "JUN", "JUL","AUG", "SEP", "OCT", "NOV","DEC"};
	
    protected String generateTPXMLMessage(String dtdFileName, String msisdn, String msg)
    {        
        Random generator = new Random();
        long radNum = Math.abs(generator.nextLong() % 10000);
        Date dd = new Date(); 
        Calendar cc = Calendar.getInstance();
		return "<?xml version='1.0' standalone='no'?>" + "<!DOCTYPE WIN_TPBOUND_MESSAGES SYSTEM '" + dtdFileName  + "'>"   
        		+ "<WIN_TPBOUND_MESSAGES>" + "<SMSTOTP>" + "<SOURCE_ADDR>" + msisdn + "</SOURCE_ADDR>" + "<TEXT>" 
        		+ msg +"</TEXT>" + "<WINTRANSACTIONID>" + radNum + "</WINTRANSACTIONID>" + "<DESTINATION_ADDR>" 
        	+ Constants.WINTEST_SHORTCODE + "</DESTINATION_ADDR>" + "<SERVICEID>" + WintelBrokerConnection.MAXIM_SERVID + "</SERVICEID>" + "<NETWORKID>" + "2" 
        	+ "</NETWORKID>" + "<ARRIVALDATETIME>" + "<DD>" + cc.get(Calendar.DAY_OF_WEEK) + "</DD>" + "<MMM>" + monthName[cc.get(Calendar.MONTH)]
            + "</MMM>" + "<YYYY>" + cc.get(Calendar.YEAR) + "</YYYY>" + "<HH>" + cc.get(Calendar.HOUR_OF_DAY) + "</HH>" + "<MM>" + cc.get(Calendar.MINUTE) + "</MM>" 
            + "</ARRIVALDATETIME>" 
        	+ "</SMSTOTP>";  
    }

    protected String generateFailureResponse(String messageId, String messageType, String errorMsg)
    {
        return "<?xml version='1.0'?>" + "<handshake>" + "<message_type>ACK</message_type>" + "<message_id>"
                + messageId + "</message_id>" + "<dun_number>CIC-INTERFACE</dun_number>" + "<send_date>"
                + troubleTicketDateFormat.format(new Date()) + "</send_date>" + "<request_message_type>" + messageType
                + "</request_message_type>" + "<request_message_id>" + messageId + "</request_message_id>"
                + "<status>" + FAILURE + "</status>" + "<reason>" + errorMsg + "</reason>" + "</handshake>";
    }
	/**
	 * @return the sourceLoc
	 */
	public String getSourceLoc() {
		return sourceLoc;
	}
	/**
	 * @param sourceLoc the sourceLoc to set
	 */
	public void setSourceLoc(String sourceLoc) { 
		this.sourceLoc = sourceLoc;
	}    


}
