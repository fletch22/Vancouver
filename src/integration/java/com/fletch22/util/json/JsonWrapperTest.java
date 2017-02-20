package com.fletch22.util.json;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fletch22.aop.QueryThing;
import com.fletch22.orb.IntegrationSystemInitializer;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.command.MethodCallCommand;
import com.fletch22.orb.command.orbType.dto.MethodCallDto;
import com.fletch22.orb.query.LogicalConstraint;
import com.fletch22.orb.query.LogicalOperator;
import com.fletch22.orb.query.QueryMother;
import com.fletch22.orb.query.constraint.Constraint;
import com.fletch22.orb.query.constraint.ConstraintDetailsAggregate;
import com.fletch22.orb.query.constraint.ConstraintDetailsSingleValue;
import com.fletch22.orb.query.criteria.Criteria;
import com.fletch22.orb.query.criteria.CriteriaStandard;
import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
public class JsonWrapperTest {

	Logger logger = LoggerFactory.getLogger(JsonWrapperTest.class);

	@Autowired
	GsonFactory gsonFactory;

	@Autowired
	QueryMother queryMother;
	
	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer;
	
	@Before
	public void before() {
		integrationSystemInitializer.nukePaveAndInitializeAllIntegratedSystems();
	}
	
	@After
	public void after() {
		integrationSystemInitializer.nukePaveAndInitializeAllIntegratedSystems();
	}

	@SuppressWarnings("unused")
	@Test
	public void test() {

		// Arrange
		QueryThing queryTest = new QueryThing();
		StopWatch stopWatch = new StopWatch();

		int maxTimes = 100;

		stopWatch.start();
		for (int i = 0; i < maxTimes; i++) {
			JsonWrapper jsonWrapper = new JsonWrapper(queryTest, gsonFactory);
			String thing = jsonWrapper.toJson();
		}
		stopWatch.stop();

		long millis = stopWatch.getNanoTime() / 100000000;
	}

	@Test
	public void testUnwrappingString() {

		QueryThing queryTest = new QueryThing();

		JsonWrapper jsonWrapper = new JsonWrapper(queryTest, gsonFactory);
		testJsonWrapperPerf(jsonWrapper);

		String stringThing = "funny";
		jsonWrapper = new JsonWrapper(stringThing, gsonFactory);

		logger.debug(jsonWrapper.toJson());

		testJsonWrapperPerf(jsonWrapper);
	}

	@Test
	public void testUnwrappingBigDecimal() {

		QueryThing queryTest = new QueryThing();

		JsonWrapper jsonWrapper = new JsonWrapper(queryTest, gsonFactory);
		testJsonWrapperPerf(jsonWrapper);

		BigDecimal bigDecimal = new BigDecimal("1.200000123123213");
		jsonWrapper = new JsonWrapper(bigDecimal, gsonFactory);

		logger.debug(jsonWrapper.toJson());

		testJsonWrapperPerf(jsonWrapper);
	}

	@Test
	public void testUnwrappingPrimitiveBoolean() {

		QueryThing queryTest = new QueryThing();

		JsonWrapper jsonWrapper = new JsonWrapper(queryTest, gsonFactory);
		testJsonWrapperPerf(jsonWrapper);

		boolean isDelicious = true;
		jsonWrapper = new JsonWrapper(isDelicious, gsonFactory);

		logger.debug(jsonWrapper.toJson());

		testJsonWrapperPerf(jsonWrapper);
	}

	@Test
	public void testUnwrappingObjectBoolean() {

		QueryThing queryTest = new QueryThing();

		JsonWrapper jsonWrapper = new JsonWrapper(queryTest, gsonFactory);
		testJsonWrapperPerf(jsonWrapper);

		Boolean isDelicious = new Boolean(true);
		jsonWrapper = new JsonWrapper(isDelicious, gsonFactory);

		logger.debug(jsonWrapper.toJson());

		testJsonWrapperPerf(jsonWrapper);
	}

	@Test
	public void testUnwrappingNullObjectBoolean() {

		QueryThing queryTest = new QueryThing();

		JsonWrapper jsonWrapper = new JsonWrapper(queryTest, gsonFactory);
		testJsonWrapperPerf(jsonWrapper);

		Boolean isDelicious = null;
		jsonWrapper = new JsonWrapper(isDelicious, gsonFactory);

		testJsonWrapperPerf(jsonWrapper);
	}

	@Test
	public void testJsonWrapperLinkedHashSet() {

		LinkedHashSet<String> set = new LinkedHashSet<String>();
		set.add("foo");
		set.add("bar");

		LinkedHashSetString linkedHashSetString = new LinkedHashSetString(set);

		JsonWrapper jsonWrapper = new JsonWrapper(linkedHashSetString, gsonFactory);

		JsonWrapper jsonWrapper2 = JsonWrapper.fromJson(gsonFactory.getInstance(), jsonWrapper.toJson());

		LinkedHashSetString reconstituted = (LinkedHashSetString) jsonWrapper2.object;

		logger.debug("jsonWrapper: {}", jsonWrapper.toJson());

		assertEquals("Should be 2 elements.", reconstituted.linkedHashSet.size(), 2);
	}

	@Test
	public void testLinkedHashmap() {

		LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<String, String>();
		linkedHashMap.put("fooKey", "fooValue");
		linkedHashMap.put("barKey", "barKey");

		LinkedHashMapStringString linkedHashMapStringString = new LinkedHashMapStringString(linkedHashMap);

		JsonWrapper jsonWrapper = new JsonWrapper(linkedHashMapStringString, gsonFactory);

		JsonWrapper jsonWrapper2 = JsonWrapper.fromJson(gsonFactory.getInstance(), jsonWrapper.toJson());

		LinkedHashMapStringString reconstituted = (LinkedHashMapStringString) jsonWrapper2.object;

		logger.debug("jsonWrapper: {}", jsonWrapper.toJson());

		assertEquals("Should be 2 elements.", reconstituted.linkedHashMap.size(), 2);
	}

	@Test
	public void testMap() {

		Map<Long, String> map = new LinkedHashMap<Long, String>();
		map.put(1L, "fooValue");
		map.put(2L, "barKey");

		MapLongString linkedHashMapLong = new MapLongString(map);

		JsonWrapper jsonWrapper = new JsonWrapper(linkedHashMapLong, gsonFactory);

		JsonWrapper jsonWrapper2 = JsonWrapper.fromJson(gsonFactory.getInstance(), jsonWrapper.toJson());

		MapLongString reconstituted = (MapLongString) jsonWrapper2.object;

		logger.debug("jsonWrapper: {}", jsonWrapper.toJson());

		assertEquals("Should be 2 elements.", reconstituted.map.size(), 2);
	}

	private void testJsonWrapperPerf(JsonWrapper jsonWrapper) {

		String json = jsonWrapper.toJson();
		logger.debug(json);

		Gson gson = gsonFactory.getInstance();

		StopWatch stopWatch = new StopWatch();

		int maxTimes = 100;
		stopWatch.start();
		for (int i = 0; i < maxTimes; i++) {
			jsonWrapper = JsonWrapper.fromJson(gson, json);
			logger.debug("object: {}", jsonWrapper.object);
		}
		stopWatch.stop();

		long millis = stopWatch.getNanoTime() / 100000000;

		logger.debug("Elapsed millis per unit: " + millis);
	}

	@Test
	public void testCriteria() {

		LinkedHashSet<String> set = new LinkedHashSet<String>();
		set.add("bar");
		OrbType orbType = new OrbType(123, "foo", new BigDecimal("3456"), set);
		long orbTypeInternalIdOriginal = orbType.id;

		Criteria criteria = new CriteriaStandard(orbType.id, "foo");

		JsonWrapper jsonWrapper = new JsonWrapper(criteria, gsonFactory);

		JsonWrapper jsonWrapper2 = JsonWrapper.fromJson(gsonFactory.getInstance(), jsonWrapper.toJson());

		Criteria reconstituted = (Criteria) jsonWrapper2.object;

		logger.debug("jsonWrapper: {}", jsonWrapper.toJson());

		assertEquals("Should be 2 elements.", reconstituted.getOrbTypeInternalId(), orbTypeInternalIdOriginal);
	}

	@Test
	public void testConstraintDetailSingleValue() {

		LinkedHashSet<String> set = new LinkedHashSet<String>();
		set.add("bar");
		OrbType orbType = new OrbType(123, "foo", new BigDecimal("3456"), set);
		long orbTypeInternalIdOriginal = orbType.id;

		Criteria criteria = new CriteriaStandard(orbType.id, "foo");

		Constraint constraint = Constraint.eq("bar", "somevalue");
		criteria.addAnd(constraint);

		JsonWrapper jsonWrapper = new JsonWrapper(criteria, gsonFactory);

		Gson gson = gsonFactory.getInstance();

		JsonWrapper jsonWrapper2 = JsonWrapper.fromJson(gson, jsonWrapper.toJson());
		Criteria reconstituted = (Criteria) jsonWrapper2.object;

		logger.debug("jsonWrapper: {}", jsonWrapper.toJson());

		LogicalConstraint logicalConstraint = reconstituted.logicalConstraint;

		assertEquals(logicalConstraint.constraintList.size(), 1);

		assertEquals(logicalConstraint.logicalOperator, LogicalOperator.AND);

		Constraint constraintRecon = logicalConstraint.constraintList.get(0);

		assertTrue(constraintRecon instanceof ConstraintDetailsSingleValue);

		ConstraintDetailsSingleValue constraintDetailsSingleValue = (ConstraintDetailsSingleValue) constraintRecon;

		assertEquals(constraintDetailsSingleValue.getAttributeName(), "bar");
		assertEquals(constraintDetailsSingleValue.getOperativeValue(), "somevalue");

		assertEquals("Should be 2 elements.", reconstituted.getOrbTypeInternalId(), orbTypeInternalIdOriginal);
	}

	@Test
	public void testConstraintDetailsAggregate() {
		
		

		Criteria criteria = queryMother.getSimpleAggregateQuery();

		JsonWrapper jsonWrapper = new JsonWrapper(criteria, gsonFactory);

		Gson gson = gsonFactory.getInstance();

		JsonWrapper jsonWrapper2 = JsonWrapper.fromJson(gson, jsonWrapper.toJson());

		Criteria reconstituted = (Criteria) jsonWrapper2.object;

		logger.info("jsonWrapper: {}", jsonWrapper.toJson());
	}
	
	@Test
	public void testCriteriaDetailsAgg() {
		String json = "{\"type\":\"com.fletch22.orb.query.constraint.ConstraintDetailsAggregate\",\"properties\":{\"relationshipOperator\":\"ARE\",\"aggregate\":\"UNIQUE\",\"criteriaForAggregation\":{\"fieldOfInterest\":[\"bar\"],\"criteriaId\":1010,\"orbType\":{\"id\":1008,\"label\":\"foo\",\"tranDate\":1450399111696.0000000001,\"customFields\":[\"bar\"]},\"label\":\"agg\",\"hasIdBeenSet\":true,\"sortInfoList\":[],\"criteriaIdParent\":1009},\"attributeName\":\"bar\"}}";
		
		Gson gson = gsonFactory.getInstance();
		
		ConstraintDetailsAggregate constraintDetailsAggregate = gson.fromJson(json, ConstraintDetailsAggregate.class);

		assertNotNull(constraintDetailsAggregate);
	}

	@Test
	public void testCriteriaDeserialization() {
		String json = "{\"criteriaId\":1009,\"orbType\":{\"id\":1008,\"label\":\"foo\",\"tranDate\":1450399111696.0000000001,\"customFields\":[\"bar\"]},\"label\":\"fuzzyThings\",\"hasIdBeenSet\":true,\"sortInfoList\":[],\"criteriaIdParent\":-1,\"logicalConstraint\":{\"logicalOperator\":\"AND\",\"constraintList\":[{\"type\":\"com.fletch22.orb.query.constraint.ConstraintDetailsAggregate\",\"properties\":{\"relationshipOperator\":\"ARE\",\"aggregate\":\"UNIQUE\",\"criteriaForAggregation\":{\"fieldOfInterest\":[\"bar\"],\"criteriaId\":1010,\"orbType\":{\"id\":1008,\"label\":\"foo\",\"tranDate\":1450399111696.0000000001,\"customFields\":[\"bar\"]},\"label\":\"agg\",\"hasIdBeenSet\":true,\"sortInfoList\":[],\"criteriaIdParent\":1009},\"attributeName\":\"bar\"}}]}}";
		Gson gson = gsonFactory.getInstance();
		
		logger.info(json);
		
		CriteriaStandard criteriaStandard = gson.fromJson(json, com.fletch22.orb.query.criteria.CriteriaStandard.class);
		
		assertNotNull(criteriaStandard);
	}

	@Test
	public void testMethodCallAttach() {
		String json = "{\"command\":{\"methodCall\":{\"className\":\"com.fletch22.orb.query.QueryManager\"},\"methodName\":\"attach\",\"methodParameters\":[{\"parameterTypeName\":\"class com.fletch22.orb.query.criteria.Criteria\", \"argument\":{\"clazzName\":\"com.fletch22.orb.query.criteria.CriteriaStandard\",\"objectValueAsJson\":\"{\\\"criteriaId\\\":1002,\\\"orbType\\\":{\\\"id\\\":1001,\\\"label\\\":\\\"foo\\\",\\\"tranDate\\\":1450397912218.0000000000,\\\"customFields\\\":[\\\"bar\\\"]},\\\"label\\\":\\\"fuzzyThings\\\",\\\"hasIdBeenSet\\\":true,\\\"sortInfoList\\\":[],\\\"criteriaIdParent\\\":-1,\\\"logicalConstraint\\\":{\\\"logicalOperator\\\":\\\"AND\\\",\\\"constraintList\\\":[{\\\"type\\\":\\\"com.fletch22.orb.query.constraint.ConstraintDetailsAggregate\\\",\\\"properties\\\":{\\\"relationshipOperator\\\":\\\"ARE\\\",\\\"aggregate\\\":\\\"UNIQUE\\\",\\\"criteriaForAggregation\\\":{\\\"fieldOfInterest\\\":[\\\"bar\\\"],\\\"criteriaId\\\":1003,\\\"orbType\\\":{\\\"id\\\":1001,\\\"label\\\":\\\"foo\\\",\\\"tranDate\\\":1450397912218.0000000000,\\\"customFields\\\":[\\\"bar\\\"]},\\\"label\\\":\\\"agg\\\",\\\"hasIdBeenSet\\\":true,\\\"sortInfoList\\\":[],\\\"criteriaIdParent\\\":1002},\\\"attributeName\\\":\\\"bar\\\"}}]}}\"}}]}}";

		MethodCallCommand methodCallCommand = new MethodCallCommand();
		methodCallCommand.setGsonFactory(gsonFactory);
		
		MethodCallDto methodCallDto = methodCallCommand.fromJson(new StringBuilder(json));

		assertNotNull(methodCallDto);
	}
}
