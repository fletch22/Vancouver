package com.fletch22.orb.query.constraint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.Fletch22ApplicationContext;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.cache.local.CacheEntry;
import com.fletch22.orb.cache.local.OrbCollection;
import com.fletch22.orb.query.LogicalConstraint;
import com.fletch22.orb.query.OrbResultSet;
import com.fletch22.orb.query.criteria.Criteria;
import com.fletch22.orb.query.sort.CriteriaSortInfo;
import com.fletch22.orb.query.sort.GrinderSortInfo;
import com.fletch22.orb.query.sort.OrbComparator;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.QueryFactory;
import com.googlecode.cqengine.resultset.ResultSet;

public class ConstraintGrinder {

	Logger logger = LoggerFactory.getLogger(ConstraintGrinder.class);

	Criteria criteria;
	IndexedCollection<CacheEntry> indexedCollection;
	long orbTypeInternalId;

	Query<CacheEntry> query = null;
	ConstraintKitchen constraintKitchen;

	private OrbTypeManager orbTypeManager;

	private OrbCollection orbCollection;

	private ConstraintProcessor constraintProcessor;

	public ConstraintGrinder(Criteria criteria, IndexedCollection<CacheEntry> indexedCollection) {
		this.criteria = criteria;
		this.indexedCollection = indexedCollection;
		this.orbTypeInternalId = criteria.getOrbTypeInternalId();
		this.constraintKitchen = (ConstraintKitchen) Fletch22ApplicationContext.getApplicationContext().getBean(ConstraintKitchen.class);
		this.orbTypeManager = (OrbTypeManager) Fletch22ApplicationContext.getApplicationContext().getBean(OrbTypeManager.class);
		this.orbCollection = (OrbCollection) Fletch22ApplicationContext.getApplicationContext().getBean(Cache.class).orbCollection;
		this.constraintProcessor = (ConstraintProcessor) Fletch22ApplicationContext.getApplicationContext().getBean(ConstraintProcessor.class);

		if (criteria.logicalConstraint == null) {
			this.query = QueryFactory.all(CacheEntry.class);
		} else {
			this.query = processConstraint(criteria.logicalConstraint);
		}
	}

	public List<CacheEntry> listCacheEntries() {

		ResultSet<CacheEntry> resultSet = this.indexedCollection.retrieve(query);

		List<CacheEntry> cacheEntryList = new ArrayList<CacheEntry>();
		for (CacheEntry cacheEntry : resultSet) {
			cacheEntryList.add(cacheEntry);
		}

		return cacheEntryList;
	}

	public OrbResultSet list() {

		ResultSet<CacheEntry> resultSetCacheEntries = this.indexedCollection.retrieve(query);

		List<Orb> orbList = new ArrayList<Orb>(resultSetCacheEntries.size());

		for (CacheEntry cacheEntry : resultSetCacheEntries) {
			Orb orb = orbCollection.get(cacheEntry.getId());
			orbList.add(orb);
		}

		if (criteria.hasSortCriteria()) {

			List<CriteriaSortInfo> criteriaSortInfoList = criteria.getSortInfoList();
			List<GrinderSortInfo> grinderSortInfoList = new ArrayList<GrinderSortInfo>();
			for (CriteriaSortInfo criteriaSortInfo : criteriaSortInfoList) {
				GrinderSortInfo grinderSortInfo = new GrinderSortInfo();
				grinderSortInfo.sortDirection = criteriaSortInfo.sortDirection;
				grinderSortInfo.sortType = criteriaSortInfo.sortType;
				grinderSortInfo.sortIndex = orbTypeManager.getIndexOfAttribute(criteria.getOrbTypeInternalId(), criteriaSortInfo.sortAttributeName);
				grinderSortInfoList.add(grinderSortInfo);
			}

			OrbType orbType = orbTypeManager.getOrbType(this.orbTypeInternalId);
			OrbComparator rowComparator = new OrbComparator(grinderSortInfoList, orbType);
			Collections.sort(orbList, rowComparator);
		}

		return new OrbResultSet(orbList);
	}

	private Query<CacheEntry> processConstraint(LogicalConstraint logicalConstraint) {
		return logicalConstraint.acceptConstraintProcessorVisitor(this.constraintProcessor, this.orbTypeInternalId);
	}
}
