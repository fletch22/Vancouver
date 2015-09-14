package com.fletch22.util.json;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class JsonUtil {
	
	Gson gson = new Gson();

	public String escapeJsonIllegals(String value) {
		value = gson.toJson(value);
		return value.substring(1, value.length() - 1);
	}
	
	public String unescapeJsonIllegals(String value) 
	{
        return gson.fromJson(value, String.class);
	}
}
