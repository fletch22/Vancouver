package com.fletch22.util;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

@Component
public class BigDecimalForDataStorageUtil {
	
	public static final int MAXIMUM_DECIMAL_DIGITS = 10;

	public String convertForDataStorage(BigDecimal bigDecimal) {
		return bigDecimal.setScale(MAXIMUM_DECIMAL_DIGITS).toString();
	}
	
}
