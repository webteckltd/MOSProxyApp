package com.o2.manonsite.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException; 
import org.xml.sax.helpers.DefaultHandler;

public class SMSSAXParser extends DefaultHandler{

	List mySMSes;
	
	private String tempVal;
	private HashMap tempMap;
	//to maintain context
	private Smstotp tempSms;
	
	
	public SMSSAXParser(){
		mySMSes = new ArrayList();
	}
	
	public void runExample() {
		parseDocument();
		printData();
	}

	private void parseDocument() {
		
		//get a factory
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
		
			//get a new instance of parser
			SAXParser sp = spf.newSAXParser();
			
			//parse the file and also register this class for call backs
			sp.parse("C:\\apps\\eclipse\\workspace\\manonsite\\dtd\\tpbound_messages_v1_Eg1.XML", this);
			
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch (IOException ie) {
			ie.printStackTrace();
		}
	}

	/**
	 * Iterate through the list and print
	 * the contents
	 */
	private void printData(){
		
		System.out.println("No of SMS Messages '" + mySMSes.size() + "'.");
		
		Iterator it = mySMSes.iterator();
		while(it.hasNext()) {
			System.out.println(it.next().toString());
		}
	}
	

	//Event Handlers
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		//reset
		tempVal = "";
		if(qName.equalsIgnoreCase("SMSTOTP")) {
			//create a new instance of employee 
			tempSms = new Smstotp(); 
		}     
		else if (qName.equalsIgnoreCase("ARRIVALDATETIME")) 
		{ 
			tempMap = new HashMap(4);
		}
		
	}
	

	public void characters(char[] ch, int start, int length) throws SAXException {
		tempVal = new String(ch,start,length);
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException {

		if(qName.equalsIgnoreCase("SMSTOTP")) 
		{
			//add it to the list
			tempSms.setArrivalTime(tempMap);
			mySMSes.add(tempSms);
		}
		else if (qName.equalsIgnoreCase("SOURCE_ADDR")) { tempSms.setSource_addr(tempVal); }
		else if (qName.equalsIgnoreCase("TEXT")) { tempSms.setText(tempVal); }
		else if (qName.equalsIgnoreCase("WINTRANSACTIONID")) { tempSms.setWintransID(tempVal); }
		else if (qName.equalsIgnoreCase("DESTINATION_ADDR")) { tempSms.setText(tempVal); }
		else if (qName.equalsIgnoreCase("SERVICEID")) { tempSms.setServID(Integer.parseInt(tempVal)); }
		else if (qName.equalsIgnoreCase("NETWORKID")) { tempSms.setNetworkID(tempVal); }		
		String d=null,h=null,m=null,y=null,mo=null;
		if (qName.equalsIgnoreCase("DD")) {tempMap.put("DD", tempVal);}
		else if(qName.equalsIgnoreCase("MMM")) {tempMap.put("DD", tempVal);}
		else if(qName.equalsIgnoreCase("YYYY")) {tempMap.put("MMM", tempVal);}
		else if(qName.equalsIgnoreCase("HH")) {tempMap.put("HH", tempVal);}
		else if(qName.equalsIgnoreCase("MM")) {tempMap.put("MM", tempVal);}
	}
	
	public static void main(String[] args){
		SMSSAXParser spe = new SMSSAXParser();
		spe.runExample();
	}
	
}
 