package com.fletch22.app.designer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.limitation.DefLimitationManager;
import com.fletch22.orb.query.Criteria;
import com.fletch22.orb.query.constraint.CriteriaBuilder;

@Component
public class ComponentConstrainer {
	
	@Autowired
	DefLimitationManager defLimitationManager;

	public void addAmongstUniqueConstraintOnField(long orbTypeInternalId, String attributeName) {
		
		Criteria criteria = new CriteriaBuilder(orbTypeInternalId).addAmongstUniqueConstraint(orbTypeInternalId, attributeName).build();
		defLimitationManager.addToCollection(criteria);
	}
	
	public void addNotAmongstUniqueConstraintOnField(long orbTypeInternalId, String attributeName) {
		
		Criteria criteria = new CriteriaBuilder(orbTypeInternalId).addNotAmongstUniqueConstraint(orbTypeInternalId, attributeName).build();
		defLimitationManager.addToCollection(criteria);
	}
}
