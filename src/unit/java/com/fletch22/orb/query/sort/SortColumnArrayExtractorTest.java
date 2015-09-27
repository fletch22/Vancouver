package com.fletch22.orb.query.sort;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.fletch22.orb.query.ResultSet.Header;

public class SortColumnArrayExtractorTest {

	@Test
	public void test() {

		// Arrange
		Header header = new Header();
		header.columnNames = new String[3];
		header.columnNames[0] = "foo";
		header.columnNames[1] = "bar";
		header.columnNames[2] = "stuff";
		
		List<String> attributesToSortOn = new ArrayList<String>();
		attributesToSortOn.add("foo");
		attributesToSortOn.add("stuff");
		
		SortColumnArrayExtractor sortColumnArrayExtractor = new SortColumnArrayExtractor();
		
		// Act
		int[] indexesToSortOn = sortColumnArrayExtractor.extract(header, attributesToSortOn);
		
		// Assert
		assertTrue(indexesToSortOn.length > 0);
		assertEquals(0, indexesToSortOn[0]);
		assertEquals(2, indexesToSortOn[1]);
	}
}
