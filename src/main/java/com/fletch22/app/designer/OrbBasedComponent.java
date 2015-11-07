package com.fletch22.app.designer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.orb.Orb;

public abstract class OrbBasedComponent {
	
	Logger logger = LoggerFactory.getLogger(OrbBasedComponent.class);
	
	public static final String ATTR_CHILDREN = "children";
	protected static final long UNSET_ID = -1;
	public static String ATTR_PARENT = "parent";
	private Orb orbOriginal;
	private long id = UNSET_ID;
	private long parentId;
	
	private ComponentChildren children = new ComponentChildren();
	
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
	
	public boolean isNew() {
		return this.getId() == UNSET_ID;
	}
	
	public abstract String getTypeLabel();
}

