package com.fletch22.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.dao.LogActionService;
import com.fletch22.orb.modules.system.OrbSystemModule;

@Component
public class LoadCacheFromPersistenceModule implements OrbSystemModule {
	
	@Autowired
	LogActionService logActionService;

	@Override
	public void initialize() {
		logActionService.loadCacheFromDb();
	}
}
