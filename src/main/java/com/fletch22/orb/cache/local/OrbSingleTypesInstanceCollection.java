package com.fletch22.orb.cache.local;

import java.math.BigDecimal;
import java.util.List;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.unique.UniqueIndex;

public class OrbSingleTypesInstanceCollection {

	IndexedCollection<CacheEntry> instances = new ConcurrentIndexedCollection<CacheEntry>();
	private int orbTypeInternalId;

	public OrbSingleTypesInstanceCollection(int orbTypeInternalId) {
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
	
	public void removeInstanceField(int index) {
		for (CacheEntry cacheEntry: instances) {
			cacheEntry.list.remove(index);
		}
	}
}
