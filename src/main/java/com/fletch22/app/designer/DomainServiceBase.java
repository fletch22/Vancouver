package com.fletch22.app.designer;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;

public abstract class DomainServiceBase<T extends OrbBasedComponent> {

	public abstract void save(T t);
	
	public abstract T get(long id);

	public void validatePropertiesSimple(Map<String, String> properties, LinkedHashSet<String> attributeSet) {
		for (Entry<String, String> entry: properties.entrySet()) {
			String property = entry.getKey();
			boolean doesContainerProperty = attributeSet.contains(property);
			if (!doesContainerProperty) {
				throw new RuntimeException(String.format("Encountered problem updating domain properties. Encountered unrecognized property '%s'.", property));
			}
		}
	}
	
	public abstract T createInstance(Map<String, String> props);
	
	public abstract T update(long id, Map<String, String> properties);
}
