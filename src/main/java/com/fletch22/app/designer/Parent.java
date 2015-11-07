package com.fletch22.app.designer;


public abstract class Parent extends OrbBasedComponent {

	public static final String ATTR_CHILDREN = "children";
	private ComponentChildren children = new ComponentChildren();
	
	public ComponentChildren getChildren() {
		return children;
	}
	
	@Override
	public boolean isCanHaveChildren() {
		return true;
	}
}
