package com.fletch22.app;

import java.util.LinkedHashSet;

public interface AppModule {
	public LinkedHashSet<String> getAttributes();
	public String getTypeLabel();
}
