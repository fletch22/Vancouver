package com.fletch22.orb.cache.reference;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fletch22.orb.IntegrationSystemInitializer;
import com.fletch22.orb.IntegrationTests;
import com.fletch22.util.RandomUtil;

@org.junit.experimental.categories.Category(IntegrationTests.class)
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
public class ReferenceCollectionTest {
	
	static Logger logger = LoggerFactory.getLogger(ReferenceCollectionTest.class);
	
	@Autowired
	ReferenceCollection referenceCollection;
	
	@Autowired
	RandomUtil randomUtil;
	
	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer;

//	@Test
//	public void testRemoveArrows() {
//		
//		// Arrange
//		ReferenceCollection referenceCollection = new ReferenceCollection();
//		
//		// Act
////		referenceCollection.removeArrows(orbInternalIdArrow, attributeNameArrow, decomposedKeyList)
//		
//		// Assert
//	}
	
	@Before
	public void before() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems();
	}
	
	@After
	public void cleanup() {
		integrationSystemInitializer.nukeAndPaveAllIntegratedSystems();
	}
	
	@Test
	public void testAddReferences() {
		
		// Arrange
		long orbInternalId = 123;
		String attributeName = "foo";
	
		int totalOrbRefs = 20;
		List<DecomposedKey> orbRefList = addOrbReferences(totalOrbRefs);
		
		int totalAttrRefs = 10;
		List<DecomposedKey> attrRefList = addAttrReferences(totalAttrRefs);
		
		List<DecomposedKey> list = new ArrayList<DecomposedKey>(orbRefList);
		list.addAll(attrRefList);
		
		// Act
		referenceCollection.addReferences(orbInternalId, attributeName, list);
		
		// Assert
		assertEquals("References should match number of ones added.", totalOrbRefs + totalAttrRefs, referenceCollection.countArrows());
	}
	
	@Test
	public void testCountArrowsPointToTarget() {
		
		// Arrange
		long targetOrbInternalId = 888;
		String targetAttributeName = "foo";
	
		List<DecomposedKey> list1 = createRefList(66, 88);
		referenceCollection.addReferences(123, targetAttributeName, list1);

		long arrowOrbInternalId = 393;
		String arrowAttribute = "bar";
		
		List<DecomposedKey> list2 = new ArrayList<DecomposedKey>();
		DecomposedKey decomposedKey = new DecomposedKey(targetOrbInternalId, targetAttributeName);
		list2.add(decomposedKey);
		
		int numberOfArrowsToPoint = 12;
		for (int i = 0; i < numberOfArrowsToPoint; i++) {
			referenceCollection.addReferences(randomUtil.getRandomInteger(), arrowAttribute, list2);
		}
		
		// Act
		int countPointingToTarget = referenceCollection.countArrowsPointingToTargetAttribute(targetOrbInternalId, targetAttributeName);
		
		// Assert
		assertEquals("References should match number of ones added.", numberOfArrowsToPoint, countPointingToTarget);
	}
	
	@Test
	public void testRemoveArrows() {
		
		// Arrange
		List<DecomposedKey> targets = new ArrayList<DecomposedKey>();
		
		long arrowOrbInternalId = 456;
		String arrowAttributeName = "test";
		long targetOrbInternalId = 668;
		String targetAttributeName = "funny";
				
		DecomposedKey decomposedKey = new DecomposedKey(targetOrbInternalId, targetAttributeName);
		targets.add(decomposedKey);
		decomposedKey = new DecomposedKey(13 + targetOrbInternalId, "soo" + targetAttributeName);
		targets.add(decomposedKey);
		
		referenceCollection.addReferences(arrowOrbInternalId, arrowAttributeName, targets);

		int count = referenceCollection.countArrowsPointingToTargetAttribute(targetOrbInternalId, targetAttributeName);
		assertEquals("Should be one arrow pointing to target.", 1, count);
		
		// Act
		referenceCollection.removeArrows(arrowOrbInternalId, arrowAttributeName, targets);
		
		// Assert
		count = referenceCollection.countArrowsPointingToTargetAttribute(targetOrbInternalId, targetAttributeName);
		assertEquals("Should be zero arrows pointing to target.", 0, count);
	}
	
	private List<DecomposedKey> createRefList(long orbInternalId, String attributeName, int numberOfAttrRefs, int numberOfOrbRefs) {

		List<DecomposedKey> orbRefList = addOrbReferences(numberOfOrbRefs);
		List<DecomposedKey> attrRefList = addAttrReferences(numberOfAttrRefs);
		
		List<DecomposedKey> list = new ArrayList<DecomposedKey>(orbRefList);
		list.addAll(attrRefList);
		
		return list;
	}
	
	private List<DecomposedKey> createRefList(int numberOfAttrRefs, int numberOfOrbRefs) {

		List<DecomposedKey> orbRefList = addOrbReferences(numberOfAttrRefs);
		List<DecomposedKey> attrRefList = addAttrReferences(numberOfOrbRefs);
		
		List<DecomposedKey> list = new ArrayList<DecomposedKey>(orbRefList);
		list.addAll(attrRefList);
		
		return list;
	}

	private List<DecomposedKey> addAttrReferences(int numberToAdd) {
		List<DecomposedKey> list = new ArrayList<DecomposedKey>();
		
		for (int i = 0; i < numberToAdd; i++) {
			DecomposedKey decomposedKey = new DecomposedKey(randomUtil.getRandomInteger(), "foo" + randomUtil.getRandomString());
			list.add(decomposedKey);
		}
		return list;
	}
	
	private List<DecomposedKey> addOrbReferences(int numberToAdd) {
		List<DecomposedKey> list = new ArrayList<DecomposedKey>();
		
		for (int i = 0; i < numberToAdd; i++) {
			DecomposedKey decomposedKey = new DecomposedKey(randomUtil.getRandomInteger());
			list.add(decomposedKey);
		}
		return list;
	}
}
