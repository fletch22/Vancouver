package com.fletch22.app.designer;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fletch22.app.designer.reference.ReferenceResolverService;

public abstract class DomainService<T extends Parent, U extends Child> {
	
	Logger logger = LoggerFactory.getLogger(DomainService.class);
	
	@Autowired
	ReferenceResolverService referenceResolver;
	
	public void addToParent(T parent, U child) {
		connectParentAndChild(parent, child);
		save(parent);
	}
	
	public abstract void save(T t);
	
	public abstract T get(long id);
	
	public void clearAndResolveAllDescendents(Parent orbBasedComponent) {
		referenceResolver.clearAResolveAllDescendents(orbBasedComponent);
	}
	
	public void clearAndResolveNextGeneration(Parent orbBasedComponent) {
		referenceResolver.clearAndResolveNextGeneration(orbBasedComponent);
	}

	protected void connectParentAndChild(T parent, U child) {
		clearAndResolveNextGeneration(parent);
		child.setParentId(parent.getId());
		parent.getChildren().addChild(child);
	}
	
	protected void validatePropertiesSimple(Map<String, String> properties, LinkedHashSet<String> attributeSet) {
		for (Entry<String, String> entry: properties.entrySet()) {
			String property = entry.getKey();
			boolean doesContainerProperty = attributeSet.contains(property);
			if (!doesContainerProperty) {
				throw new RuntimeException(String.format("Encountered problem updating domain properties. Encountered unrecognized property '%'.", property));
			}
		}
	}
	
	public abstract T createInstance(Map<String, String> props);
	
	public abstract T update(long id, Map<String, String> properties);
}
