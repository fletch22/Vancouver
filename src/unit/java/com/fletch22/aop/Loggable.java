package com.fletch22.aop;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
 
	public static String TEST = "test";
}
