package com.fletch22.orb.query.sort;

import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.orb.query.ResultSet.Row;

public class RowComparator extends SortComparator implements Comparator<Row> {
	
	static Logger logger = LoggerFactory.getLogger(RowComparator.class);

	public RowComparator(List<GrinderSortInfo> grinderSortInfoList) {
		this.grinderSortInfoList = grinderSortInfoList;
	}
	
	@Override
	public int compare(Row row1, Row row2) {
		int value = 0;
		
		if (this.grinderSortInfoList.size() > 0) {
			
			for (GrinderSortInfo grinderSortInfo : grinderSortInfoList) {
				String valueToCompare1 = row1.cells[grinderSortInfo.sortIndex];
				String valueToCompare2 = row2.cells[grinderSortInfo.sortIndex];
				
				int direction = getDirection(grinderSortInfo);
				
				value = compare(grinderSortInfo.sortType, valueToCompare1, valueToCompare2);
				value = direction * value;
				
				if (value != 0) {
					return value;
				}
			}
		}
		
		return value;
	}
}
