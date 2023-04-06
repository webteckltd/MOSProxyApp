/*
 * Created on 13-Mar-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.o2.manonsite.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.servlet.ServletException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import com.o2.common.Constants;
import com.o2.manonsite.mrs.MRServiceClientStub;
/**
 * @author aademij1
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MosExpert {

	private String sID = null; 
	private static final String USER_FIELD = "USER";
	private static final String PASSWORD_FIELD = "Password";
	private static final String REQID_FIELD = "RequestID"; 
	//2000 to 3000 = Error codes reserved for third parties to assign, please notify WIN of the meaning
	static final String REQUESTID_STR="HTTP_T1_IDyyyy_Rzzz";
	DocumentBuilder builder;
	protected org.apache.xpath.CachedXPathAPI xpathAPI;
	private static final Logger log = LoggerFactory.getLogger(MosExpert.class);	
	protected MRServiceClientStub mrsServiceRemote; 

	private FileInputStream fis;
	private PropertyPrinter printer = new PropertyPrinter(); 
	private int FileCreationCnt=0;
	int xmlStatusCode=0;

	public MosExpert(String SessId) throws ServletException
	{
		this();
		sID = new String(SessId);		
		//System.out.println("sID:" + sID);
	}
	public MosExpert() throws ServletException
	{		        

	}	

	private String getText(Node node)
	{
		StringBuffer buf = new StringBuffer();

		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			buf.append(list.item(i).getNodeValue()); 
		}
		return buf.toString();
	} 

	// note use of recursion
	public void followNode(Node node) throws IOException {

		printer.writeNode(node);
		if (node.hasChildNodes()) {
			Node firstChild = node.getFirstChild();
			followNode(firstChild);
		}
		Node nextNode = node.getNextSibling();
		if (nextNode != null) followNode(nextNode);

	}

	public String generateGenericXMLResponse(int httpCode)
	{
		Random generator = new Random();
		long r = Math.abs(generator.nextInt(Integer.MAX_VALUE) % 1000);
		
		String reqidstr =REQUESTID_STR.replaceFirst("yyyy", new Long(r).toString()).replaceFirst("zzz", new Integer(httpCode).toString());
		return "<?xml version=\"1.0\" standalone=\"no\"?>" 
		+ "<!DOCTYPE SMSRESPONSE SYSTEM \"" + Constants.GENERIC_RESPONSE_V1_DTD_DEF + "\">"
		+ "<SMSRESPONSE>" + "<REQUESTID>"
		+ reqidstr + "</REQUESTID>" + "</SMSRESPONSE>";	    
	}
	String dtdFilename;
	public List DOMValidateDTD(String xmlString) 
	{ 
		//List propXML = null; 
		FileOutputStream fos=null;
		SMSParserHandler handler = new SMSParserHandler();
		try {	
			if(xmlString == null)
				throw new FileNotFoundException();
			log.debug("xmlString:"  + xmlString);		 

			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(true);

			EntityResolver er = new EntityResolver() {
				public InputSource resolveEntity(String publicId, String systemId)
				throws SAXException, IOException { 
					if (systemId.endsWith("dtd")) {
						dtdFilename= MosServlet.ServletRealPath + System.getProperty("file.separator") + new File(systemId).getName();
						log.debug("systemId:" + dtdFilename);
						return new InputSource(dtdFilename);
					}
					// If no match, returning null makes process continue normally
					return null;					 
				}
			};						

			javax.xml.parsers.SAXParser parser = factory.newSAXParser();				
			//get the XMLReader and validate
			XMLReader XMLr=parser.getXMLReader();
			XMLr.setEntityResolver(er); 
			XMLr.parse(new InputSource(new StringReader(xmlString)));
			
 
			if (handler.isValid()) 
			{
				//Parser the Data
				parser.setProperty("http://xml.org/sax/properties/declaration-handler",	handler);
				InputSource inpSou = new InputSource(new StringReader(xmlString));
				inpSou.setSystemId(dtdFilename);
				parser.parse(inpSou, handler);
				
				log.debug("xml Document is valid."); 
				Iterator it = handler.getSMSData().iterator(); 
			}
			else {
				// If the document isn't well-formed, an exception has
				// already been thrown and this has been skipped.
				log.error("xml Document isn't well-formed."); 
				setXmlStatusCode(301);
				//propXML.setProperty(Constants.STATUS_CODE, "301"); 					
			}		
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			log.error(("ERR: Invalid XML received - schema validation failed"), e);
			setXmlStatusCode(310); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} 
		/*catch (SAXException e) {
			// TODO Auto-generated catch block
			log.error(("ERR: Invalid XML received - schema validation failed"), e);
			propXML.setProperty(Constants.STATUS_CODE, "310");
		}*/		 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setXmlStatusCode(2000);
			//propXML.setProperty(Constants.STATUS_CODE, "2000");
		}	
		finally
		{	
			return handler.getSMSData();		
		}
	}
	public List DummayValidateDTD(String xmlString) 
	{ 
		//List propXML = null; 
		FileOutputStream fos=null;
		SMSParserHandler handler = new SMSParserHandler();
		try {	
			if(xmlString == null)
				throw new FileNotFoundException();
			log.debug("xmlString"  + xmlString);		 

			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setValidating(true);

			EntityResolver er = new EntityResolver() {
				public InputSource resolveEntity(String publicId, String systemId)
				throws SAXException, IOException { 
					if (systemId.endsWith("dtd")) {
						dtdFilename= "/apps/manonsite/maxim/dtd" + System.getProperty("file.separator") + new File(systemId).getName();
						log.debug("systemId:" + dtdFilename);
						return new InputSource(dtdFilename);
					}
					// If no match, returning null makes process continue normally
					return null;					 
				}
			};						

			javax.xml.parsers.SAXParser parser = factory.newSAXParser();				
			//get the XMLReader and validate
			XMLReader XMLr=parser.getXMLReader();
			XMLr.setEntityResolver(er); 
			XMLr.parse(new InputSource(new StringReader(xmlString)));
			
 
			if (handler.isValid()) 
			{
				//Parser the Data
				parser.setProperty("http://xml.org/sax/properties/declaration-handler",	handler);
				InputSource inpSou = new InputSource(new StringReader(xmlString));
				inpSou.setSystemId(dtdFilename);
				parser.parse(inpSou, handler);
				
				log.debug("xml Document is valid."); 
				Iterator it = handler.getSMSData().iterator(); 
			}
			else {
				// If the document isn't well-formed, an exception has
				// already been thrown and this has been skipped.
				log.error("xml Document isn't well-formed."); 
				setXmlStatusCode(301);
				//propXML.setProperty(Constants.STATUS_CODE, "301"); 					
			}		
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			log.error(("ERR: Invalid XML received - schema validation failed"), e);
			setXmlStatusCode(310); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} 
		/*catch (SAXException e) {
			// TODO Auto-generated catch block
			log.error(("ERR: Invalid XML received - schema validation failed"), e);
			propXML.setProperty(Constants.STATUS_CODE, "310");
		}*/		 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			setXmlStatusCode(2000);
			//propXML.setProperty(Constants.STATUS_CODE, "2000");
		}	
		finally
		{	
			return handler.getSMSData();		
		}
	}	

	
	
	/**
	 * @return the xmlStatusCode
	 */
	public int getXmlStatusCode() {
		return xmlStatusCode;
	}
	/**
	 * @param xmlStatusCode the xmlStatusCode to set
	 */
	public void setXmlStatusCode(int xmlStatusCode) {
		this.xmlStatusCode = xmlStatusCode;
	}	 
}
