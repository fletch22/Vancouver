package com.fletch22.orb.cache.query;

import java.util.HashMap;
import java.util.Map;

import com.fletch22.orb.query.CriteriaFactory.Criteria;

public class QueryCollection {

	public Map<Long, Criteria> queries = new HashMap<Long, Criteria>();

	public boolean doesQueryExist(long queryInternalId) {
		return queries.containsKey(queryInternalId);
	}
}
