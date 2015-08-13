package com.fletch22.orb.cache.local;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.unique.UniqueIndex;

public class OrbSingleTypesInstanceCollection {
	
	Logger logger = LoggerFactory.getLogger(OrbSingleTypesInstanceCollection.class);

	IndexedCollection<CacheEntry> instances = new ConcurrentIndexedCollection<CacheEntry>();
	private long orbTypeInternalId;

	public OrbSingleTypesInstanceCollection(long orbTypeInternalId) {
		this.orbTypeInternalId = orbTypeInternalId;
		instances.addIndex(UniqueIndex.onAttribute(CacheEntry.ID));
	}

	private void createCacheEntry(long id, String label, BigDecimal tranDate, List<String> customFieldValues) {
		CacheEntry cacheEntry = new CacheEntry(id, orbTypeInternalId, label, tranDate, customFieldValues);
		instances.add(cacheEntry);
	}

	public void addInstance(long id, String label, BigDecimal tranDate, List<String> customFieldValues) {
		createCacheEntry(id, label, tranDate, customFieldValues);
	}
	
	public void addInstanceFieldToAllInstances(String value) {
		for (CacheEntry cacheEntry: instances) {
			cacheEntry.attributes.add(value);
		}
	}
	
	public void addInstanceFieldToAllInstances() {
		addInstanceFieldToAllInstances(null);
	}
	
	public void removeInstance(long id) {
		for (CacheEntry cacheEntry: instances) {
			if (cacheEntry.getId() == id) {
				instances.remove(cacheEntry);
			}
		}
	}

	public void removeInstanceFieldFromAllInstances(int indexOfField) {
		for (CacheEntry cacheEntry: instances) {
			cacheEntry.attributes.remove(indexOfField);
		}
	}
}
