package com.fletch22.app.designer;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ViewAttributesCollector {
	
	Logger logger = LoggerFactory.getLogger(ViewAttributesCollector.class);
	
	private static final String ATTR_ATTRIBUTE_LIVE_LIST = "ATTRIBUTE_LIVE_LIST";
	private static final String ATTR_TYPE_LABEL = "TYPE_LABEL";
	private static final String BASE_NAMESPACE = "com.fletch22.app.designer";
	
	private static HashMap<String, Set<String>> collection = null;
	
	public Map<String, Set<String>> collect() {

		if (ViewAttributesCollector.collection == null) {
			ViewAttributesCollector.collection = new HashMap<>();
			Reflections reflections = new Reflections(BASE_NAMESPACE);    
			Set<Class<? extends OrbBasedComponent>> classes = reflections.getSubTypesOf(OrbBasedComponent.class);
			
			for (Class<? extends OrbBasedComponent> clazz: classes) {
				Optional<Field> optionalField = this.hasField(clazz, ATTR_ATTRIBUTE_LIVE_LIST);
				if (optionalField.isPresent()) {
					Field attributeLiveList = optionalField.get();
					Set<String> viewAttributes = (Set<String>) this.getFieldStaticValue(attributeLiveList);
					optionalField = this.hasField(clazz, ATTR_TYPE_LABEL);
					if (optionalField.isPresent()) {
						Field fieldTypeLabel = optionalField.get();
						String typeLabel = (String) this.getFieldStaticValue(fieldTypeLabel);
						ViewAttributesCollector.collection.put(typeLabel, viewAttributes);
					} else {
						String message = String.format("Encountered problem trying to get TYPE_LABEL on '%s'. Should have been there. Updated the class and restart the app.", clazz.getSimpleName());
						throw new RuntimeException(message);
					}
				}
			}
		}
		
		return collection;
	}
	
	private Object getFieldStaticValue(Field field) {
		try {
			return field.get(null);
		} catch (Exception e) {
			String message = String.format("Encountered problem while trying to get static field's attribute valuef for '%s'", field.getName());
			throw new RuntimeException(message);
		}
	}
	
	private Optional<Field> hasField(@SuppressWarnings("rawtypes") Class clazz, String attributeName) {
		Field[] fields = clazz.getFields();
		Optional<Field> fieldOpt = Optional.empty();
		
		for (Field field: fields) {
			if (field.getName().equals(attributeName)) {
				fieldOpt = Optional.of(field);
				break;
			}
		}
		return fieldOpt;
	}
}
