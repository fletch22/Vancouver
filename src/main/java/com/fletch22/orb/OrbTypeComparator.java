package com.fletch22.orb;

import java.util.LinkedHashSet;

import org.springframework.stereotype.Component;

import com.fletch22.orb.cache.local.CacheDifferenceReasons;
import com.fletch22.orb.cache.local.ComparisonResult;

@Component
public class OrbTypeComparator {

	public ComparisonResult areSame(OrbType orbType1, OrbType orbType2) {
		ComparisonResult comparisonResult = new ComparisonResult();
		comparisonResult.isSame = true;
		
		if (orbType1.id != orbType2.id) {
			comparisonResult.isSame = false;
			comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ORB_TYPE_ID_DIFFERENT;
		} else if (isOneObjectNull(orbType1.label, orbType2.label)) {
			comparisonResult.isSame = false;
			comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ONE_ORB_TYPE_LABEL_IS_NULL_AND_THE_OTHER_NOT;
		} else if (!orbType1.label.equals(orbType2.label)) {
			comparisonResult.isSame = false;
			comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ORB_TYPE_LABELS_ARE_DIFFERENT;
		} else if (isOneObjectNull(orbType1.tranDate, orbType2.tranDate)) {
			comparisonResult.isSame = false;
			comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ONE_ORB_TYPE_TRAN_DATE_IS_NULL_AND_THE_OTHER_NOT;
		} else if (!orbType1.tranDate.equals(orbType2.tranDate)) {
			comparisonResult.isSame = false;
			comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ORB_TYPE_TRAN_DATES_ARE_DIFFERENT;
		} else {
			comparisonResult = compareCustomFields(orbType1.customFields, orbType2.customFields);
		}
		
		return comparisonResult;
	}

	private ComparisonResult compareCustomFields(LinkedHashSet<String> customFields1, LinkedHashSet<String> customFields2) {
		ComparisonResult comparisonResult = new ComparisonResult();
		comparisonResult.isSame = true;

		if (isOneObjectNull(customFields1, customFields2)) {
			comparisonResult.isSame = false;
			comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ONE_TYPE_CUSTOM_FIELD_SET_IS_NULL_AND_THE_OTHER_NOT;
		} else if (customFields1.size() != customFields2.size()) {
			comparisonResult.isSame = false;
			comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ORB_TYPE_CUSTOM_FIELDS_ARE_DIFFERENT_SIZES;
		} else {
			for (String field1 : customFields1) {
				if (!customFields2.contains(field1)) {
					comparisonResult.isSame = false;
					comparisonResult.cacheDifferenceReasons = CacheDifferenceReasons.ORB_TYPE_CUSTOM_FIELD_DIFFERENT;
					break;
				}
			}
		}
		
		return comparisonResult;
	}

	private boolean isOneObjectNull(Object object1, Object object2) {
		return (object1 == null && object2 != null) || (object1 != null && object2 == null);
	}
}
