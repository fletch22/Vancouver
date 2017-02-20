package com.fletch22.web.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.fletch22.Fletch22ApplicationContext;
import com.fletch22.app.designer.AppDesignerModule;
import com.fletch22.orb.IntegrationSystemInitializer;

public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

	Logger logger = LoggerFactory.getLogger(StartupListener.class);

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {

		IntegrationSystemInitializer integrationSystemInitializer = (IntegrationSystemInitializer) getBean(IntegrationSystemInitializer.class);

		AppDesignerModule appDesignerModule = (AppDesignerModule) getBean(AppDesignerModule.class);
		integrationSystemInitializer.addOrbSystemModule(appDesignerModule);
		integrationSystemInitializer.nukePaveAndInitializeAllIntegratedSystems();
	}

	public Object getBean(Class<?> clazz) {
		return Fletch22ApplicationContext.getApplicationContext().getBean(clazz);
	}
}
