package com.fletch22.web.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.Fletch22ApplicationContext;
import com.fletch22.app.designer.AppDesignerModule;
import com.fletch22.orb.IntegrationSystemInitializer;

public class Fletch22ServletContextListener implements ServletContextListener {
	
	Logger logger = LoggerFactory.getLogger(Fletch22ServletContextListener.class);

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.info("ServletContextListener destroyed");
	}

	// Run this before web application is started
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
		logger.info("ServletContextListener signalled ...");	
		IntegrationSystemInitializer integrationSystemInitializer = (IntegrationSystemInitializer) getBean(IntegrationSystemInitializer.class);
		
		AppDesignerModule appDesignerModule = (AppDesignerModule) getBean(AppDesignerModule.class);
		appDesignerModule.initialize();
//		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems();
//		integrationSystemInitializer.addOrbSystemModule(appDesignerModule);
//		integrationSystemInitializer.initializeSystem();
		
		logger.info("Orb System Initialized.");
	}
	
	public Object getBean(Class<?> clazz) {
		return Fletch22ApplicationContext.getApplicationContext().getBean(clazz);
	}
}
