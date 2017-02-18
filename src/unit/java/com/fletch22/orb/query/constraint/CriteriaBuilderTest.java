package com.fletch22.orb.query.constraint;

import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.BDDMockito.*;

import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import com.fletch22.Fletch22ApplicationContext;
import com.fletch22.orb.query.criteria.Criteria;
import com.fletch22.util.RandomUtil;

public class CriteriaBuilderTest {
	
	ApplicationContext applicationContextOriginal;
	
	@Before
	public void before() {
		
		this.applicationContextOriginal = Fletch22ApplicationContext.getApplicationContext();
		
		ApplicationContext mockApplicationContext = Mockito.mock(ApplicationContext.class);
		
		RandomUtil randomUtil = Mockito.mock(RandomUtil.class);
		given(randomUtil.getRandomUuidString()).willReturn("123");
		
		given(mockApplicationContext.getBean((Class<RandomUtil>) any())).willReturn(randomUtil);
		
		Fletch22ApplicationContext.setApplicationContext(mockApplicationContext);
	}
	
	@After
	public void after() {
		Fletch22ApplicationContext.setApplicationContext(this.applicationContextOriginal);
	}

	@Test
	public void testCriteriaBuilderBasic() {

		// Arrange
		// Act
		Criteria criteria = new CriteriaBuilder(123).build();
		
		// Assert
		assertNotNull(criteria); 
	}
	
	@Test
	public void testCriteriaBuilderUnique() {

		// Arrange
		long orbTypeInternalId = 123;
		String attributeNameToBeUnique = "foo";
		
		// Act
		String[] attributeNames = { attributeNameToBeUnique };
		Criteria criteria = new CriteriaBuilder(orbTypeInternalId)
			.addAmongstUniqueConstraint(orbTypeInternalId, attributeNames)
			.build();
		
		// Assert
		assertNotNull(criteria); 
	}
	
	
	

}
