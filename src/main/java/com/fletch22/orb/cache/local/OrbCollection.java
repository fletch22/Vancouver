package com.fletch22.orb.cache.local;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.cache.local.OrbTypeCollection.OrbType;

@Component
public class OrbCollection {
	
	Logger logger = LoggerFactory.getLogger(OrbCollection.class);

	Map<Long, OrbSingleTypesInstanceCollection> allInstances = new HashMap<Long, OrbSingleTypesInstanceCollection>();
	Map<Long, Orb> quickLookup = new HashMap<Long, Orb>();
	
	@Autowired
	OrbTypeCollection orbTypeCollection;
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	OrbManager orbManager;

	public void add(Orb orb) {
		OrbSingleTypesInstanceCollection orbSingleTypesInstanceCollection = allInstances.get(orb.getOrbTypeInternalId());
		List<String> fields = getPropertyValuesInOrder(orb);
		orbSingleTypesInstanceCollection.addInstance(orb.getOrbInteralId(), null, orb.getTranDate(), fields);
		quickLookup.put(orb.getOrbInteralId(), orb);
	}
	
	public Orb add(long orbInternalId, long orbTypeInternalId, BigDecimal tranDate) {
		OrbSingleTypesInstanceCollection orbSingleTypesInstanceCollection = allInstances.get(orbTypeInternalId);
		
		if (orbSingleTypesInstanceCollection == null) {
			logger.info("Type not found. Creating new type.");
			orbSingleTypesInstanceCollection = new OrbSingleTypesInstanceCollection(orbTypeInternalId);
			allInstances.put(orbTypeInternalId, orbSingleTypesInstanceCollection);
		}

		orbSingleTypesInstanceCollection.addInstance(orbInternalId, null, tranDate, new ArrayList<String>());
		
		Orb orb = new Orb(orbInternalId, orbTypeInternalId, tranDate, new LinkedHashMap<String, String>());
		quickLookup.put(orb.getOrbInteralId(), orb);
		
		return orb;
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

	public void deleteAll() {
		allInstances.clear();
		quickLookup.clear();
	}

	public Orb get(long orbInternalId) {
		return quickLookup.get(orbInternalId);
	}
	
	public void addAttribute(long orbTypeInternalId, String name) {
		
		if (allInstances.containsKey(orbTypeInternalId)) {
			OrbSingleTypesInstanceCollection orbSingleTypesInstanceCollection = allInstances.get(orbTypeInternalId);
			for (CacheEntry cacheEntry:  orbSingleTypesInstanceCollection.instances) {
				Orb orb = quickLookup.get(cacheEntry.id);
				orb.getUserDefinedProperties().put(name, null);
				
				cacheEntry.attributes.add(null);
			}
		}
	}
	
	public void removeAttribute(long orbTypeInternalId, int indexOfAttribute, String name) {
		
		OrbSingleTypesInstanceCollection orbSingleTypesInstanceCollection = allInstances.get(orbTypeInternalId);
		
		for (CacheEntry cacheEntry:  orbSingleTypesInstanceCollection.instances) {
			Orb orb = quickLookup.get(cacheEntry.id);
			orb.getUserDefinedProperties().remove(name);
			cacheEntry.attributes.remove(indexOfAttribute);
		}
	}
}
