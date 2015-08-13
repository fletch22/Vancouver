package com.fletch22.orb.cache.local;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;

public class CacheEntry {

	long id;
	String label;
	long typeId;
	BigDecimal tranDate;
	List<String> attributes = new ArrayList<String>();

	public CacheEntry(long id, long typeId, String label, BigDecimal tranDate, List<String> list) {
		this.id = id;
		this.label = label;
		this.attributes = list;
		this.tranDate = tranDate;
	}
	
	public long getId() {
		return id;
	}

	public String getValue(String index) {
		int i = Integer.parseInt(index);

		return (this.attributes.size() > i) ? attributes.get(i) : null;
	}

	public static final SimpleAttribute<CacheEntry, Long> ID = new SimpleAttribute<CacheEntry, Long>("ID") {
		public Long getValue(CacheEntry cacheEntry, QueryOptions queryOptions) {
			return cacheEntry.id;
		}
	};
}