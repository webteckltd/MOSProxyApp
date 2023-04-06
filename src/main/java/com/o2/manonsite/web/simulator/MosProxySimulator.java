package com.o2.manonsite.web.simulator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.*;
import java.net.UnknownHostException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class MosProxySimulator {
	private static final Logger log = LoggerFactory.getLogger(MosProxySimulator.class);

	@GetMapping({ "/serviceTemplate.htm" })
	public String loadMosProxySimulator() {
		return "serviceTemplate";
	}


	@RequestMapping({ "/executeMosProxySimulator.cms" })
	public String mosProxySimulator(HttpServletRequest request,
			@RequestParam(value = "servicMethod", required = true) String servicMethod,
			@RequestParam(value = "servicURL", required = true) String servicURL,
			@RequestParam(value = "requestBody", required = true) String requestBody,

			Map<String, Object> model) {


		GeneralResponseObject resObj = new GeneralResponseObject();
		resObj.setStatus("SUCESS");
		try {
			log.debug("MosProxySimulator mosProxySimulator()");
			log.debug("servicMethod = " + servicMethod + " servicURL = " + servicURL + " requestBody = " + requestBody);

			log.debug("URLDecoder.decode(requestBody) = " +URLDecoder.decode(requestBody,"UTF-8"));
			//log.debug("URLEncoder.encode(requestBody) = " +URLEncoder.encode(requestBody,"UTF-8"));

			HttpURLConnection conwayConn = getUrlConnection(servicURL);

			//detectProxy( conwayConn.getURL()); 
			DataOutputStream myOutStream = null;
			DataInputStream myInStream = null;
			StringBuffer sb = new StringBuffer();
			try {

				myOutStream = new DataOutputStream(conwayConn.getOutputStream()); 
				//myOutStream.writeBytes( URLEncoder.encode(requestBody));
				myOutStream.writeBytes(requestBody);
				myOutStream.flush();
				myOutStream.close();
				// Test the URL Connection for success
				log.debug("host/port=" + conwayConn.getURL().getHost() + "/" + conwayConn.getURL().getPort());			
				if (!conwayConn.getResponseMessage().equals("OK")) {
					String expStr = "Error" + "URL " + conwayConn.getURL().toExternalForm()  
							+ ": " + conwayConn.getResponseMessage() + "(" +conwayConn.getResponseCode() + ")";				
					log.debug(expStr);
					resObj.setMessage(expStr);
					resObj.setStatus("FAILED");
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
					log.debug("OUTPUT = "+ sb);
					resObj.setMessage(sb.toString());
				}
				//pingHttp(conwayConn.getURL().getHost(), conwayConn.getURL().getPort()); 
			} 
			catch (UnknownHostException e) 
			{  
				log.debug("Could not resolve the host : " + e.getMessage()); 
				resObj.setMessage("Could not resolve the host : " + e.getMessage());
				resObj.setStatus("FAILED");
			}
			catch (IOException ioe) 
			{
				log.debug("IOException" +ioe.getMessage()); 
				resObj.setMessage("IOException" +ioe.getMessage());
				resObj.setStatus("FAILED");
			}




		} catch (Exception e) {
			e.printStackTrace();
			resObj.setStatus("FAILED");
			resObj.setMessage(e.getMessage());
		}

		model.put("dataObject", resObj);
		return "jsonView";
	}



	private HttpURLConnection getUrlConnection(String theUrl) {

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
		conn.setRequestProperty("Content-type", "text/html");
		return conn;

	}

}
