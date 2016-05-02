package com.fletch22.app.state.diff;

public class AddedChild {
	public long parentId;
	public Child child;
	
	public AddedChild(long parentId, Child child) {
		this.parentId = parentId;
		this.child = child;
	}
}