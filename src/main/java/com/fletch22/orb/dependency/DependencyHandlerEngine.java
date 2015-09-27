package com.fletch22.orb.dependency;

import java.util.ArrayList;
import java.util.List;

public class DependencyHandlerEngine {

	private List<DependencyHandler> dependencyHandlerList = new ArrayList<DependencyHandler>();
	
	public void addHandler(DependencyHandler dependencyHandler) {
		this.dependencyHandlerList.add(dependencyHandler);
	}
	
	public void check() {
		for (DependencyHandler dependencyHandler: this.dependencyHandlerList) {
			dependencyHandler.check();
		}
	}
}
