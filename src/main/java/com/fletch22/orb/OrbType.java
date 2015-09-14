package com.fletch22.orb;

import java.math.BigDecimal;
import java.util.LinkedHashSet;

import com.fletch22.orb.serialization.GsonSerializable;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;

public class OrbType implements GsonSerializable {
	
	public long id;
	public String label;
	public BigDecimal tranDate;
	public LinkedHashSet<String> customFields = new LinkedHashSet<String>();
	
	public OrbType(long id, String label, BigDecimal tranDate, LinkedHashSet<String> customFields) {
		this.id = id;
		this.label = label;
		this.tranDate = tranDate;
		this.customFields = customFields;
	}

	public LinkedHashSet<String> addField(String customFieldName) {
		customFields.add(customFieldName);
		return this.customFields;
	}
	
	public static final SimpleAttribute<OrbType, Long> ID = new SimpleAttribute<OrbType, Long>("ID") {
		public Long getValue(OrbType orbType, QueryOptions queryOptions) {
			return orbType.id;
		}
	};

	public static final SimpleAttribute<OrbType, String> LABEL = new SimpleAttribute<OrbType, String>("LABEL") {
		public String getValue(OrbType orbType, QueryOptions queryOptions) {
			return orbType.label;
		}
	};
}