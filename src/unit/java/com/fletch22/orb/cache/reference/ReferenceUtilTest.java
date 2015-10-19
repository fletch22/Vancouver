package com.fletch22.orb.cache.reference;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Test;

public class ReferenceUtilTest {

	@Test
	public void testComposeReferences() {
		
		// Arrange
		ReferenceUtil referenceUtil = new ReferenceUtil();
		
		String fooReference = referenceUtil.composeReference(123, "foo");
		String barReference = referenceUtil.composeReference(345, "bar");
		String bananaReference = referenceUtil.composeReference(345, "banana");
		
		
		Set<String> references = new LinkedHashSet<String>();
		references.add(fooReference);
		references.add(barReference);
		references.add(bananaReference);
		
		// Act
		StringBuffer actual = referenceUtil.composeReferences(references);
		
		// Assert
		assertEquals("Should be equal to expected string.", fooReference + "," + barReference + "," + bananaReference, actual.toString());
	}

}
