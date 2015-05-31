package com.fletch22.orb.cache.local;

import java.math.BigDecimal;
import java.util.List;

import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;

public class CacheEntry {

	long id;
	String label;
	long typeId;
	BigDecimal tranDate;
	List<String> list;

	public CacheEntry(long id, long typeId, String label, BigDecimal tranDate, List<String> list) {
		this.id = id;
		this.label = label;
		this.list = list;
		this.tranDate = tranDate;
	}

	public String getValue(String index) {
		int i = Integer.parseInt(index);

		return (this.list.size() > i) ? list.get(i) : null;
	}

	public static final SimpleAttribute<CacheEntry, Long> ID = new SimpleAttribute<CacheEntry, Long>("ID") {
		public Long getValue(CacheEntry cacheEntry, QueryOptions queryOptions) {
			return cacheEntry.id;
		}
	};
}