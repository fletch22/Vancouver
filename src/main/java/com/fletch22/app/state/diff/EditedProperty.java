package com.fletch22.app.state.diff;

public class EditedProperty {
	
	public long id;
	public String property;
	public String newValue;
	
	public EditedProperty(long id, String property, String newValue) {
		this.id = id;
		this.property = property;
		this.newValue = newValue;
	}
}
