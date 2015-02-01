package com.fletch22.util;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrbUtil {
	
	@Autowired
	BigDecimalForDataStorageUtil bigDecimalForDataStorageUtil;

	private static final String ATTRIBUTE_ID = "id";
	private static final String ATTRIBUTE_LABEL = "label";
	private static final String ATTRIBUTE_TRAN_DATE = "tranDate";
	
	public HashMap<String, String> createCoreProperties(long id, String label, BigDecimal tranDate) {
		HashMap<String, String> map = new HashMap<>();
		
		map.put(ATTRIBUTE_ID, String.valueOf(id));
		map.put(ATTRIBUTE_LABEL, label);
		map.put(ATTRIBUTE_TRAN_DATE, this.bigDecimalForDataStorageUtil.convertForDataStorage(tranDate));
		
		return map;
	}
}
