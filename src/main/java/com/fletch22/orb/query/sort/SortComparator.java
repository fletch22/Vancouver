package com.fletch22.orb.query.sort;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.orb.query.sort.SortInfo.SortType;

public abstract class SortComparator {

	static Logger logger = LoggerFactory.getLogger(SortComparator.class);

	protected List<GrinderSortInfo> grinderSortInfoList = null;

	protected Map<String, BigDecimal> mapCache = new HashMap<String, BigDecimal>();

	protected int compare(SortType sortType, String valueToCompare1, String valueToCompare2) {
		int value;

		if (SortType.NUMERIC.equals(sortType)) {
			try {
				BigDecimal value1 = null;
				BigDecimal value2 = null;

				value1 = mapCache.get(valueToCompare1);
				if (value1 == null) {
					value1 = new BigDecimal(valueToCompare1);
					mapCache.put(valueToCompare1, value1);
				}

				value2 = mapCache.get(valueToCompare2);
				if (value2 == null) {
					value2 = new BigDecimal(valueToCompare2);
					mapCache.put(valueToCompare2, value2);
				}

				value = value1.compareTo(value2);

			} catch (Exception exception) {
				String message = "Encountered problem while trying to convert value to numeric while sorting numerically. Ensure the values can be converted before sorting.";
				throw new RuntimeException(message, exception);
			}
		} else {
			value = valueToCompare1.compareTo(valueToCompare2);
		}

		return value;
	}

	protected int getDirection(GrinderSortInfo grinderSortInfo) {
		int direction = 1;
		if (SortInfo.SortDirection.DESC.equals(grinderSortInfo.sortDirection)) {
			direction = -1;
		}
		return direction;
	}
}
