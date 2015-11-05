package com.fletch22.app.designer;

import com.fletch22.orb.Orb;

public abstract class OrbBasedComponent {
	
	public static final String ATTR_CHILDREN = "children";
	public static String ATTR_PARENT = "parent";
	private Orb orbOriginal;
	private long id;
	private long parentId;
	
	private ComponentChildren children;
	
	public long getId() {
		return this.id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public Orb getOrbOriginal() {
		return orbOriginal;
	}

	public void setOrbOriginal(Orb orbOriginal) {
		this.orbOriginal = orbOriginal;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public ComponentChildren getChildren() {
		return children;
	}
	
	public abstract String getTypeLabel();
}

