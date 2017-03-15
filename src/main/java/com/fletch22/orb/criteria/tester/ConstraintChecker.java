package com.fletch22.orb.criteria.tester;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;
import com.fletch22.orb.cache.indexcollection.IndexedCollectionFactory;
import com.fletch22.orb.cache.local.CacheEntry;
import com.fletch22.orb.query.constraint.ConstraintGrinder;
import com.fletch22.orb.query.criteria.Criteria;
import com.googlecode.cqengine.IndexedCollection;

@Component
public class ConstraintChecker {

	static Logger logger = LoggerFactory.getLogger(ConstraintChecker.class);

	@Autowired
	IndexedCollectionFactory indexedCollectionFactory;
	
	@Autowired
	ConstraintDescriptionRefiner constraintDescriptionRefiner;

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

		ConstraintGrinder criteriaGrinder = new ConstraintGrinder(criteria, indexedCollection, orb);
		
		if (criteriaGrinder.listCacheEntries().size() == 0) {
			StringBuffer description = this.constraintDescriptionRefiner.refine(criteria.getDescription());
			throw new F22ConstraintViolationException(description);
		}
	}
}
