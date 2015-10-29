package com.fletch22.app.designer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.cache.reference.ReferenceUtil;
import com.fletch22.orb.query.OrbResultSet;
import com.fletch22.orb.query.QueryManager;

public abstract class AppDesignerDao<T> {
	
	@Autowired
	public OrbManager orbManager;

	@Autowired
	public ReferenceUtil referenceUtil;
	
	@Autowired
	protected OrbTypeManager orbTypeManager;
	
	@Autowired
	protected QueryManager queryManager;

	public StringBuffer convertToReferences(ArrayList<? extends OrbBasedComponent> list) {
		Set<String> refSet = new HashSet<String>();
		for (OrbBasedComponent orbBasedComponent : list) {
			refSet.add(referenceUtil.composeReference(orbBasedComponent.getId()));
		}
		
		return referenceUtil.composeReferences(refSet);
	}
	
	public void delete(long id) {
		orbManager.deleteOrb(id, false);
	}
	
	protected OrbType ensureInstanceUnique(String typeLabel, String attributeName, String attributeValue)  {
		
		OrbType orbType = this.orbTypeManager.getOrbType(typeLabel);
		OrbResultSet orbResultSet = this.queryManager.findByAttribute(orbType.id, attributeName, attributeValue);

		if (orbResultSet.getOrbList().size() > 0) {
			String message = String.format("Encountered problem while tyring to create an %s instance. Found more than one instance.", typeLabel);
			throw new RuntimeException(message);
		}
		
		return orbType;
	}

	protected Orb getOrbMustExist(long orbInternalId) {
		Orb orb = this.orbManager.getOrb(orbInternalId);
	
		if (orb == null) {
			throw new RuntimeException("Encountered problem trying to find AppContainer orb type. Could not find orb.");
		}
		
		return orb;
	}
}
