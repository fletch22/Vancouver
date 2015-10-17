 package com.fletch22.orb.cache.reference;

import static org.junit.Assert.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import spock.lang.Specification

import com.fletch22.orb.cache.local.AttributeArrows

class ReferenceCollectionSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(ReferenceCollectionSpec)

	def test() {
		
		given:
		ReferenceCollection referenceCollection = new ReferenceCollection()
		
		Map<Long, AttributeArrows> attributeArrowsMapAttrRefMap = new HashMap<Long, AttributeArrows>()
		
		AttributeArrows attributeArrowsAttr = new AttributeArrows()
		attributeArrowsAttr.attributesContainingArrows.add("foo");
		attributeArrowsMapAttrRefMap.put(123L, attributeArrowsAttr)
		
		Map<Long, AttributeArrows> attributeArrowsOrbRefMap = new HashMap<Long, AttributeArrows>()
		AttributeArrows attributeArrowsOrb = new AttributeArrows()
		attributeArrowsOrb.attributesContainingArrows.add("bar");
		attributeArrowsOrbRefMap.put(345L, attributeArrowsOrb)
		
		AttributeArrows attributeArrowsOrb2 = new AttributeArrows()
		attributeArrowsOrb2.attributesContainingArrows.add("foo");
		attributeArrowsOrb2.attributesContainingArrows.add("pumpkin");
		attributeArrowsOrbRefMap.put(123L, attributeArrowsOrb2)
		
		when:
		Map<Long, AttributeArrows> map = referenceCollection.combineAttributeArrowMaps(attributeArrowsMapAttrRefMap, attributeArrowsOrbRefMap)
		
		def attributeArrowsCombined = map.get(123L)
		
		then: 'Arrow maps should have combined intelligently'
		map != null
		map.size() == 2
		
		and: 'One of the attributes should have combined with the other entry.'
		attributeArrowsCombined.attributesContainingArrows.size() == 2
		
	}

}
