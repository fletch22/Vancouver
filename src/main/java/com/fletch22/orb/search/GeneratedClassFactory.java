package com.fletch22.orb.search;

import com.googlecode.cqengine.attribute.SimpleNullableAttribute;
import com.googlecode.cqengine.codegen.AttributeBytecodeGenerator;

public class GeneratedClassFactory {

	public static Class<? extends SimpleNullableAttribute<Car, String>> getInstance() {
		return AttributeBytecodeGenerator.generateSimpleNullableAttributeForParameterizedGetter(Car.class, String.class, "getListValue", "2", "LIST_VALUE");
	}
}