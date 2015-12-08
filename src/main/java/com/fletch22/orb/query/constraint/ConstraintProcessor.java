package com.fletch22.orb.query.constraint;

import static com.googlecode.cqengine.query.QueryFactory.and;
import static com.googlecode.cqengine.query.QueryFactory.equal;
import static com.googlecode.cqengine.query.QueryFactory.in;
import static com.googlecode.cqengine.query.QueryFactory.or;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.cache.local.Cache;
import com.fletch22.orb.cache.local.CacheEntry;
import com.fletch22.orb.query.LogicalConstraint;
import com.fletch22.orb.query.LogicalOperator;
import com.fletch22.orb.query.OrbResultSet;
import com.fletch22.orb.query.RelationshipOperator;
import com.fletch22.orb.search.GeneratedCacheEntryClassFactory;
import com.googlecode.cqengine.attribute.SimpleNullableAttribute;
import com.googlecode.cqengine.query.Query;

@Component
public class ConstraintProcessor implements ConstraintProcessVisitor {
	
	static Logger logger = LoggerFactory.getLogger(Constraint.class);
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	ConstraintKitchen constraintKitchen;
	
	@Autowired
	Cache cache;

	@Override
	public Query<CacheEntry> visit(ConstraintDetailsList constraintDetailsList, long orbTypeInternalId) {
		Query<CacheEntry> queryLocal = null;	
		
		SimpleNullableAttribute<CacheEntry, String> simpleNullableAttribute = createSimpleNullableAtttribute(orbTypeInternalId, constraintDetailsList);

		if (constraintDetailsList.getRelationshipOperator() == RelationshipOperator.IN) {
			queryLocal = in(simpleNullableAttribute, constraintDetailsList.operativeValueList.toArray(new String[constraintDetailsList.operativeValueList.size()]));
		} else {
			throw new NotImplementedException("Encountered problem processing aggregate constrinat. Relationship '" + constraintDetailsList.getRelationshipOperator() + "' not recognized.");
		}
		
		return queryLocal;
	}

	@Override
	public Query<CacheEntry> visit(ConstraintDetailsSingleValue constraintDetailsSingleValue, long orbTypeInternalId) {
		Query<CacheEntry> queryLocal = null;	
		
		SimpleNullableAttribute<CacheEntry, String> simpleNullableAttribute = createSimpleNullableAtttribute(orbTypeInternalId, constraintDetailsSingleValue);

		if (constraintDetailsSingleValue.getRelationshipOperator() == RelationshipOperator.EQUALS) {
			queryLocal = equal(simpleNullableAttribute, constraintDetailsSingleValue.operativeValue);
		} else {
			throw new NotImplementedException("Encountered problem processing aggregate constrinat. Relationship '" + constraintDetailsSingleValue.getRelationshipOperator() + "' not recognized.");
		}
		
		return queryLocal;
	}

	@Override
	public Query<CacheEntry> visit(ConstraintDetailsAggregate constraintDetailsAggregate, long orbTypeInternalId) {
		Query<CacheEntry> queryLocal = null;	
		
		SimpleNullableAttribute<CacheEntry, String> simpleNullableAttribute = createSimpleNullableAtttribute(orbTypeInternalId, constraintDetailsAggregate);

		if (constraintDetailsAggregate.getRelationshipOperator() == RelationshipOperator.IS) {
			
			OrbResultSet orbResultSet = cache.orbCollection.executeQuery(constraintDetailsAggregate.criteriaForAggregation);
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

	@Override
	public Query<CacheEntry> visit(LogicalConstraint logicalConstraint, long orbTypeInternalId) {
		List<Constraint> constraintList = logicalConstraint.constraintList;
		
		List<Query<CacheEntry>> queries = new ArrayList<Query<CacheEntry>>();
		for (Constraint constraintInner: constraintList) {
			
			Query<CacheEntry> queryLocal = constraintInner.acceptConstraintProcessorVisitor(this, orbTypeInternalId);
			
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
	
	private SimpleNullableAttribute<CacheEntry, String> createSimpleNullableAtttribute(long orbTypeInternalId, ConstraintDetails constraintDetails) {
		Class<? extends SimpleNullableAttribute<CacheEntry, String>> clazz = getClazzFactory(constraintDetails.getAttributeName(), orbTypeInternalId);

		SimpleNullableAttribute<CacheEntry, String> simpleNullableAttribute = null;
		try {
			simpleNullableAttribute = (SimpleNullableAttribute<CacheEntry, String>) clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return simpleNullableAttribute;
	}

	@SuppressWarnings("unchecked")
	private Class<? extends SimpleNullableAttribute<CacheEntry, String>> getClazzFactory(String attributeName, long orbTypeInternalId) {
		
		Class<? extends SimpleNullableAttribute<CacheEntry, String>> clazz = null;
		
		try {
			int indexOfAttribute = this.orbTypeManager.getIndexOfAttribute(orbTypeInternalId, attributeName);
			
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
	
	private Set<String> getAttributeValuesByFrequency(ConstraintDetailsAggregate constraintDetailsAggregate, OrbResultSet orbResultSet, int frequencyRequired) {
		Map<String, Integer> aggregateColumnValues = new HashMap<String, Integer>();
		for (Orb orb : orbResultSet.orbList) {
			String value = orb.getUserDefinedProperties().get(constraintDetailsAggregate.criteriaForAggregation.fieldOfInterest);
			
			Integer frequency = aggregateColumnValues.get(value);
			frequency = (frequency == null) ? 1: frequency + 1;
			
			aggregateColumnValues.put(value, frequency);
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
}
