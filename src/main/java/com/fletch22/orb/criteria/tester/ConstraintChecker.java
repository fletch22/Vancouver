package com.fletch22.orb.criteria.tester;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;
import com.fletch22.orb.cache.indexcollection.IndexedCollectionFactory;
import com.fletch22.orb.cache.local.CacheEntry;
import com.fletch22.orb.query.CriteriaFactory.Criteria;
import com.fletch22.orb.query.constraint.ConstraintGrinder;
import com.googlecode.cqengine.IndexedCollection;

@Component
public class ConstraintChecker {
	
	static Logger logger = LoggerFactory.getLogger(ConstraintChecker.class);
	
	@Autowired
	IndexedCollectionFactory indexedCollectionFactory;
	
	IndexedCollection<CacheEntry> indexedCollection;

	private IndexedCollection<CacheEntry> getIndexedCollection() {
		if (indexedCollection == null) {
			indexedCollection = indexedCollectionFactory.createInstance();
		}
		indexedCollection.clear();
		return indexedCollection;
	}

	public void checkConstraint(Criteria criteria, Orb orb) {
		CacheEntry cacheEntry = new CacheEntry(orb);
		getIndexedCollection().add(cacheEntry);
		
		ConstraintGrinder criteriaGrinder = new ConstraintGrinder(criteria, indexedCollection);
		
		if (criteriaGrinder.listCacheEntries().size() == 0) {
			throw new RuntimeException("Check constraint fails validation.");
		}
	}
}
