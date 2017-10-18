package com.fletch22.orb.query.criteria;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.util.json.GsonFactory;
import com.google.gson.Gson;

@Component
public class CriteriaCloner {
	
	Logger logger = LoggerFactory.getLogger(CriteriaCloner.class);

	@Autowired
	GsonFactory gsonFactory;
	
	public Criteria clone(Criteria criteria) {
		Gson gson = gsonFactory.getInstance();
		
		String json = gson.toJson(criteria, Criteria.class);
		logger.debug(json);
		
		return gson.fromJson(json, Criteria.class);
	}
}
