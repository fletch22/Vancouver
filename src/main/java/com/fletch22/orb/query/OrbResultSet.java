package com.fletch22.orb.query;

import java.util.ArrayList;
import java.util.List;

import com.fletch22.orb.Orb;

public class OrbResultSet {

	public List<Orb> orbList = new ArrayList<Orb>();
	
	public OrbResultSet() {}
	
	public OrbResultSet(List<Orb> orbList) {
		this.orbList = orbList;
	}
	
	public Orb uniqueResult() {
		if (orbList.size() > 1) {
			throw new RuntimeException("Encountered a problem processing result set; there is more than one result. Only one result was expected.");
		}
		return orbList.size() > 0 ? orbList.get(0): null;
	}
	
	public Orb uniqueResultNotNull() {
		if (orbList.size() > 1) {
			throw new RuntimeException("Encountered a problem processing result set; there is more than one result. Only one result was expected.");
		} else if (orbList.size() == 0) {
			throw new RuntimeException("Encountered a problem processing result set; no result found. Exactly one result was expected.");
		}
		return orbList.get(0);
	}
	
	public List<Orb> getOrbList() {
		return orbList;
	}
}
