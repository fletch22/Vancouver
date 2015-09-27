package com.fletch22.orb.query.sort;


public abstract class SortInfo {
	public enum SortDirection {
		ASC,
		DESC
	}
	
	public enum SortType {
		NUMERIC,
		ALPHANUMERIC		
	}
	
	public static final SortDirection DEFAULT_SORT_DIRECTION = SortDirection.ASC;
	public static final SortType DEFAULT_SORT_TYPE = SortType.ALPHANUMERIC;
	
	public SortDirection sortDirection = DEFAULT_SORT_DIRECTION;
	public SortType sortType = DEFAULT_SORT_TYPE;
}
