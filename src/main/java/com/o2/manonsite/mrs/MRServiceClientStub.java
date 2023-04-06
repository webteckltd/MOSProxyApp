package com.o2.manonsite.mrs;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;


public class MRServiceClientStub {
	String ipaddres ;
	String port;
	String uri ="/mrservice/getContent";
	String serviceUrl; 
	private static final Logger log = LoggerFactory.getLogger(MRServiceClientStub.class);
	
	public MRServiceClientStub(String ipaddres, String port) {
		super();
		this.ipaddres = ipaddres;
		this.port = port;
		serviceUrl =  "http://"+ipaddres.trim()+":"+port.trim()+uri;
	}
	
	
	public RequestContent getContent( RequestContent request) throws Exception{
		RequestContent resContent  = null;
		try {
	
			    log.debug("calling service  : " + serviceUrl);				
				DefaultHttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(serviceUrl);
				httppost.addHeader("content-type", "application/json");
				
				ObjectMapper mapper = new ObjectMapper();
				String resquestBody = mapper.writeValueAsString(request);
				log.debug(" Request JSON data = " + resquestBody);
				
				StringEntity s = new StringEntity(resquestBody, "UTF-8");
				httppost.setEntity(s);

				log.debug("executing request" + httppost.getRequestLine());
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();

				log.debug(response.getStatusLine().toString());
				if (entity != null) {
					String responseJson  =  EntityUtils.toString(entity);	
					log.debug("Response JSON data =  " + responseJson);
					resContent  = mapper.readValue(responseJson, RequestContent.class);
					entity.consumeContent();
				}						
				httpclient.getConnectionManager().shutdown();
				
				
			}catch (Exception e) {	
				log.error("Problem while calling MR Service ", e);
				throw e;
			}
		return resContent;
	}
	
	
	
	
}
