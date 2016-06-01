package com.fletch22.orb.cache.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.cache.local.OrbSingleTypesInstanceCollectionFactory.OrbSingleTypesInstanceCollection;
import com.fletch22.orb.cache.reference.DecomposedKey;
import com.fletch22.orb.cache.reference.OrbReference;
import com.fletch22.orb.cache.reference.ReferenceUtil;
import com.fletch22.orb.query.constraint.ConstraintGrinder;
import com.fletch22.orb.query.criteria.Criteria;
import com.fletch22.orb.query.OrbResultSet;

@Component
@Scope("prototype")
public class OrbCollection {

	Logger logger = LoggerFactory.getLogger(OrbCollection.class);

	@Autowired
	public OrbReference orbReference;

	@Autowired
	public ReferenceUtil referenceUtil;

	@Autowired
	OrbSingleTypesInstanceCollectionFactory orbSingleTypesInstanceCollectionFactory;
	
	public boolean touchFlag = false;

	Map<Long, OrbSingleTypesInstanceCollection> allInstances = new HashMap<Long, OrbSingleTypesInstanceCollection>();
	private Map<Long, OrbSteamerTrunk> quickLookup = new HashMap<Long, OrbSteamerTrunk>();

	public void add(OrbType orbType, Orb orb) {
		OrbSingleTypesInstanceCollection orbSingleTypesInstanceCollection = allInstances.get(orb.getOrbTypeInternalId());

		if (orbSingleTypesInstanceCollection == null) {
			logger.debug("Type not found in single type instance collection. Creating new single type instance collection.");

			orbSingleTypesInstanceCollection = orbSingleTypesInstanceCollectionFactory.createInstance(orbType.id);
			allInstances.put(orbType.id, orbSingleTypesInstanceCollection);
		}

		ArrayList<String> fields = getPropertyValuesInOrder(orbType, orb);

		CacheEntry cacheEntry = orbSingleTypesInstanceCollection.addInstance(orb.getOrbInternalId(), null, fields);

		OrbSteamerTrunk orbSteamerTrunk = new OrbSteamerTrunk(orb, cacheEntry);

		quickLookup.put(orb.getOrbInternalId(), orbSteamerTrunk);

		for (String attributeName : fields) {
			setAttribute(orb.getOrbInternalId(), attributeName, orb.getUserDefinedProperties().get(attributeName));
		}
	}

	public OrbResultSet executeQuery(Criteria criteria) {

		OrbSingleTypesInstanceCollection orbSingleTypesInstanceCollection = allInstances.get(criteria.getOrbTypeInternalId());

		OrbResultSet orbResultSet = null;
		if (orbSingleTypesInstanceCollection == null) {
			orbResultSet = new OrbResultSet();
		} else {
			ConstraintGrinder criteriaGrinder = new ConstraintGrinder(criteria, orbSingleTypesInstanceCollection.instances);
			orbResultSet = criteriaGrinder.list();
		}

		return orbResultSet;
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

		OrbSteamerTrunk orbSteamerTrunk = quickLookup.get(orbInternalId);

		if (orbSteamerTrunk == null || orbSteamerTrunk.orb == null) {
			throw new RuntimeException("Encountered problem getting orb. Couldn't find orb with id '" + orbInternalId + "'.");
		}

		return orbSteamerTrunk.orb;
	}

	public OrbSteamerTrunk getOrbSteamerTrunk(long orbInternalId) {
		OrbSteamerTrunk orbSteamerTrunk = quickLookup.get(orbInternalId);

		if (orbSteamerTrunk == null)
			throw new RuntimeException("Encountered problem getting orb. Couldn't find orb with id '" + orbInternalId + "'.");

		return orbSteamerTrunk;
	}

	public Map<Long, Set<String>> getAttributeReferencesToOrb(Orb orb) {
		Map<Long, AttributeArrows> arrows = orbReference.getArrowsPointingAtTarget(orb);

		Map<Long, Set<String>> attributeMap = new HashMap<Long, Set<String>>();
		Set<Long> arrowKeys = arrows.keySet();
		for (long orbInternalIdArrow : arrowKeys) {
			AttributeArrows attributeArrows = arrows.get(orbInternalIdArrow);
			attributeMap.put(orbInternalIdArrow, attributeArrows.attributesContainingArrows);
		}

		return attributeMap;
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
			for (CacheEntry cacheEntry : orbSingleTypesInstanceCollection.instances) {
				Orb orb = quickLookup.get(cacheEntry.id).orb;
				orb.getUserDefinedProperties().put(name, null);

				cacheEntry.attributes.add(null);
			}
		}
	}

	public Map<Long, String> removeAttribute(long orbTypeInternalId, int indexOfAttribute, String attributeName) {
		Map<Long, String> mapRemoved = new HashMap<Long, String>();

		OrbSingleTypesInstanceCollection orbSingleTypesInstanceCollection = allInstances.get(orbTypeInternalId);

		if (orbSingleTypesInstanceCollection != null 
		&& orbSingleTypesInstanceCollection.instances.size() > 0) {
			removeAttributeWhenInstanceExists(orbTypeInternalId, indexOfAttribute, attributeName, mapRemoved, orbSingleTypesInstanceCollection);
		}

		return mapRemoved;
	}

	private void removeAttributeWhenInstanceExists(long orbTypeInternalId, int indexOfAttribute, String attributeName, Map<Long, String> mapRemoved, OrbSingleTypesInstanceCollection orbSingleTypesInstanceCollection) {
		orbReference.removeTargetAttribute(orbTypeInternalId, attributeName);

		for (CacheEntry cacheEntry : orbSingleTypesInstanceCollection.instances) {
			Orb orb = quickLookup.get(cacheEntry.id).orb;

			LinkedHashMap<String, String> properties = orb.getUserDefinedProperties();

			String attributeValue = properties.get(attributeName);

			if (referenceUtil.isValueAReference(attributeValue)) {
				orbReference.removeArrowsFromIndex(orb.getOrbInternalId(), attributeName, attributeValue);
			}

			mapRemoved.put(orb.getOrbInternalId(), attributeValue);

			properties.remove(attributeName);
			cacheEntry.attributes.remove(indexOfAttribute);
		}
	}

	public void addAttributeValues(Map<Long, String> map, long orbTypeInternalId, int indexAttribute, String attributeName) {

		Set<Long> orbInternalIdSet = map.keySet();
		for (long orbInternalId : orbInternalIdSet) {
			OrbSteamerTrunk orbSteamerTrunk = quickLookup.get(orbInternalId);

			String value = map.get(orbInternalId);
			LinkedHashMap<String, String> properties = orbSteamerTrunk.orb.getUserDefinedProperties();
			properties.put(attributeName, value);

			if (referenceUtil.isValueAReference(value)) {
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
			boolean isNewValueAReference = referenceUtil.isValueAReference(value);
			if (isNewValueAReference) {
				List<DecomposedKey> keys = this.orbReference.convertToDecomposedKeys(value);
				for (DecomposedKey decomposeKey: keys) {
					if (decomposeKey.isKeyPointingToAttribute()) {
						if (isTargetAttributeValueAlreadyAReference(value)) {
							throw new RuntimeException("A reference can't point to another reference. I don't know why but that's not allowed. Figure it out later.");
						}	
					}
				}
			}

			if (referenceUtil.isValueAReference(oldValue)) {
				logger.debug("setAttribute oldValue: {}", oldValue);
				orbReference.removeArrowsFromIndex(orbInternalId, attributeName, oldValue);
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

		DecomposedKey decomposedKey = referenceUtil.decomposeKey(referenceValue);

		if (decomposedKey.isKeyPointingToAttribute()) {

			Orb orb = get(decomposedKey.getOrbInternalId());

			String targetValue = orb.getUserDefinedProperties().get(decomposedKey.getAttributeName());

			if (referenceUtil.isValueAReference(targetValue)) {
				isReference = true;
			}
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

		if (orbSingleTypeInstancesCollection != null) {
			for (CacheEntry cacheEntry : orbSingleTypeInstancesCollection.instances) {
				OrbSteamerTrunk trunk = quickLookup.get(cacheEntry.id);
				orbsWithType.add(trunk.orb);
			}
		}

		return orbsWithType;
	}

	public void renameAttribute(long orbTypeInternalId, String attributeNameOld, String attributeNameNew) {

		OrbSingleTypesInstanceCollection orbSingleTypeInstancesCollection = allInstances.get(orbTypeInternalId);

		if (orbSingleTypeInstancesCollection != null) {
			for (CacheEntry cacheEntry : orbSingleTypeInstancesCollection.instances) {
	
				OrbSteamerTrunk trunk = quickLookup.get(cacheEntry.id);
				Orb orb = trunk.orb;
				orbReference.referenceCollection.renameAttributeReference(orb.getOrbInternalId(), attributeNameOld, attributeNameNew);
	
				LinkedHashMap<String, String> linkedHashMap = orb.getUserDefinedProperties();
				String value = linkedHashMap.remove(attributeNameOld);
				linkedHashMap.put(attributeNameNew, value);
			}
		}
	}

	public long getCountOrbsOfType(long orbTypeInternalId) {
		OrbSingleTypesInstanceCollection orbSingleTypeInstancesCollection = allInstances.get(orbTypeInternalId);

		long count = 0;
		if (orbSingleTypeInstancesCollection != null) {
			count = orbSingleTypeInstancesCollection.instances.size();
		}

		return count;
	}

	public boolean doesReferenceToOrbExist(Orb orb) {
		return orbReference.countArrowsPointToTarget(orb) > 0;
	}
}
