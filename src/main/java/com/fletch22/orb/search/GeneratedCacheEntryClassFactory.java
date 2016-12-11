package com.fletch22.orb.search;

import com.fletch22.orb.cache.local.CacheEntry;
import com.googlecode.cqengine.attribute.SimpleNullableAttribute;
import com.googlecode.cqengine.codegen.AttributeBytecodeGenerator;

public class GeneratedCacheEntryClassFactory {

	public static Class<? extends SimpleNullableAttribute<CacheEntry, String>> getInstance(int nthElement, String uniquefier) {
		return AttributeBytecodeGenerator.generateSimpleNullableAttributeForParameterizedGetter(CacheEntry.class, String.class, "getValue", String.valueOf(nthElement), uniquefier);
	}
	
	public static Class<? extends SimpleNullableAttribute<CacheEntry, String>> getCompositeInstance(int[] nthElement, String uniquefier) {
		
		String nthElementAsString = "";
		for (int i = 0; i < nthElement.length; i++) {
			nthElementAsString += String.valueOf(nthElement[i]);
			if (i + 1 < nthElement.length) {
				nthElementAsString += ",";
			}
		}
		
		return AttributeBytecodeGenerator.generateSimpleNullableAttributeForParameterizedGetter(CacheEntry.class, String.class, "getCompositeValues", nthElementAsString, uniquefier);
	}
}