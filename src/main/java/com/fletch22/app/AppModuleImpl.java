package com.fletch22.app;

import java.util.LinkedHashSet;

public enum AppModuleImpl implements AppModule {
	
	FrontEndState(com.fletch22.app.state.FrontEndState.ATTRIBUTE_LIST, com.fletch22.app.state.FrontEndState.TYPE_LABEL);
	
	private LinkedHashSet<String> attributes;
	private String typeLabel;
	
	private AppModuleImpl(LinkedHashSet<String> attributes, String typeLabel) {
		this.attributes = attributes;
		this.typeLabel = typeLabel;
	}

	public LinkedHashSet<String> getAttributes() {
		return this.attributes;
	}
	
	public String getTypeLabel() {
		return this.typeLabel;
	}
}
