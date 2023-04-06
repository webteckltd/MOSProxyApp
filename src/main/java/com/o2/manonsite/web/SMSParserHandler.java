package com.o2.manonsite.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.helpers.DefaultHandler;
 
/**
 * @author AAdemij1
 *
 */
public class SMSParserHandler extends DefaultHandler  implements DeclHandler {

	private static final String SMSHEADER = "SMSTOTP";

	private static final String ARRIVALDATETIME = "ARRIVALDATETIME";

	private static final Logger log = LoggerFactory.getLogger(SMSParserHandler.class);	
	List mySMSes;

	private String tempVal;
	private HashMap tempMap;
	//to maintain context
	private Smstotp tempSms;
	private ArrayList arrSMSlist= new ArrayList();

	/* 	regular expression over element names
	 * 	choice: (...|...|...) 
		sequence: (...,...,...) 
		optional: ...? 
		zero or more: ...* 
		one or more: ...+ 
	 */

	/**
	 * 
	 */
	public SMSParserHandler() {
		super();
		// TODO Auto-generated constructor stub
		mySMSes = new ArrayList();
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ext.DeclHandler#attributeDecl(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public void attributeDecl(String elementName, String attributeName, String type,
			String valueDefault, String value) throws SAXException {
		// TODO Auto-generated method stub
		/*System.out.println("ATTRIBUTE: ");
		System.out.println("Element Name: " + elementName);
		System.out.println("Attribute Name: " + attributeName);
		System.out.println("Type: " + type);
		System.out.println("Default Value: " + valueDefault);
		System.out.println("Value: " + value); */
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ext.DeclHandler#elementDecl(java.lang.String, java.lang.String)
	 */
	public void elementDecl(String name, String model) throws SAXException {
		// TODO Auto-generated method stub
		//System.out.println("ELEMENT: ");
		//System.out.println("Name: " + name);
		//System.out.println("Model: " + model); 
		if(name.equals(SMSHEADER) ||name.equals(ARRIVALDATETIME))
		{  
			String [] tmpmodels = model.replaceAll("[?|+|(|)]", "").split(",");	
			for (int i=0; tmpmodels != null && i <  tmpmodels.length; i++)
			{
				arrSMSlist.add(tmpmodels[i]);
			}		
			log.debug("arrSMSlist" + arrSMSlist.toString());
		} 
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ext.DeclHandler#externalEntityDecl(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void externalEntityDecl(String name, String publicId, String systemId)
	throws SAXException {
		// TODO Auto-generated method stub
		log.debug("EXTERNAL ENTITY: " + name + ":"+ publicId + ":"+ systemId);
	}

	/*
	 * (non-Javadoc)
	 * @see org.xml.sax.ext.DeclHandler#internalEntityDecl(java.lang.String, java.lang.String)
	 */
	public void internalEntityDecl(String name, String value)
	throws SAXException {
		// TODO Auto-generated method stub
		log.debug("INTERNAL ENTITY: " + name + ":"+ value);
	}

	//flag to check if the xml document was valid
	public boolean isSchemaValidated = true;

	private String[] models;



	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#fatalError(org.xml.sax.SAXParseException)
	 * Ignore the fatal errors
	 */
	public void fatalError(SAXParseException exception)throws SAXException { }

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#error(org.xml.sax.SAXParseException)
	 * Validation errors
	 */
	public void error(SAXParseException e)throws SAXParseException {
		log.debug("Error at " +e.getLineNumber() + " line.");
		log.debug(e.getMessage());
		setValidity(e);
		printInfo(e);
		//System.exit(0);
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#warning(org.xml.sax.SAXParseException)
	 * Show warnings
	 */
	public void warning(SAXParseException err)throws SAXParseException{
		log.debug(err.getMessage());
		setValidity(err);
		printInfo(err);
		//System.exit(0);
	}	


	private void setValidity(SAXParseException se)
	{
		isSchemaValidated = false; 
	}

	public boolean isValid()
	{
		return isSchemaValidated; 
	}
	private void printInfo(SAXParseException e) {
		log.debug("Public ID: "+e.getPublicId());
		log.debug("System ID: "+e.getSystemId());
		log.debug("Line number: "+e.getLineNumber());
		log.debug("Column number: "+e.getColumnNumber());
		log.debug("Message: "+e.getMessage());
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//reset
		tempVal = "";
		if(qName.equalsIgnoreCase(SMSHEADER)) {
			//create a new instance of employee 
			tempSms = new Smstotp(); 
		}     
		else if (qName.equalsIgnoreCase(ARRIVALDATETIME)) 
		{ 
			tempMap = new HashMap(4);
		}

	}		

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch,start,length);
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if(qName.equalsIgnoreCase(SMSHEADER)) 
		{
			//add it to the list
			tempSms.setArrivalTime(tempMap);
			if(!isSchemaValidated)
				tempSms.setParserState(301);
			mySMSes.add(tempSms);
			
		}
		else
		{		
			//System.out.println("tempVal: " + tempVal +  " qName " + qName);
			for (int i=0; arrSMSlist != null && i <  arrSMSlist.size(); i++)
			{ 	
				//SOURCE_ADDR, TEXT, WINTRANSACTIONID, DESTINATION_ADDR, SERVICEID, NETWORKID, ARRIVALDATETIME, 
				//LOCATION, DD, MMM, YYYY, HH, MM]
				
				
				if (qName.equalsIgnoreCase((String)arrSMSlist.get(i))) 
				{ 					 
					switch(i)
					{
					case 0:
						tempSms.setSource_addr(new String(tempVal)); 
						break;
					case 1:
						tempSms.setText(tempVal);
						break;
					case 2:
						tempSms.setWintransID(tempVal);
						break;
					case 3:
						tempSms.setDest_addr(tempVal);
						break;
					case 4:
						tempSms.setServID(Integer.parseInt(tempVal)); 
						break;
					case 5:
						tempSms.setNetworkID(tempVal);
						break;
					case 6:
						break;					
					case 7:
						tempSms.setLocation(tempVal);
						break;
					case 8:
						tempMap.put("DD", tempVal);	
						break;			
					case 9:
						tempMap.put("MMM", tempVal);
						break;
					case 10:
						tempMap.put("YYYY", tempVal);
					case 11:
						tempMap.put("HH", tempVal);
						break;
					case 12:
						tempMap.put("MM", tempVal);
						break;
					default:
						break;
					}
					//continue;
				}

			}
		}
	}

public void printData(){
		
	log.debug("No of SMS Messages '" + mySMSes.size() + "'.");
		
		Iterator it = mySMSes.iterator();
		while(it.hasNext()) {
			log.debug(it.next().toString());
		}
	}

public void printData(List SMSes){
	
	log.debug("No of SMS Messages '" + SMSes.size() + "'.");
	
	Iterator it = SMSes.iterator();
	while(it.hasNext()) {
		log.debug(it.next().toString());
	}
}
public  List getSMSData(){
	
	log.debug("No of SMS Messages '" + mySMSes.size() + "'.");	
	return mySMSes;
}
	public static void main(String[] args){
		SAXParserFactory factory = SAXParserFactory.newInstance();
		javax.xml.parsers.SAXParser parser;
		try {
			parser = factory.newSAXParser();
			SMSParserHandler handler = new SMSParserHandler();
			parser.setProperty("http://xml.org/sax/properties/declaration-handler",	handler);
			//parser.parse(document.getAbsolutePath());
			parser.parse("C:\\apps\\eclipse\\workspace\\manonsite\\dtd\\tpbound_messages_v1_Eg1.XML", handler);
			handler.printData();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
}
