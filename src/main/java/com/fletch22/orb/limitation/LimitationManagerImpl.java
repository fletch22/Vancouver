package com.fletch22.orb.limitation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.query.CriteriaAttributeDeleteHandler;
import com.fletch22.orb.query.event.LimitationAttributeDeleteHandler;

@Component
public class LimitationManagerImpl extends AbstractLimitationManager implements LimitationManager {
	
	@Autowired
	LimitationAttributeDeleteHandler limitationAttributeDeleteHandler;

	@Override
	public CriteriaAttributeDeleteHandler getCriteriaAttributeDeleteHandler() {
		return this.limitationAttributeDeleteHandler;
	}

}
