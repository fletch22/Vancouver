package com.fletch22.orb.query.sort;

import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;

public class OrbComparator extends SortComparator implements Comparator<Orb> {
	
	static Logger logger = LoggerFactory.getLogger(OrbComparator.class);

	String[] columnNameArray = null; 

	public OrbComparator(List<GrinderSortInfo> grinderSortInfoList, OrbType orbType) {
		this.grinderSortInfoList = grinderSortInfoList;
		columnNameArray = orbType.customFields.toArray(new String[orbType.customFields.size()]);
	}
	
	@Override
	public int compare(Orb orb1, Orb orb2) {
		int value = 0;
		
		if (this.grinderSortInfoList.size() > 0) {
			
			for (GrinderSortInfo grinderSortInfo : grinderSortInfoList) {
				String valueToCompare1 = orb1.getUserDefinedProperties().get(columnNameArray[grinderSortInfo.sortIndex]);
				String valueToCompare2 = orb2.getUserDefinedProperties().get(columnNameArray[grinderSortInfo.sortIndex]);
				
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
