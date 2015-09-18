 package com.fletch22.orb;

import org.springframework.stereotype.Component;

@Component
public class InternalIdGenerator {
	
	public static final long START_ID = 1000;
	private long currentId = START_ID;

	public long getCurrentId() {
		return currentId;
	}
	
	public void incrementId() {
		++this.currentId;
	}
	
	public long getNewId()  {
		incrementId();
		return getCurrentId();
	}

	public void setCurrentId(long id) {
		this.currentId = id;
	}
}
