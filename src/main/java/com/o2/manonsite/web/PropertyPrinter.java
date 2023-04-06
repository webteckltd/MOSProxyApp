package com.o2.manonsite.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.*;
import java.io.*; 
import java.util.Properties;

public class PropertyPrinter {  
	private final Properties pprops = new Properties();;
	private int nodeCount = 0;
	private static final Logger log = LoggerFactory.getLogger(PropertyPrinter.class);	

	public PropertyPrinter() 
	{ 
	}

	String lastName=null;
	public void writeNode(Node node) throws IOException {
		if (node == null) 
		{
			throw new NullPointerException("Node must be non-null.");
		}

		if (node.getNodeType() == Node.DOCUMENT_NODE
				|| node.getNodeType() == Node.DOCUMENT_FRAGMENT_NODE) 
		{ 
			// starting a new document, reset the node count
			nodeCount = 1;
		}

		String name = node.getNodeName(); // never null
		String type = NodeTyper.getTypeName(node); // never null
		String localName = node.getLocalName();
		String uri = node.getNamespaceURI();
		String prefix = node.getPrefix();
		String value = node.getNodeValue();

		if (node.getNodeType() == Node.ELEMENT_NODE && name != null) 
		{
			lastName=name;
		}
		if (value != null && (node.getNodeType() == Node.ATTRIBUTE_NODE || node.getNodeType() == Node.TEXT_NODE)) 
		{
			//log.debug("Node " + nodeCount + " Type: " + type + " lastName: " + lastName + " Value: " + value); 
			pprops.setProperty(lastName, value);
		}

		if (localName != null) 
		{
			log.debug("Local Name: " + localName);
		}

		if (prefix != null) 
		{
			log.debug("Prefix: " + prefix);
		}

		if (uri != null) 
		{
			log.debug("  Namespace URI: " + uri);
		}		 
		nodeCount++;
		//log.debug("pprops size : " + pprops.size());
	}

	/**
	 * @return the printer
	 */
	public Properties getPProps() {
		return pprops;
	}
}

