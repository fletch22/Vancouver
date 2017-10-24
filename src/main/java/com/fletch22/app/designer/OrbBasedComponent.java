package com.fletch22.app.designer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.orb.Orb;

public abstract class OrbBasedComponent implements Child {

	Logger logger = LoggerFactory.getLogger(OrbBasedComponent.class);

	public static final long UNSET_ID = -1;
	public static String ATTR_PARENT = "parent";
	private Orb orbOriginal;
	private long id = UNSET_ID;
	private long parentId = UNSET_ID;
	public String ordinal = Child.UNSET_ORDINAL;

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

	public boolean isNew() {
		return this.getId() == UNSET_ID;
	}

	public boolean hasParent() {
		return this.getParentId() != UNSET_ID;
	}

	public boolean isCanHaveChildren() {
		return false;
	}
	
	public long getOrdinalAsNumber() {
		String ordinal = (this.ordinal == null) ? OrbBasedComponent.UNSET_ORDINAL : this.ordinal;
		return Long.parseLong(ordinal);
	}

	public String getOrdinal() {
		String ordinal = (this.ordinal == null) ? OrbBasedComponent.UNSET_ORDINAL : this.ordinal;
		return ordinal;
	}

	public void setOrdinal(String ordinal) {
		this.ordinal = ordinal;
	}
}
