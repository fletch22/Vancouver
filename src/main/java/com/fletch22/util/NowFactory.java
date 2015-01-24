package com.fletch22.util;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

@Component	
public class NowFactory {

	public DateTime getNow() {
		return DateTime.now();
	}
}
