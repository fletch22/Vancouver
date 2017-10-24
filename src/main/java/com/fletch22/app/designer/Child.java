package com.fletch22.app.designer;

public interface Child {

	public static final String ATTR_ORDINAL = "ordinal";
	public static final String UNSET_ORDINAL = "-1";
	public static final long ORDINAL_LAST = -1;

	public void setParentId(long id);

	public String getTypeLabel();

	public long getId();

	public long getOrdinalAsNumber();

	public String getOrdinal();

	public void setOrdinal(String ordinal);
}
