package com.fletch22.orb;

import java.util.LinkedHashSet;

public class OrbTypeConstants {
	public static final String ORBTYPE_LABEL = "ORB_TYPE";
    public static final String ORBTYPE_QUERY_RESULT_LABEL = "ORB_TYPE_QUERY_RESULT";
	public static final int ORBTYPE_TYPE_ID_ORDINAL = 0;
	public static final int ORBTYPE_USERLABEL_FIELD_ORDINAL = 1;
	public static final int ORBTYPE_START_FIELD_ORDINAL = 2;
	public static final String ORBTYPE_DEFAULT_LABEL_X = "Orb Base Type";
    public static final int ORBTYPE_INTERNAL_ID_UNSET = -1;
    public static final int ORBTYPE_ATTR_ORDINAL_UNSET = -1;
	public static final int ORBTYPE_BASETYPE_ID = 0;
	
	public enum SystemOrbTypes {
		QUERY("query", 1);
		
		private String label;
		private long id;
		
		private SystemOrbTypes(String label, long id) {
			this.label = label;
			this.id = id;
		}

		public String getLabel() {
			return label;
		}

		public long getId() {
			return id;
		}
	}
}
