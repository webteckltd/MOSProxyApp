
package com.o2.manonsite.mrs;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MRSMessageHelper
{
    public static String MTCODE = "manonsite.co.uk/default"; // Got this from MRS support. See Peter Duguid 
    
    public static String NORMAL_SMS_MESSAGE = "NOR";  
    public static String FLASH_MESSAGE = "FLA"; 
    
    private static final Logger log = LoggerFactory.getLogger(MRSMessageHelper.class);	
    
   
    
    public static String getRequestMessage(RequestContent resposne)
    {
    	String mesg=null;
    	List<Item> item  = resposne.getItem();
		for (Iterator iterator = item.iterator(); iterator.hasNext();) {
			Item item2 = (Item) iterator.next();
			if(item2.getKey().equalsIgnoreCase("Message")){
				mesg =  item2.getValue();
				break;
			}
			
		}	
    	return mesg;
    }
    public static RequestContent createRequest(String shortCode, String network, String msisdn, String msg)
    {
       log.debug("[ BEGIN createRequest ] ");
    	RequestContent  req = new RequestContent();
        
        Item mapItems[] = new Item[4]; 
        for (int i = 0; i < 4; i++) {
            mapItems[i] = new Item();   
        } 
        
        mapItems[0].setKey("SHORTCODE");
        mapItems[0].setValue(shortCode);

        mapItems[1].setKey("NETWORK");
        mapItems[1].setValue(network);

        mapItems[2].setKey("MSISDN");
        mapItems[2].setValue(msisdn);
        
        mapItems[3].setKey("msg");
        mapItems[3].setValue(msg);
        
        req.addItem(mapItems); 
        
        return req; 
        
    }
    
    public static RequestContent createRequest(Properties pXML )
    {
    	log.debug("[ BEGIN createRequest ] ");
    	
    	RequestContent  req = new RequestContent();
    	
    	
    	String shortCode = null,   network = null,   msisdn = null,   msg = null;

    	 
    	Item mapItems[] = new Item[4 + pXML.size()]; 
    	for (int i = 0; i < 4 + pXML.size(); i++) {
    		mapItems[i] = new Item();   
    	}
    	Enumeration aenum = pXML.keys();
    	int i=0;
    	while (aenum.hasMoreElements() && i <pXML.size())
    	{
    		mapItems[i] = new Item();
    		String name = (String) aenum.nextElement(); 
    		mapItems[i].setKey(name);
    		mapItems[i].setValue(pXML.getProperty(name));
    		i++;
    	}  

    	mapItems[i].setKey("SHORTCODE");
    	mapItems[i++].setValue(pXML.getProperty("DESTINATION_ADDR"));

    	mapItems[i].setKey("NETWORK");
    	mapItems[i++].setValue(pXML.getProperty("NETWORKID"));

    	mapItems[i].setKey("MSISDN");
    	mapItems[i++].setValue(pXML.getProperty("SOURCE_ADDR"));

    	mapItems[i].setKey("msg");
    	mapItems[i].setValue(pXML.getProperty("TEXT"));

    	req.addItem(mapItems); 

    	log.debug("[ END createRequest ] ");
    	return req; 

    }
}
