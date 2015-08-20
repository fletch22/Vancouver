package com.fletch22.orb.cache.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;

public class OrbCollection {
	
	Logger logger = LoggerFactory.getLogger(OrbCollection.class);

	Map<Long, OrbSingleTypesInstanceCollection> allInstances = new HashMap<Long, OrbSingleTypesInstanceCollection>();
	private Map<Long, OrbSteamerTrunk> quickLookup = new HashMap<Long, OrbSteamerTrunk>();
	
	public void add(OrbType orbType, Orb orb) {
		OrbSingleTypesInstanceCollection orbSingleTypesInstanceCollection = allInstances.get(orb.getOrbTypeInternalId());
		
		if (orbSingleTypesInstanceCollection == null) {
			logger.info("Type not found in single type instance collection. Creating new single type instance collection.");
			orbSingleTypesInstanceCollection = new OrbSingleTypesInstanceCollection(orbType.id);
			allInstances.put(orbType.id, orbSingleTypesInstanceCollection);
		}
		
		ArrayList<String> fields = getPropertyValuesInOrder(orbType, orb);
		CacheEntry cacheEntry = orbSingleTypesInstanceCollection.addInstance(orb.getOrbInternalId(), null, orb.getTranDate(), fields);
		
		OrbSteamerTrunk orbSteamerTrunk = new OrbSteamerTrunk(orb, cacheEntry);
		
		quickLookup.put(orb.getOrbInternalId(), orbSteamerTrunk);
	}
	
	private ArrayList<String> getPropertyValuesInOrder(OrbType orbType, Orb orb) {
		Map<String, String> properties = orb.getUserDefinedProperties();
		ArrayList<String> fieldValues = new ArrayList<String>();
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
		Orb orb = quickLookup.get(orbInternalId).orb;
		
		if (orb == null) throw new RuntimeException("Encountered problem getting orb. Couldn't find orb with id '" + orbInternalId + "'.");
		
		return orb;
	}
	
	public Orb delete(long orbInternalId) {
		Orb orb = quickLookup.get(orbInternalId).orb;
		
		OrbSingleTypesInstanceCollection orbSingleTypesInstanceCollection = allInstances.get(orb.getOrbTypeInternalId());
		orbSingleTypesInstanceCollection.removeInstance(orb.getOrbInternalId());
		
		quickLookup.remove(orbInternalId);
		
		return orb;
	}
	
	public void addAttribute(long orbTypeInternalId, String name) {
		
		if (allInstances.containsKey(orbTypeInternalId)) {
			OrbSingleTypesInstanceCollection orbSingleTypesInstanceCollection = allInstances.get(orbTypeInternalId);
			for (CacheEntry cacheEntry:  orbSingleTypesInstanceCollection.instances) {
				Orb orb = quickLookup.get(cacheEntry.id).orb;
				orb.getUserDefinedProperties().put(name, null);
				
				cacheEntry.attributes.add(null);
			}
		}
	}
	
	public Map<Long, String> removeAttribute(long orbTypeInternalId, int indexOfAttribute, String name) {
		Map<Long, String> mapRemoved = new HashMap<Long, String>();
		
		OrbSingleTypesInstanceCollection orbSingleTypesInstanceCollection = allInstances.get(orbTypeInternalId);
		
		for (CacheEntry cacheEntry:  orbSingleTypesInstanceCollection.instances) {
			Orb orb = quickLookup.get(cacheEntry.id).orb;
			
			LinkedHashMap<String, String> properties = orb.getUserDefinedProperties();
			mapRemoved.put(orb.getOrbInternalId(), properties.get(name));
			
			properties.remove(name);
			cacheEntry.attributes.remove(indexOfAttribute);
		}
		
		return mapRemoved;
	}
	
	public void addAttributeValues(Map<Long, String> map, long orbTypeInternalId, int indexAttribute, String attributeName) {
		
		Set<Long> orbInternalIdSet = map.keySet();
		for (long orbInternalId: orbInternalIdSet) {
			OrbSteamerTrunk orbSteamerTrunk = quickLookup.get(orbInternalId);
			
			String value = map.get(orbInternalId);
			LinkedHashMap<String, String> properties = orbSteamerTrunk.orb.getUserDefinedProperties();
			properties.put(attributeName, value);
			
			orbSteamerTrunk.cacheEntry.attributes.add(indexAttribute, value);
		}
	}
	
	public Map<Long, OrbSteamerTrunk> getQuickLookup() {
		return quickLookup;
	}

	public boolean doesOrbExist(long orbInternalId) {
		return quickLookup.containsKey(orbInternalId);
	}

	public int getCount() {
		return quickLookup.size();
	}
	
	public static class OrbSteamerTrunk {
		CacheEntry cacheEntry;
		Orb orb;
		
		public OrbSteamerTrunk(Orb orb, CacheEntry cacheEntry) {
			this.orb = orb;
			this.cacheEntry = cacheEntry;
		}
	}
}
