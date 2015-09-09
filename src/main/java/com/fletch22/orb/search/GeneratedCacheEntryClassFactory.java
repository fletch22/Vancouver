package com.fletch22.orb.search;

import com.fletch22.orb.cache.local.CacheEntry;
import com.googlecode.cqengine.attribute.SimpleNullableAttribute;
import com.googlecode.cqengine.codegen.AttributeBytecodeGenerator;

public class GeneratedCacheEntryClassFactory {

	public static Class<? extends SimpleNullableAttribute<CacheEntry, String>> getInstance(int nthElement, String uniquefier) {
		return AttributeBytecodeGenerator.generateSimpleNullableAttributeForParameterizedGetter(CacheEntry.class, String.class, "getValue", String.valueOf(nthElement), uniquefier);
	}
}