/*
 * Created on 31-May-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.o2.manonsite.web;

import java.util.Date;
import java.io.File;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * @author aademij1
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SessionListener implements HttpSessionListener {
	
	private static final Logger log = LoggerFactory.getLogger(SessionListener.class);	
	private int sessionCount;  
	
	public SessionListener() {
		this.sessionCount = 0;	     
	}

	public void sessionCreated(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		session.setMaxInactiveInterval(60);
		synchronized (this) {
			sessionCount++;
		}
		String id = session.getId();
		Date now = new Date();
		String message = new StringBuffer("New Session created on ").append(
				now.toString()).append("\nID: ").append(id).append("\n")
				.append("There are now ").append("" + sessionCount).append(
				" live sessions in the application.").toString();

		log.debug(message);
	}

	public void sessionDestroyed(HttpSessionEvent se) {

		HttpSession session = se.getSession();
		String id = session.getId();  
		//File tmp_dir = new File((String) (session.getServletContext().getAttribute("javax.servlet.context.tempdir")));  
		synchronized (this) {
			--sessionCount;
		}
		String message = new StringBuffer("Session destroyed"
				+ "\nValue of destroyed session ID is ").append("" + id).append(
				"\n").append("There are now ").append("" + sessionCount)
				.append(" live sessions in the application.").toString();
		log.debug(message);

		/*File [] tmp_files = tmp_dir.listFiles();
		String fname=null;
		for(int i=0;i < tmp_files.length;i++)
		{
			fname = tmp_files[i].getName();
			if(fname.startsWith("MRSService"))
			{ 
				log.debug("Removing " + fname);
				tmp_files[i].delete();
			}
		}*/
		//Delete Temp WSDL FIle
		//URL wsdlURL = (URL) (session.getServletContext().getAttribute("mos.servlet.wsdl.tmp.url"));
		//log.debug("wsdlURL:" + wsdlURL);

		//log.debug("Deleting Temporary MRSService WSDL File");
		//new File(wsdlURL.getFile()).delete();
	}
} 
