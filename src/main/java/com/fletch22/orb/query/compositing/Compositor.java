package com.fletch22.orb.query.compositing;

public class Compositor {

	public static final String UNIQUEIFIER = "idasf09dsa9a032kjjklfdsDAfdaasdfasdifdu9s80";
	
	public static String getCompositeValue(String[] values) {
		return String.join(UNIQUEIFIER, values);
	}
}
