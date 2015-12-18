package com.fletch22.util.json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JsonUtil {
	
	@Autowired
	GsonFactory gsonFactory;

	public String escapeJsonIllegals(String value) {
		value = gsonFactory.getInstance().toJson(value);
		return value.substring(1, value.length() - 1);
	}
	
	public String unescapeJsonIllegals(String value) 
	{
        return gsonFactory.getInstance().fromJson(value, String.class);
	}
}
