package com.fletch22.orb.query.sort;

import java.util.List;

import com.fletch22.orb.query.ResultSet.Header;

public class SortColumnArrayExtractor {
	
	public int[] extract(Header header, List<String> attributesToSortOn) {
		return mapAttributesToHeader(header, attributesToSortOn);
	}

	private int[] mapAttributesToHeader(Header header, List<String> attributesToSortOn) {
		int[] indexes = new int[attributesToSortOn.size()];
		
		for (int i = 0; i < indexes.length; i++) {
			String attributeName = attributesToSortOn.get(i);
			indexes[i] = findIndexOfAttribute(header, attributeName);
		}
		
		return indexes;
	}

	private int findIndexOfAttribute(Header header, String attributeName) {
		int index = -1;
		
		for (int i = 0; i < header.columnNames.length; i++) {
			String column = header.columnNames[i];
			if (column.equals(attributeName)) {
				index = i;
				break;
			}
		}
		
		if (index == -1) {
			throw new RuntimeException("Encountered problem while trying to sort.");
		}
		
		return index;
	}
}
