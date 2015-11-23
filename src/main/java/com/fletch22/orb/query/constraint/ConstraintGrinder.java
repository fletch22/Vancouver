package com.fletch22.orb.query.constraint;

import static com.googlecode.cqengine.query.QueryFactory.and;
import static com.googlecode.cqengine.query.QueryFactory.equal;
import static com.googlecode.cqengine.query.QueryFactory.in;
import static com.googlecode.cqengine.query.QueryFactory.or;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.Fletch22ApplicationContext;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.cache.local.CacheEntry;
import com.fletch22.orb.cache.local.OrbCollection;
import com.fletch22.orb.query.CriteriaFactory;
import com.fletch22.orb.query.LogicalConstraint;
import com.fletch22.orb.query.LogicalOperator;
import com.fletch22.orb.query.OrbResultSet;
import com.fletch22.orb.query.RelationshipOperator;
import com.fletch22.orb.query.CriteriaFactory.Criteria;
import com.fletch22.orb.query.sort.CriteriaSortInfo;
import com.fletch22.orb.query.sort.GrinderSortInfo;
import com.fletch22.orb.query.sort.OrbComparator;
import com.fletch22.orb.search.GeneratedCacheEntryClassFactory;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.SimpleNullableAttribute;
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
	
	public ConstraintGrinder(Criteria criteria, IndexedCollection<CacheEntry> indexedCollection) {
		this.criteria = criteria;
		this.indexedCollection = indexedCollection;
		this.orbTypeInternalId = criteria.getOrbTypeInternalId();
		this.constraintKitchen = (ConstraintKitchen) Fletch22ApplicationContext.getApplicationContext().getBean(ConstraintKitchen.class);
		this.orbTypeManager = (OrbTypeManager) Fletch22ApplicationContext.getApplicationContext().getBean(OrbTypeManager.class);
		this.orbCollection = (OrbCollection) Fletch22ApplicationContext.getApplicationContext().getBean(Cache.class).orbCollection;
		
		if (criteria.logicalConstraint == null)  {
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
				grinderSortInfo.sortIndex = orbTypeManager.getIndexOfAttribute(criteria.getOrbType(), criteriaSortInfo.sortAttributeName);
				grinderSortInfoList.add(grinderSortInfo);
			}
			
			OrbType orbType = orbTypeManager.getOrbType(this.orbTypeInternalId);
			OrbComparator rowComparator = new OrbComparator(grinderSortInfoList, orbType);
			Collections.sort(orbList, rowComparator);
		}
		
		return new OrbResultSet(orbList);
	}
	
	private Query<CacheEntry> processConstraint(LogicalConstraint logicalConstraint) {
		List<Constraint> constraintList = logicalConstraint.constraintList;
		
		List<Query<CacheEntry>> queries = new ArrayList<Query<CacheEntry>>();
		for (Constraint constraintInner: constraintList) {
			
			Query<CacheEntry> queryLocal = null;
			if (constraintInner instanceof ConstraintDetailsSingleValue) {
				queryLocal = processConstraintDetailsSingleValue((ConstraintDetailsSingleValue) constraintInner);
			} else if (constraintInner instanceof ConstraintDetailsList) {
				queryLocal = processConstraintDetailsList((ConstraintDetailsList) constraintInner);
			} else if (constraintInner instanceof ConstraintDetailsAggregate) {
				queryLocal = processConstraintAggregate((ConstraintDetailsAggregate) constraintInner);
			} else if (constraintInner instanceof LogicalConstraint) {
				queryLocal = processConstraint((LogicalConstraint) constraintInner);
			} else {
				throw new RuntimeException("Did not recognize constraint");
			}
			queries.add(queryLocal);
		}
		
		Query<CacheEntry> query = null;
		
		if (queries.size() == 1) {
			query = queries.get(0);
		} else {
			if (logicalConstraint.logicalOperator.equals(LogicalOperator.AND)) {
				query = and(queries);
			} else if (logicalConstraint.logicalOperator.equals(LogicalOperator.OR)) {
				query = or(queries);
			}
		}
		
		return query;
	}
	
	private Query<CacheEntry> processConstraintAggregate(ConstraintDetailsAggregate constraintDetailsAggregate) {
		Query<CacheEntry> queryLocal = null;	
		
		SimpleNullableAttribute<CacheEntry, String> simpleNullableAttribute = createSimpleNullableAtttribute(constraintDetailsAggregate);

		if (constraintDetailsAggregate.getRelationshipOperator() == RelationshipOperator.IS) {
			
			OrbResultSet orbResultSet = orbCollection.executeQuery(constraintDetailsAggregate.criteriaForAggregation);
			Set<String> aggregateColumnValues = getAttributeValuesByFrequency(constraintDetailsAggregate, orbResultSet, 1);
			
			if (aggregateColumnValues.size() == 1) {
				Iterator<String> iter = aggregateColumnValues.iterator();
				queryLocal = equal(simpleNullableAttribute, iter.next());	
			} else {
				queryLocal = in(simpleNullableAttribute, aggregateColumnValues);
			}
		} else {
			throw new NotImplementedException("Encountered problem processing aggregate constrinat. Relationship '" + constraintDetailsAggregate.getRelationshipOperator() + "' not recognized.");
		}
		
		return queryLocal;
	}

	private Set<String> getAttributeValuesByFrequency(ConstraintDetailsAggregate constraintDetailsAggregate, OrbResultSet orbResultSet, int frequencyRequired) {
		Map<String, Integer> aggregateColumnValues = new HashMap<String, Integer>();
		for (Orb orb : orbResultSet.orbList) {
			String value = orb.getUserDefinedProperties().get(constraintDetailsAggregate.aggregationAttributeName);
			
			Integer frequency = aggregateColumnValues.get(value);
			if (frequency == null) {
				aggregateColumnValues.put(value, 1);	
			} else {
				aggregateColumnValues.put(value, frequency + 1);
			}
		}

		Set<String> unique = new HashSet<String>();
		Set<String> keys = aggregateColumnValues.keySet();
		for (String key: keys) {
			Integer frequency = aggregateColumnValues.get(key);
			if (frequency == frequencyRequired) {
				unique.add(key);
			}
		}
		
		return unique;
	}

	private Query<CacheEntry> processConstraintDetailsList(ConstraintDetailsList constraintDetailsList) {
		Query<CacheEntry> queryLocal = null;	
		
		SimpleNullableAttribute<CacheEntry, String> simpleNullableAttribute = createSimpleNullableAtttribute(constraintDetailsList);

		if (constraintDetailsList.getRelationshipOperator() == RelationshipOperator.IN) {
			queryLocal = in(simpleNullableAttribute, constraintDetailsList.operativeValueList.toArray(new String[constraintDetailsList.operativeValueList.size()]));
		} else {
			throw new NotImplementedException("Encountered problem processing aggregate constrinat. Relationship '" + constraintDetailsList.getRelationshipOperator() + "' not recognized.");
		}
		
		return queryLocal;
	}

	private OrbTypeManager getOrbTypeManager() {
		return Fletch22ApplicationContext.getApplicationContext().getBean(OrbTypeManager.class);
	}

	private Query<CacheEntry> processConstraintDetailsSingleValue(ConstraintDetailsSingleValue constraintDetailsSingleValue) {

		Query<CacheEntry> queryLocal = null;	
		
		SimpleNullableAttribute<CacheEntry, String> simpleNullableAttribute = createSimpleNullableAtttribute(constraintDetailsSingleValue);

		if (constraintDetailsSingleValue.getRelationshipOperator() == RelationshipOperator.EQUALS) {
			queryLocal = equal(simpleNullableAttribute, constraintDetailsSingleValue.operativeValue);
		} else {
			throw new NotImplementedException("Encountered problem processing aggregate constrinat. Relationship '" + constraintDetailsSingleValue.getRelationshipOperator() + "' not recognized.");
		}
		
		return queryLocal;
	}

	private SimpleNullableAttribute<CacheEntry, String> createSimpleNullableAtttribute(ConstraintDetails constraintDetails) {
		Class<? extends SimpleNullableAttribute<CacheEntry, String>> clazz = getClazzFactory(constraintDetails.getAttributeName());

		SimpleNullableAttribute<CacheEntry, String> simpleNullableAttribute = null;
		try {
			simpleNullableAttribute = (SimpleNullableAttribute<CacheEntry, String>) clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return simpleNullableAttribute;
	}
	
	
	@SuppressWarnings("unchecked")
	private Class<? extends SimpleNullableAttribute<CacheEntry, String>> getClazzFactory(String attributeName) {
		
		Class<? extends SimpleNullableAttribute<CacheEntry, String>> clazz = null;
		
		try {
			int indexOfAttribute = getOrbTypeManager().getIndexOfAttribute(orbTypeInternalId, attributeName);
			
			String compositeKey = SimpleNullableAttribute.class.getName() + "_" + String.valueOf(orbTypeInternalId) + "_" + String.valueOf(indexOfAttribute) + "_" + attributeName;
			
			if (hasBespokeAttributeClassBeenRegistered(compositeKey)) {
				clazz = (Class<? extends SimpleNullableAttribute<CacheEntry, String>>) getBespokeAttributeClass(compositeKey);
			} else {
				clazz = GeneratedCacheEntryClassFactory.getInstance(indexOfAttribute, compositeKey);
				ensureNameRegistered(compositeKey, clazz);
			}
			
		} catch (Exception e) {
			StackTraceElement[] trace = e.getStackTrace();
			for (StackTraceElement stackTraceElement: trace) {
				logger.debug("CN: {}", stackTraceElement.getClassName());
			}
			throw new RuntimeException(e);
		}
		return clazz;
	}
	
	private boolean hasBespokeAttributeClassBeenRegistered(String key) {
		Map<String, Class<?>> map = this.constraintKitchen.previouslyBespokeAttributeClassMap;
		return map.containsKey(key);
	}
	
	private Class<?> getBespokeAttributeClass(String key) {
		Map<String, Class<?>> map = this.constraintKitchen.previouslyBespokeAttributeClassMap;
		return map.get(key);
	}
	
	private void ensureNameRegistered(String key, Class<?> clazz) {
		Map<String, Class<?>> map = this.constraintKitchen.previouslyBespokeAttributeClassMap;
		map.put(key, clazz);
	}
}
