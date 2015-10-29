package com.fletch22.app.designer;

import java.util.ArrayList;

import com.fletch22.orb.Orb;

public abstract class OrbBasedComponent {
	
	private Orb orbOriginal;
	private long id;
	private ArrayList<OrbBasedComponent> children = new ArrayList<OrbBasedComponent>();
	
	public long getId() {
		return this.id;
	}
	
	public ArrayList<OrbBasedComponent> getChildren() {
		return this.children;
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
}

