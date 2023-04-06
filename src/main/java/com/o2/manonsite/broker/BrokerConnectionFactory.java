package com.o2.manonsite.broker;

import com.o2.manonsite.broker.wintel.WintelBrokerConnection;

public class BrokerConnectionFactory {

	public static BrokerConnection newInstance(String os){
      
            return new WintelBrokerConnection();
       
       
    }	
}
