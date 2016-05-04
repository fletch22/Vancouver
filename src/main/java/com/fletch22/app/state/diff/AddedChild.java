package com.fletch22.app.state.diff;

public class AddedChild {
	public long parentId;
	public Child child;
	public String temporaryId;
	
	public AddedChild(long parentId, Child child, String temporaryId) {
		this.temporaryId = temporaryId;
		this.parentId = parentId;
		this.child = child;
	}
}