package com.fletch22.orb;

import org.springframework.stereotype.Component;

@Component
public class InternalIdGenerator {
	
	private long currentId = 0;

	public long getCurrentId() {
		return currentId;
	}
	
	public long getNextId() {
		return ++this.currentId;
	}
}
