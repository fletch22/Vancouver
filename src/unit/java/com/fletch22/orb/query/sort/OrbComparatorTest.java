package com.fletch22.orb.query.sort;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;

import org.junit.Test;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;

public class OrbComparatorTest {

	@Test
	public void testSortSuccess() {

		// Arrange
		List<GrinderSortInfo> grinderSortInfoArray = new ArrayList<GrinderSortInfo>();

		GrinderSortInfo grinderSortInfo = new GrinderSortInfo();
		grinderSortInfo.sortIndex = 0;
		grinderSortInfoArray.add(grinderSortInfo);
		
		LinkedHashSet<String> customFields = new LinkedHashSet<String>();
		customFields.add("foo");
		OrbType orbType = new OrbType(123L, "test", null, customFields);

		OrbComparator orbComparator = new OrbComparator(grinderSortInfoArray, orbType);

		List<Orb> orbs = new ArrayList<Orb>();

		String[] originalArray = new String[4];
		originalArray[0] = "d";
		originalArray[1] = "c";
		originalArray[2] = "b";
		originalArray[3] = "a";

		for (int i = 0; i < originalArray.length; i++) {
			Orb orb = new Orb();
			LinkedHashMap<String, String> userDefinedProperties = new LinkedHashMap<String, String>();
			orb.setUserDefinedProperties(userDefinedProperties);
			userDefinedProperties.put("foo", originalArray[i]);
			orbs.add(orb);
		}

		Collections.sort(orbs, orbComparator);

		// Assert
		assertEquals("a", orbs.get(0).getUserDefinedProperties().get("foo"));
		assertEquals("b", orbs.get(1).getUserDefinedProperties().get("foo"));
		assertEquals("c", orbs.get(2).getUserDefinedProperties().get("foo"));
		assertEquals("d", orbs.get(3).getUserDefinedProperties().get("foo"));
	}

	@Test
	public void testSortMultipleColumnsSuccess() {

		// Arrange
		List<GrinderSortInfo> grinderSortInfoArray = new ArrayList<GrinderSortInfo>();

		GrinderSortInfo grinderSortInfo1 = new GrinderSortInfo();
		grinderSortInfo1.sortIndex = 0;
		grinderSortInfoArray.add(grinderSortInfo1);

		GrinderSortInfo grinderSortInfo2 = new GrinderSortInfo();
		grinderSortInfo2.sortIndex = 2;
		grinderSortInfoArray.add(grinderSortInfo2);
		
		LinkedHashSet<String> customFields = new LinkedHashSet<String>();
		customFields.add("foo");
		customFields.add("cat");
		customFields.add("bar");
		OrbType orbType = new OrbType(123L, "test", null, customFields);

		OrbComparator orbComparator = new OrbComparator(grinderSortInfoArray, orbType);

		List<Orb> orbs = new ArrayList<Orb>();

		String[] originalArray = new String[4];
		originalArray[0] = "d";
		originalArray[1] = "c";
		originalArray[2] = "b";
		originalArray[3] = "a";

		for (int i = 0; i < originalArray.length; i++) {
			Orb orb = new Orb();
			LinkedHashMap<String, String> userDefinedProperties = new LinkedHashMap<String, String>();
			orb.setUserDefinedProperties(userDefinedProperties);
			userDefinedProperties.put("foo", "a");
			userDefinedProperties.put("cat", "asdf");
			userDefinedProperties.put("bar", originalArray[i]);
			
			orbs.add(orb);
		}

		Collections.sort(orbs, orbComparator);

		// Assert
		assertEquals("a", orbs.get(0).getUserDefinedProperties().get("bar"));
		assertEquals("b", orbs.get(1).getUserDefinedProperties().get("bar"));
		assertEquals("c", orbs.get(2).getUserDefinedProperties().get("bar"));
		assertEquals("d", orbs.get(3).getUserDefinedProperties().get("bar"));
	}

	@Test
	public void testSortDescending() {

		// Arrange
		List<GrinderSortInfo> grinderSortInfoArray = new ArrayList<GrinderSortInfo>();

		GrinderSortInfo grinderSortInfo = new GrinderSortInfo();
		grinderSortInfo.sortIndex = 0;
		grinderSortInfo.sortDirection = SortInfo.SortDirection.DESC;
		grinderSortInfoArray.add(grinderSortInfo);
		
		LinkedHashSet<String> customFields = new LinkedHashSet<String>();
		customFields.add("foo");
		OrbType orbType = new OrbType(123L, "test", null, customFields);

		OrbComparator orbComparator = new OrbComparator(grinderSortInfoArray, orbType);

		List<Orb> orbs = new ArrayList<Orb>();

		String[] originalArray = new String[5];
		originalArray[0] = "a";
		originalArray[1] = "d";
		originalArray[2] = "c";
		originalArray[3] = "b";
		originalArray[4] = "z";

		for (int i = 0; i < originalArray.length; i++) {
			Orb orb = new Orb();
			LinkedHashMap<String, String> userDefinedProperties = new LinkedHashMap<String, String>();
			orb.setUserDefinedProperties(userDefinedProperties);
			userDefinedProperties.put("foo", originalArray[i]);
			orbs.add(orb);
		}

		Collections.sort(orbs, orbComparator);

		// Assert
		assertEquals("z", orbs.get(0).getUserDefinedProperties().get("foo"));
		assertEquals("d", orbs.get(1).getUserDefinedProperties().get("foo"));
		assertEquals("c", orbs.get(2).getUserDefinedProperties().get("foo"));
		assertEquals("b", orbs.get(3).getUserDefinedProperties().get("foo"));
		assertEquals("a", orbs.get(4).getUserDefinedProperties().get("foo"));
	}

	@Test
	public void testSortNumeric() {

		// Arrange
		List<GrinderSortInfo> grinderSortInfoArray = new ArrayList<GrinderSortInfo>();

		GrinderSortInfo grinderSortInfo = new GrinderSortInfo();
		grinderSortInfo.sortIndex = 0;
		grinderSortInfo.sortType = SortInfo.SortType.NUMERIC;
		grinderSortInfoArray.add(grinderSortInfo);
		
		LinkedHashSet<String> customFields = new LinkedHashSet<String>();
		customFields.add("foo");
		OrbType orbType = new OrbType(123L, "test", null, customFields);
		

		OrbComparator orbComparator = new OrbComparator(grinderSortInfoArray, orbType);

		List<Orb> orbs = new ArrayList<Orb>();

		String[] originalArray = new String[5];
		originalArray[0] = "1";
		originalArray[1] = ".13240";
		originalArray[2] = "2414";
		originalArray[3] = "00123213";
		originalArray[4] = "90099900";

		for (int i = 0; i < originalArray.length; i++) {
			Orb orb = new Orb();
			LinkedHashMap<String, String> userDefinedProperties = new LinkedHashMap<String, String>();
			orb.setUserDefinedProperties(userDefinedProperties);
			userDefinedProperties.put("foo", originalArray[i]);
			orbs.add(orb);
		}

		Collections.sort(orbs, orbComparator);

		// Assert
		assertEquals(".13240", orbs.get(0).getUserDefinedProperties().get("foo"));
		assertEquals("1", orbs.get(1).getUserDefinedProperties().get("foo"));
		assertEquals("2414", orbs.get(2).getUserDefinedProperties().get("foo"));
		assertEquals("00123213", orbs.get(3).getUserDefinedProperties().get("foo"));
		assertEquals("90099900", orbs.get(4).getUserDefinedProperties().get("foo"));
	}
}
