package com.fletch22.orb.query;

import static com.googlecode.cqengine.query.QueryFactory.equal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.Fletch22ApplicationContext;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.cache.local.CacheEntry;
import com.fletch22.orb.search.GeneratedCacheEntryClassFactory;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.SimpleNullableAttribute;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.resultset.ResultSet;
import static com.googlecode.cqengine.query.QueryFactory.*;

public class ConstraintGrinder {
	
	Logger logger = LoggerFactory.getLogger(ConstraintGrinder.class);
	
	List<LogicalConstraint> logicalConstraintList;
	IndexedCollection<CacheEntry> indexedCollection;
	long orbTypeInternalId;
	
	Query<CacheEntry> query = null;
	ConstraintKitchen constraintKitchen;

	public ConstraintGrinder(long orbTypeInternalId, List<LogicalConstraint> logicalConstraintList, IndexedCollection<CacheEntry> indexedCollection) {
		this.logicalConstraintList = logicalConstraintList;
		this.indexedCollection = indexedCollection;
		this.orbTypeInternalId = orbTypeInternalId;
		this.constraintKitchen = (ConstraintKitchen) Fletch22ApplicationContext.getApplicationContext().getBean(ConstraintKitchen.class);
		
		for (LogicalConstraint logicalConstraint : logicalConstraintList) {
			query = processConstraint(logicalConstraint);
		}
	}
	
	public List<CacheEntry> list() {
		
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		ResultSet<CacheEntry> resultSet = this.indexedCollection.retrieve(query);
		stopWatch.stop();
		
		BigDecimal elapsed = new BigDecimal(stopWatch.getNanoTime()).divide(new BigDecimal(1000000));
		logger.info("Elapsed : {}", elapsed);

		logger.info("Query found {} elements.", resultSet.size());
		
		List<CacheEntry> cacheEntryList = new ArrayList<CacheEntry>();
		for (CacheEntry cacheEntry : resultSet) {
			cacheEntryList.add(cacheEntry);
		}
		
		return cacheEntryList;
	}

	private Query<CacheEntry> processConstraint(LogicalConstraint logicalConstraint) {
		Constraint[] constraintArray = logicalConstraint.constraint.getConstraints();
		
		List<Query<CacheEntry>> queries = new ArrayList<Query<CacheEntry>>();
		for (Constraint constraintInner: constraintArray) {
			
			Query<CacheEntry> queryLocal = null;
			if (constraintInner instanceof ConstraintDetails) {
				queryLocal = processConstraintDetails((ConstraintDetails) constraintInner);
			} else if (constraintInner instanceof ConstraintCollection) {
				LogicalConstraint logicalConstraintLocal = new LogicalConstraint(logicalConstraint.logicalOperator, constraintInner);
				queryLocal = processConstraint(logicalConstraintLocal);
			} else if (constraintInner instanceof LogicalConstraint) {
				queryLocal = processConstraint((LogicalConstraint) constraintInner);
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
	
	private OrbTypeManager getOrbTypeManager() {
		return Fletch22ApplicationContext.getApplicationContext().getBean(OrbTypeManager.class);
	}

	private Query<CacheEntry> processConstraintDetails(ConstraintDetails constraintDetails) {

		Query<CacheEntry> queryLocal = null;	
		
		Class<? extends SimpleNullableAttribute<CacheEntry, String>> clazz = getClazzFactory(constraintDetails);

		SimpleNullableAttribute<CacheEntry, String> simpleNullableAttribute = null;
		try {
			simpleNullableAttribute = (SimpleNullableAttribute<CacheEntry, String>) clazz.newInstance();
			logger.info("Type: {}", simpleNullableAttribute.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (constraintDetails.relationshipOperator == RelationshipOperator.EQUALS) {
			queryLocal = equal(simpleNullableAttribute, constraintDetails.operativeValue);
		}
		
		return queryLocal;
	}
	
	private Class<? extends SimpleNullableAttribute<CacheEntry, String>> getClazzFactory(ConstraintDetails constraintDetails) {
		
		Class<? extends SimpleNullableAttribute<CacheEntry, String>> clazz = null;
		
		try {
			int indexOfAttribute = getOrbTypeManager().getIndexOfAttribute(orbTypeInternalId, constraintDetails.attributeName);
			
			String compositeKey = SimpleNullableAttribute.class.getName() + "_" + String.valueOf(orbTypeInternalId) + "_" + String.valueOf(indexOfAttribute) + "_" + constraintDetails.attributeName;
			
			if (hasBespokeAttributeClassBeenRegistered(compositeKey)) {
				clazz = (Class<? extends SimpleNullableAttribute<CacheEntry, String>>) getBespokeAttributeClass(compositeKey);
			} else {
				clazz = GeneratedCacheEntryClassFactory.getInstance(indexOfAttribute, compositeKey);
				ensureNameRegistered(compositeKey, clazz);
			}
			
		} catch (Exception e) {
			StackTraceElement[] trace = e.getStackTrace();
			for (StackTraceElement stackTraceElement: trace) {
				logger.info("CN: {}", stackTraceElement.getClassName());
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
