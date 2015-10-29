package com.fletch22.orb.cache.local;

import java.util.ArrayList;

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

	private CacheEntry createCacheEntry(long id, String label, ArrayList<String> customFieldValues) {
		CacheEntry cacheEntry = new CacheEntry(id, orbTypeInternalId, label, customFieldValues);
		instances.add(cacheEntry);
		return cacheEntry;
	}

	public CacheEntry addInstance(long id, String label, ArrayList<String> customFieldValues) {
		return createCacheEntry(id, label, customFieldValues);
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
