package com.fletch22.orb;

import org.springframework.stereotype.Component;

@Component
public class InternalIdGenerator {
	
	private long currentId = 0;

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
