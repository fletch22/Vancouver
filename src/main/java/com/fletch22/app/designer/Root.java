package com.fletch22.app.designer;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

@Component
public class Root {
	public String startupTimestamp = getStartupTimestamp();
	
	public void resetStartupTimestamp() {
		this.startupTimestamp = getStartupTimestamp();
	}
	
	private String getStartupTimestamp() {
		return String.valueOf(DateTime.now().getMillis());
	}
}
