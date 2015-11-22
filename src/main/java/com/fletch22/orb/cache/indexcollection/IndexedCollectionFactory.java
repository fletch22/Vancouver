package com.fletch22.orb.cache.indexcollection;

import org.springframework.stereotype.Component;

import com.fletch22.orb.cache.local.CacheEntry;
import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.unique.UniqueIndex;

@Component	
public class IndexedCollectionFactory {

	public IndexedCollection<CacheEntry> createInstance() {

		IndexedCollection<CacheEntry> indexedCollection = new ConcurrentIndexedCollection<CacheEntry>();
		indexedCollection.addIndex(UniqueIndex.onAttribute(CacheEntry.ID));
		
		return indexedCollection;
	}
}
