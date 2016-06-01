package com.fletch22.util.json;
import org.springframework.stereotype.Component;

import com.fletch22.orb.query.constraint.Constraint;
import com.fletch22.orb.query.criteria.Criteria;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


@Component
public class GsonFactory {
	
	Gson instance = null;

	public Gson getInstance() {
		
		if (instance == null)  {
			GsonBuilder gsonBilder = new GsonBuilder();
			gsonBilder.registerTypeAdapter(Constraint.class, new ConstraintAdapter());
			gsonBilder.registerTypeAdapter(Criteria.class, new CriteriaAdapter());
			this.instance = gsonBilder.create();
		}
		
		return instance;
	}
}
