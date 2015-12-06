package com.fletch22.orb.limitation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.query.CriteriaAttributeDeleteHandler;
import com.fletch22.orb.query.event.DefLimitationAttributeDeleteHandler;

@Component
public class DefLimitationManagerImpl extends AbstractLimitationManager implements DefLimitationManager {
	
	@Autowired
	DefLimitationAttributeDeleteHandler defLimitationAttributeDeleteHandler;

	@Override
	public CriteriaAttributeDeleteHandler getCriteriaAttributeDeleteHandler() {
		return this.defLimitationAttributeDeleteHandler;
	}
}
