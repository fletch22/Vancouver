package com.fletch22.orb.query;

import java.util.List;

import org.apache.commons.lang3.NotImplementedException;

import com.fletch22.orb.cache.local.CacheEntry;


public class Criteria {

	public List<ConstraintCollection> constraintsList;
	
	public Criteria add(ConstraintCollection constraint) {

		constraintsList.add(constraint);
		
		return this;
	}
	
	public List<CacheEntry> list() {
		throw new NotImplementedException("asdf");
	}
}
