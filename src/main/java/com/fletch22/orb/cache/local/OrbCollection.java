package com.fletch22.orb.cache.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.cache.local.OrbReference.AttributeArrows;
import com.fletch22.orb.cache.local.OrbReference.DecomposedKey;
import com.fletch22.orb.query.ConstraintGrinder;
import com.fletch22.orb.query.CriteriaFactory.Criteria;

public class OrbCollection {
	
	Logger logger = LoggerFactory.getLogger(OrbCollection.class);
	
	public OrbReference orbReference;

	Map<Long, OrbSingleTypesInstanceCollection> allInstances = new HashMap<Long, OrbSingleTypesInstanceCollection>();
	private Map<Long, OrbSteamerTrunk> quickLookup = new HashMap<Long, OrbSteamerTrunk>();
	
	public void add(OrbType orbType, Orb orb) {
		OrbSingleTypesInstanceCollection orbSingleTypesInstanceCollection = allInstances.get(orb.getOrbTypeInternalId());
		
		if (orbSingleTypesInstanceCollection == null) {
			logger.debug("Type not found in single type instance collection. Creating new single type instance collection.");
			orbSingleTypesInstanceCollection = new OrbSingleTypesInstanceCollection(orbType.id);
			allInstances.put(orbType.id, orbSingleTypesInstanceCollection);
		}
		
		ArrayList<String> fields = getPropertyValuesInOrder(orbType, orb);
		CacheEntry cacheEntry = orbSingleTypesInstanceCollection.addInstance(orb.getOrbInternalId(), null, orb.getTranDate(), fields);
		
		OrbSteamerTrunk orbSteamerTrunk = new OrbSteamerTrunk(orb, cacheEntry);
		
		quickLookup.put(orb.getOrbInternalId(), orbSteamerTrunk);
	}
	
	public List<CacheEntry> executeQuery(Criteria criteria) {
		
		OrbSingleTypesInstanceCollection orbSingleTypesInstanceCollection = allInstances.get(criteria.getOrbTypeInternalId());
		
		ConstraintGrinder criteriaGrinder = new ConstraintGrinder(criteria.getOrbTypeInternalId(), criteria.logicalConstraintsList, orbSingleTypesInstanceCollection.instances);
		
		return criteriaGrinder.list();
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
		orbReference.clear();
		allInstances.clear();
		quickLookup.clear();
	}

	public Orb get(long orbInternalId) {
		Orb orb = quickLookup.get(orbInternalId).orb;
		
		if (orb == null) throw new RuntimeException("Encountered problem getting orb. Couldn't find orb with id '" + orbInternalId + "'.");
		
		return orb;
	}
	
	public OrbSteamerTrunk getOrbSteamerTrunk(long orbInternalId) {
		OrbSteamerTrunk orbSteamerTrunk = quickLookup.get(orbInternalId);
		
		if (orbSteamerTrunk == null) throw new RuntimeException("Encountered problem getting orb. Couldn't find orb with id '" + orbInternalId + "'.");
		
		return orbSteamerTrunk;
	}
	
	public Map<Long, AttributeArrows> getReferencesToOrb(Orb orb) {
		return orbReference.getArrowsPointingAtTarget(orb);
	}
	
	public Orb delete(OrbType orbType, long orbInternalId) {
		Orb orb = quickLookup.get(orbInternalId).orb;
		
		orbReference.ensureOrbsArrowsRemoved(orb);
		
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
	
	public Map<Long, String> removeAttribute(long orbTypeInternalId, int indexOfAttribute, String attributeName) {
		Map<Long, String> mapRemoved = new HashMap<Long, String>();
		
		OrbSingleTypesInstanceCollection orbSingleTypesInstanceCollection = allInstances.get(orbTypeInternalId);
		
		orbReference.removeTarget(orbTypeInternalId, attributeName);
		
		for (CacheEntry cacheEntry:  orbSingleTypesInstanceCollection.instances) {
			Orb orb = quickLookup.get(cacheEntry.id).orb;
			
			LinkedHashMap<String, String> properties = orb.getUserDefinedProperties();
			
			String attributeValue = properties.get(attributeName);
			
			if (orbReference.isValueAReference(attributeValue)) {
				orbReference.removeArrowsFromIndex(orb.getOrbInternalId(), attributeName, attributeValue);
			}
			
			mapRemoved.put(orb.getOrbInternalId(), attributeValue);
			
			properties.remove(attributeName);
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
			
			if (orbReference.isValueAReference(value)) {
				orbReference.addReferences(orbInternalId, attributeName, value);
			}
			
			orbSteamerTrunk.cacheEntry.attributes.set(indexAttribute, value);
		}
	}
	
	public String setAttribute(long orbInternalId, String attributeName, String value) {
		OrbSteamerTrunk orbSteamerTrunk = getOrbSteamerTrunk(orbInternalId);
		
		Orb orb = orbSteamerTrunk.orb;
		String oldValue = orb.getUserDefinedProperties().get(attributeName);

		if (!areEqualAttributes(oldValue, value)) {
			boolean isNewValueAReference = orbReference.isValueAReference(value);
			if (isNewValueAReference) {
				if (isTargetAttributeValueAlreadyAReference(value)) {
					throw new RuntimeException("A reference can't point to another reference. I don't know why but that's not allowed. Figure it out later.");
				}
			}
			
			if (orbReference.isValueAReference(oldValue)) {
				orbReference.removeArrowsFromIndex(orbInternalId, attributeName, value);
			}

			if (isNewValueAReference) {
				orbReference.addReferences(orbInternalId, attributeName, value);
			}

			orb.getUserDefinedProperties().put(attributeName, value);
			
			Set<String> set = orb.getUserDefinedProperties().keySet();
			
			int indexAttribute = 0;
			boolean isFound = false;
			for (String key : set) {
				if (key.equals(attributeName)) {
					isFound = true;
					break;
				}
				indexAttribute++;
			}
			
			if (!isFound) {
				throw new RuntimeException("Could not find attribute in list of attributes.");
			}
			
			orbSteamerTrunk.cacheEntry.attributes.set(indexAttribute, value);
		}
		
		return oldValue;
	}
	
	private boolean isTargetAttributeValueAlreadyAReference(String referenceValue) {
		boolean isReference = false;
		
		DecomposedKey decomposedKey = orbReference.decomposeKey(referenceValue);
		
		Orb orb = get(decomposedKey.orbInternalId);
		
		String targetValue = orb.getUserDefinedProperties().get(decomposedKey.attributeName);
		
		if (orbReference.isValueAReference(targetValue)) {
			isReference = true;
		}
		
		return isReference;
	}

	private boolean areEqualAttributes(String value1, String value2) {
		return (value1 == null ? value2 == null : value1.equals(value2));
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

	public List<Orb> getOrbsWithType(long orbTypeInternalId) {
		OrbSingleTypesInstanceCollection orbSingleTypeInstancesCollection = allInstances.get(orbTypeInternalId);
		
		List<Orb> orbsWithType = new ArrayList<Orb>();
		for (CacheEntry cacheEntry: orbSingleTypeInstancesCollection.instances) {
			OrbSteamerTrunk trunk = quickLookup.get(cacheEntry.id);
			orbsWithType.add(trunk.orb);
		}
		
		return orbsWithType;
	}

	public void renameAttribute(long orbTypeInternalId, String attributeNameOld, String attributeNameNew) {
		
		OrbSingleTypesInstanceCollection orbSingleTypeInstancesCollection = allInstances.get(orbTypeInternalId);
		
		for (CacheEntry cacheEntry: orbSingleTypeInstancesCollection.instances) {
			
			OrbSteamerTrunk trunk = quickLookup.get(cacheEntry.id);
			Orb orb = trunk.orb;
			orbReference.referenceCollection.renameAttribute(orb.getOrbInternalId(), attributeNameOld, attributeNameNew);
			
			LinkedHashMap<String, String> linkedHashMap = orb.getUserDefinedProperties();
			String value = linkedHashMap.remove(attributeNameOld);
			linkedHashMap.put(attributeNameNew, value);
		}
	}
}
