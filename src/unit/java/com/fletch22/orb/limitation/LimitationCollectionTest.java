package com.fletch22.orb.limitation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.orb.OrbType;
import com.fletch22.orb.query.CriteriaFactory;
import com.fletch22.orb.query.CriteriaFactory.Criteria;
import com.fletch22.util.RandomUtil;
import com.fletch22.util.StopWatch;

public class LimitationCollectionTest {

	static Logger logger = LoggerFactory.getLogger(LimitationCollectionTest.class);

	@Test
	public void testJava8StreamAndFilter() {
		LimitationCollection limitationCollection = new LimitationCollection();

		List<Criteria> originalList = limitationCollection.criteriaList;

		populateCriteriaList(originalList);

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		List<Criteria> list = originalList.stream().filter(criteria -> criteria.getOrbTypeInternalId() == 123).collect(Collectors.toList());
		stopWatch.stop();

		logger.info("Elapsed millis: " + stopWatch.getElapsedMillis());
	}
	
	@Test
	public void testNewingCriteriaList() {
		
		StopWatch stopWatch = new StopWatch();
		
		Map<Long, List<Criteria>> list = new HashMap<Long, List<Criteria>>();
		
		stopWatch.start();
		List<Criteria> isNullList = list.get(123l);
		isNullList = (isNullList == null) ? new ArrayList<Criteria>() : isNullList;
		stopWatch.stop();
		logger.info("Elapsed: {}", stopWatch.getElapsedMillis());
	}

	private void populateCriteriaList(List<Criteria> originalList) {

		CriteriaFactory criteriaFactory = new CriteriaFactory();

		RandomUtil randomUtil = new RandomUtil();

		int count = 1000;
		for (int i = 0; i < count; i++) {

			OrbType orbType = new OrbType(i, randomUtil.getRandomString(10), new BigDecimal("12345678"), null);
			Criteria criteria = criteriaFactory.createInstance(orbType, "foo");
			originalList.add(criteria);
		}
	}
}
