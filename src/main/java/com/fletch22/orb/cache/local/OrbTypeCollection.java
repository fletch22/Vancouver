package com.fletch22.orb.cache.local;

import static com.googlecode.cqengine.query.QueryFactory.equal;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.index.unique.UniqueIndex;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.option.QueryOptions;

@Component
public class OrbTypeCollection {

	IndexedCollection<OrbType> types = new ConcurrentIndexedCollection<OrbType>();
	Map<Long, OrbType> quickLookup = new HashMap<Long, OrbType>();

	public OrbTypeCollection() {
		types.addIndex(UniqueIndex.onAttribute(OrbType.ID));
		types.addIndex(UniqueIndex.onAttribute(OrbType.LABEL));
	}

	public void add(OrbType orbType) {
		this.quickLookup.put(orbType.id, orbType);
		this.types.add(orbType);
	}

	public OrbType remove(long id) {
		types.remove(id);
		return this.quickLookup.remove(id);
	}

	public OrbType get(long id) {
		return this.quickLookup.get(id);
	}

	public boolean doesTypeWithLabelExist(String label) {
		Query<OrbType> query = equal(OrbType.LABEL, label);
		return types.retrieve(query).isEmpty();
	}

	public static class OrbType {
		long id;
		String label;
		BigDecimal tranDate;
		LinkedHashSet<String> customFields = new LinkedHashSet<String>();
		
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
}
