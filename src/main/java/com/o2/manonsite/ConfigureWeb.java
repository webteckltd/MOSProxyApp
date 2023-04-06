package com.o2.manonsite;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import com.o2.manonsite.web.MosSender;
import com.o2.manonsite.web.MosServlet;



@Configuration
public class ConfigureWeb implements ServletContextInitializer {

	private static final Logger logger = LoggerFactory.getLogger(ConfigureWeb.class);
	
  public void onStartup(ServletContext servletContext) throws ServletException {
      registerServlet(servletContext);
  }

  private void registerServlet(ServletContext servletContext) {
	  logger.debug("register Servlet");
      ServletRegistration.Dynamic MosServlet = servletContext.addServlet("MosServlet", new MosServlet());

      MosServlet.addMapping("/o2mosservlet");
      MosServlet.setAsyncSupported(true);
      MosServlet.setLoadOnStartup(2);
      
      
      ServletRegistration.Dynamic MosSender = servletContext.addServlet("MosSender", new MosSender());

      MosSender.addMapping("/o2mossender");
      MosSender.setAsyncSupported(true);
      MosSender.setLoadOnStartup(2);
      
  }
  
  
  @Bean
  public FilterRegistrationBean registration(HiddenHttpMethodFilter filter) {
      FilterRegistrationBean registration = new FilterRegistrationBean(filter);
      registration.setEnabled(false);
      return registration;
  }
}