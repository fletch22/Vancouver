package com.fletch22.orb.search;

import com.googlecode.cqengine.attribute.SimpleNullableAttribute;
import com.googlecode.cqengine.codegen.AttributeBytecodeGenerator;

public class GeneratedClassFactory {

	public static Class<? extends SimpleNullableAttribute<Car, String>> getInstance(int nthElement) {
		return AttributeBytecodeGenerator.generateSimpleNullableAttributeForParameterizedGetter(Car.class, String.class, "getListValue", String.valueOf(nthElement), "LIST_VALUE");
	}
}