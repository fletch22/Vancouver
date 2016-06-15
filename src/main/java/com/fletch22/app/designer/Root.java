package com.fletch22.app.designer;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

@Component
public class Root {

	public String startupTimestamp = String.valueOf(DateTime.now().getMillis());
}
