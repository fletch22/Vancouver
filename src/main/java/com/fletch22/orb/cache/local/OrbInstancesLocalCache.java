package com.fletch22.orb.cache.local;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;
import com.fletch22.orb.cache.local.OrbTypeCollection.OrbType;

@Component
public class OrbInstancesLocalCache {

	Map<Long, OrbSingleTypesInstanceCollection> allInstances = new HashMap<Long, OrbSingleTypesInstanceCollection>();
	
	@Autowired
	OrbTypeCollection orbTypeCollection;

	public void add(Orb orb) {
		OrbSingleTypesInstanceCollection orbSingleTypesInstanceCollection = allInstances.get(orb.getOrbTypeInternalId());
		List<String> fields = getPropertyValuesInOrder(orb);
		orbSingleTypesInstanceCollection.addInstance(orb.getOrbInteralId(), null, orb.getTranDate(), fields);
	}
	
	public void add(long orbInternalId, long orbTypeInternalId, BigDecimal tranDate) {
		OrbSingleTypesInstanceCollection orbSingleTypesInstanceCollection = allInstances.get(orbTypeInternalId);
		orbSingleTypesInstanceCollection.addInstance(orbInternalId, null, tranDate, null);
	}

	private List<String> getPropertyValuesInOrder(Orb orb) {
		OrbType orbType = orbTypeCollection.get(orb.getOrbTypeInternalId());
		
		Map<String, String> properties = orb.getUserDefinedProperties();
		List<String> fieldValues = new ArrayList<String>();
		for (String field : orbType.customFields) {
			fieldValues.add(properties.get(field));
		}
		
		return fieldValues;
	}

}
