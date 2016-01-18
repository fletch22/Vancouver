package com.fletch22.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.fletch22.Fletch22ApplicationContext;
import com.fletch22.orb.client.service.OrbTypeService;

public class TestServlet extends HttpServlet {
	
	Logger logger = LoggerFactory.getLogger(TestServlet.class);

    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	
    	ApplicationContext applicationContext = getApplicationContext();
    	
    	int count = applicationContext.getBeanDefinitionCount();
    	resp.getOutputStream().write(String.valueOf(count).getBytes());
    	
    	OrbTypeService orbTypeService = (OrbTypeService) applicationContext.getBean(OrbTypeService.class);
    	
    	String orbLabel = "Foomanshu" + DateTime.now().getMillis();
    	long orbTypeInternalId = orbTypeService.addOrbType(orbLabel);
    	
    	logger.info("About to give message.");
    	
    	String outputString = "Hello World Peax: " + String.valueOf(orbTypeInternalId);
    	
        resp.getOutputStream().write(outputString.getBytes());
    }
    
    public ApplicationContext getApplicationContext() {
    	return Fletch22ApplicationContext.getApplicationContext();
    }
}