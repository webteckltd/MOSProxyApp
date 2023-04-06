package com.o2.manonsite.web.simulator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.AbstractView;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component("jsonView")
public class JsonView extends AbstractView{

	private JsonEncoding  encoding = JsonEncoding.UTF8;
	private static final Logger logger = LoggerFactory.getLogger(JsonView.class);
	final DateFormat df = new SimpleDateFormat("dd-MM-yyyy");

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model,HttpServletRequest request, HttpServletResponse response) throws Exception {	

		prepareResponse(request,response);
		Object dataObject  = 	model.get("dataObject");

		if(dataObject != null){			
			ObjectMapper mapper = new ObjectMapper();
			mapper.setDateFormat(df);
			logger.debug(" Response JSON data = " + mapper.writeValueAsString(dataObject));
			JsonGenerator generator = mapper.getJsonFactory().createJsonGenerator(response.getOutputStream(), encoding);
			mapper.writeValue(generator, dataObject);
		}
	}

	protected void prepareResponse(HttpServletRequest request, HttpServletResponse response)
	{
		response.setContentType("application/json");
		response.setCharacterEncoding(encoding.getJavaName());
		response.addHeader("Pragma", "no-cache");
		response.addHeader("Cache-Control", "no-cache, no-store, max-age=0");
		response.addDateHeader("Expires", 1L);	        
	}		

}
