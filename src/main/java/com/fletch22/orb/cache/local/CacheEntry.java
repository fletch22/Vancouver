package com.fletch22.orb.cache.local;

import java.util.ArrayList;
import java.util.Set;

import com.fletch22.orb.Orb;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;

public class CacheEntry {
	
	long id;
	String label;
	long typeId;
	ArrayList<String> attributes = new ArrayList<String>();

	public CacheEntry(long id, long typeId, String label, ArrayList<String> list) {
		init(id, typeId, label, list);
	}
	
	public CacheEntry(Orb orb) {
		
		Set<String> propertyKeys = orb.getUserDefinedProperties().keySet();
		ArrayList<String> propertyValues = new ArrayList<String>();
		for (String key: propertyKeys) {
			propertyValues.add(orb.getUserDefinedProperties().get(key));
		}
		
		init(orb.getOrbInternalId(), orb.getOrbTypeInternalId(), null, propertyValues);
	}
	
	private void init(long id, long typeId, String label, ArrayList<String> list) {
		this.id = id;
		this.label = label;
		this.attributes = list;
	}
	
	public long getId() {
		return id;
	}

	public String getValue(String index) {
		int i = Integer.parseInt(index);
		
		return (this.attributes.size() > i) ? attributes.get(i) : null;
	}
	
	public ArrayList<String> getAttributes() {
		return attributes;
	}

	public static final SimpleAttribute<CacheEntry, Long> ID = new SimpleAttribute<CacheEntry, Long>("ID") {
		public Long getValue(CacheEntry cacheEntry, QueryOptions queryOptions) {
			return cacheEntry.id;
		}
	};
}