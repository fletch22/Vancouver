package com.fletch22.orb.cache.local;

import static org.junit.Assert.*;

import org.junit.Test;

import spock.lang.Specification;

class OrbReferenceSpec extends Specification {

	@Test
	def 'testDecomposition'() {
		
		given:
		OrbReference orbReference = new OrbReference();
		
		long orbInternalId = 1234
		String attributeName = "foo-manchu"
		
		String composedKey = orbReference.composeReference(orbInternalId, attributeName)
		
		when:
		def decomposedKey = orbReference.decomposeKey(composedKey);
		
		then:
		decomposedKey.orbInternalId == orbInternalId
		decomposedKey.attributeName == attributeName
	}

}
