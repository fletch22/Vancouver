package com.fletch22.orb;

import java.util.LinkedHashMap;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.fletch22.orb.cache.local.CacheDifferenceReasons;
import com.fletch22.orb.cache.local.ComparisonResult;

@Component
public class OrbComparator {

	public ComparisonResult areSame(Orb orb1, Orb orb2) {
		ComparisonResult comparisonResult = new ComparisonResult();
		comparisonResult.isSame = true;
		
		if (orb1.getOrbInternalId() != orb2.getOrbInternalId()) {
			comparisonResult.isSame = false;
			comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ORB_ID_DIFFERENT;
		} else if (isOneObjectNull(orb1.getTranDate(), orb2.getTranDate())) {
			comparisonResult.isSame = false;
			comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ONE_ORB_TRAN_DATE_IS_NULL_AND_THE_OTHER_NOT;
		} else if (!orb1.getTranDate().equals(orb2.getTranDate())) {
			comparisonResult.isSame = false;
			comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ORB_TRAN_DATES_ARE_DIFFERENT;
		} else {
			comparisonResult = compareProperties(orb1.getUserDefinedProperties(), orb2.getUserDefinedProperties());
		}
		
		return comparisonResult;
	}

	private ComparisonResult compareProperties(LinkedHashMap<String, String> userDefinedProperties1, LinkedHashMap<String, String> userDefinedProperties2) {
		ComparisonResult comparisonResult = new ComparisonResult();
		comparisonResult.isSame = true;
		
		if (isOneObjectNull(userDefinedProperties1, userDefinedProperties2)) {
			comparisonResult.isSame = false;
			comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ONE_ORB_PROPERTIES_IS_NULL_AND_THE_OTHER_NOT;
		} else if (userDefinedProperties2.size() != userDefinedProperties2.size()) {
			comparisonResult.isSame = false;
			comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ORB_PROPERTIES_ARE_DIFFERENT_SIZES;
		} else {
			comparisonResult = compareEachProperty(userDefinedProperties1, userDefinedProperties2);
		}
		
		return comparisonResult;
	}

	private ComparisonResult compareEachProperty(LinkedHashMap<String, String> userDefinedProperties1, LinkedHashMap<String, String> userDefinedProperties2) {
		ComparisonResult comparisonResult = new ComparisonResult();
		comparisonResult.isSame = true;
		
		Set<String> keySet1 = userDefinedProperties1.keySet();
		for (String key1 : keySet1) {
			if (!userDefinedProperties2.containsKey(key1)) {
				comparisonResult.isSame = false;
				comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ORB_PROPERTIES_KEYS_ARE_DIFFERENT;
				break;
			} else {
				String value1 = userDefinedProperties1.get(key1);
				String value2 = userDefinedProperties2.get(key1);
				if (isOneObjectNull(value1, value2)) {
					comparisonResult.isSame = false;
					comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ONE_ORB_PROPERTIES_VALUE_IS_NULL_AND_THE_OTHER_NOT;
				} else if (!value1.equals(value2)) {
					comparisonResult.isSame = false;
					comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ORB_PROPERTIES_VALUES_ARE_DIFFERENT;
				}
			}
		}
		
		return comparisonResult;
	}

	private boolean isOneObjectNull(Object object1, Object object2) {
		return (object1 == null && object2 != null) || (object1 != null && object2 == null);
	}
}
