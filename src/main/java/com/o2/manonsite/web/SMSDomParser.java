package com.o2.manonsite.web;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SMSDomParser {

	//No generics
	List myEmpls;
	Document dom;


	public SMSDomParser(){
		//create a list to hold the employee objects
		myEmpls = new ArrayList();
	}

	public void runExample() {
		
		//parse the xml file and get the dom object
		parseXmlFile();
		
		//get each employee element and create a Employee object
		parseDocument();
		
		//Iterate through the list and print the data
		printData();
		
	}
	
	
	private void parseXmlFile(){
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {			
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			//parse using builder to get DOM representation of the XML file
			dom = db.parse("C:\\apps\\eclipse\\workspace\\manonsite\\dtd\\tpbound_messages_v1_Eg1.XML");
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	
	private void parseDocument(){
		//get the root element
		Element docEle = dom.getDocumentElement();
		
		//get a nodelist of &lt;employee&gt; elements
		NodeList nl = docEle.getElementsByTagName("SMSTOTP");
		if(nl != null && nl.getLength() > 0) {
			for(int i = 0 ; i < nl.getLength();i++) {
				
				//get the employee element
				Element el = (Element)nl.item(i);
				
				//get the Employee object
				Smstotp e = getSmstotp(el);
				
				//add it to list
				myEmpls.add(e);
			}
		}
	}


	/**
	 * I take an employee element and read the values in, create
	 * an Employee object and return it
	 * @param empEl
	 * @return
	 */
	private Smstotp getSmstotp(Element empEl) {
		
		//for each &lt;employee&gt; element get text or int values of 
		//name ,id, age and name
		String name = getTextValue(empEl,"SOURCE_ADDR");
		String text = getTextValue(empEl,"TEXT");
		String winid = getTextValue(empEl,"WINTRANSACTIONID");
		String destid = getTextValue(empEl,"DESTINATION_ADDR");
		
		int sid = getIntValue(empEl,"SERVICEID");
		String nid = getTextValue(empEl,"NETWORKID");			 
		
		//Create a new Employee with the value read from the xml nodes
		Smstotp e = new Smstotp(name,text,winid,destid,sid,nid,null,"");
		
		return e;
	}


	/**
	 * I take a xml element and the tag name, look for the tag and get
	 * the text content 
	 * i.e for &lt;employee&gt;&lt;name&gt;John&lt;/name&gt;&lt;/employee&gt; xml snippet if
	 * the Element points to employee node and tagName is name I will return John  
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}

	
	/**
	 * Calls getTextValue and returns a int value
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private int getIntValue(Element ele, String tagName) {
		//in production application you would catch the exception
		return Integer.parseInt(getTextValue(ele,tagName));
	}
	
	/**
	 * Iterate through the list and print the 
	 * content to console
	 */
	private void printData(){
		
		System.out.println("No of Employees '" + myEmpls.size() + "'.");
		
		Iterator it = myEmpls.iterator();
		while(it.hasNext()) {
			System.out.println(it.next().toString());
		}
	}

	
	public static void main(String[] args){
		//create an instance
		SMSDomParser  dpe = new SMSDomParser();
		
		//call run example
		dpe.runExample();
	}

}
