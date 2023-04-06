package com.o2.manonsite.web;

import java.util.HashMap;
import java.util.Properties;

//import org.apache.log4j.Logger; 

public class Smstotp {

	//private static Logger log = Logger.getLogger(Smstotp.class);
	/*
	 * <!ELEMENT SMSTOTP (SOURCE_ADDR,
                   TEXT,
                   WINTRANSACTIONID,  
                   DESTINATION_ADDR,
                   SERVICEID, 
                   NETWORKID, 
                   ARRIVALDATETIME,
                   LOCATION? )>
	 */
	private String source_addr="";
	private String text="";	
	private String wintransID="";
	private String dest_addr="";
	private int servID;
	private String networkID="";
	private HashMap arrivalTime=new HashMap();
	private String location="";
	private int parserState=0;
	private String replyText="";
	public Smstotp(){
		
	}
	
	
	/**
	 * @param source_addr
	 * @param text
	 * @param wintransID
	 * @param servID
	 * @param networkID
	 * @param arrivalTime
	 * @param location
	 */
	public Smstotp(String source_addr, String text, String wintransID,String dest_addr,
			int servID, String networkID, HashMap arrivalTime, String location) {
		super();
		this.source_addr = source_addr;
		this.text = text;
		this.wintransID = wintransID;
		this.dest_addr = dest_addr;
		this.servID = servID;
		this.networkID = networkID;
		this.arrivalTime = arrivalTime;
		this.location = location;
	}
 
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Smstotp Details - ");
		sb.append("source_addr:" + getSource_addr());
		sb.append(", ");
		sb.append("text:" + getText());
		sb.append(", ");
		sb.append("wintransID:" + getWintransID());
		sb.append(", ");
		sb.append("dest_addr:" + getDest_addr());
		sb.append(", ");
		sb.append("servID:" + getServID()); 
		sb.append(", ");
		sb.append("networkID:" + getNetworkID());
		sb.append(", ");
		sb.append("arrivalTime:" + getArrivalTime());
		sb.append(", ");
		sb.append("location:" + getLocation());
		sb.append(", ");
		sb.append("parser Status:" + getParserState());
		sb.append(".");
		
		return sb.toString();
	}

	public Properties toProperties() {
		Properties pXML=new Properties(); 
		pXML.put("SOURCE_ADDR",getSource_addr()); 
		pXML.put("TEXT", getText()); 
		pXML.put("WINTRANSID" , getWintransID()); 
		pXML.put("DESTINATION_ADDR", getDest_addr());
		pXML.put("SERVID", new Integer(getServID()).toString()); 
		pXML.put("NETWORKID" , getNetworkID());
		pXML.put("ARRIVALTIME", getArrivalTime().toString());
		pXML.put("LOCATION", getLocation());
		pXML.put("STATUS", new Integer(getParserState()).toString()); 		
		return pXML;
	}
	/**
	 * @return the source_addr
	 */
	public String getSource_addr() {
		return source_addr;
	}


	/**
	 * @param source_addr the source_addr to set
	 */
	public void setSource_addr(String source_addr) {
		this.source_addr = source_addr != null ? source_addr:"";
	}


	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}


	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text != null ? text:"";
	}


	/**
	 * @return the wintransID
	 */
	public String getWintransID() {
		return wintransID;
	}


	/**
	 * @param wintransID the wintransID to set
	 */
	public void setWintransID(String wintransID) {
		this.wintransID = wintransID != null ? wintransID:"";
	}


	/**
	 * @return the servID
	 */
	public int getServID() {
		return servID;
	}


	/**
	 * @param servID the servID to set
	 */
	public void setServID(int servID) {
		this.servID = servID;
	}


	/**
	 * @return the arrivalTime
	 */
	public HashMap getArrivalTime() {
		return arrivalTime;
	}


	/**
	 * @param arrivalTime the arrivalTime to set
	 */
	public void setArrivalTime(HashMap arrivalTime) {
		this.arrivalTime = arrivalTime != null ? arrivalTime: new HashMap();
	}


	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}


	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {		 
		this.location = location != null ? location:"";
	}


	/**
	 * @return the networkID
	 */
	public String getNetworkID() {
		return networkID;
	}


	/**
	 * @param networkID the networkID to set
	 */
	public void setNetworkID(String networkID) {
		this.networkID = networkID != null ? networkID:"";
	}


	/**
	 * @return the dest_addr
	 */
	public String getDest_addr() {
		return dest_addr;
	}


	/**
	 * @param dest_addr the dest_addr to set
	 */
	public void setDest_addr(String dest_addr) {
		this.dest_addr = dest_addr != null ? dest_addr:"";
	}


	/**
	 * @return the parserState
	 */
	public int getParserState() {
		return parserState;
	}


	/**
	 * @param parserState the parserState to set
	 */
	public void setParserState(int parserState) {
		this.parserState = parserState;
	}


	/**
	 * @return the replyText
	 */
	public String getReplyText() {
		return replyText;
	}


	/**
	 * @param replyText the replyText to set
	 */
	public void setReplyText(String replyText) {
		this.replyText = replyText;
	}
}
