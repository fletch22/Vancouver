package com.fletch22.orb.query.sort;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.fletch22.orb.query.ResultSet.Row;

public class RowComparatorTest {

	@Test
	public void testSortSuccess() {

		// Arrange
		List<GrinderSortInfo> grinderSortInfoArray = new ArrayList<GrinderSortInfo>();
		
		GrinderSortInfo grinderSortInfo = new GrinderSortInfo();
		grinderSortInfo.sortIndex = 0;
		grinderSortInfoArray.add(grinderSortInfo);
		
		RowComparator rowComparator = new RowComparator(grinderSortInfoArray);
		
		List<Row> rows = new ArrayList<Row>();
		
		String[] originalArray = new String[4];
		originalArray[0] = "d";
		originalArray[1] = "c";
		originalArray[2] = "b";
		originalArray[3] = "a";
		
		for (int i = 0; i < originalArray.length; i++) {
			Row row = new Row();
			row.cells = new String[1];
			row.cells[0] = originalArray[i];
			rows.add(row);
		}
		
		Collections.sort(rows, rowComparator);
		
		// Assert
		assertEquals("a", rows.get(0).cells[0]);
		assertEquals("b", rows.get(1).cells[0]);
		assertEquals("c", rows.get(2).cells[0]);
		assertEquals("d", rows.get(3).cells[0]);
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
		
		RowComparator rowComparator = new RowComparator(grinderSortInfoArray);
		
		List<Row> rows = new ArrayList<Row>();
		
		String[] originalArray = new String[4];
		originalArray[0] = "d";
		originalArray[1] = "c";
		originalArray[2] = "b";
		originalArray[3] = "a";
		
		for (int i = 0; i < originalArray.length; i++) {
			Row row = new Row();
			row.cells = new String[3];
			row.cells[0] = "a";
			row.cells[1] = "asdf";
			row.cells[2] = originalArray[i];
			rows.add(row);
		}
		
		Collections.sort(rows, rowComparator);
		
		// Assert
		assertEquals("a", rows.get(0).cells[2]);
		assertEquals("b", rows.get(1).cells[2]);
		assertEquals("c", rows.get(2).cells[2]);
		assertEquals("d", rows.get(3).cells[2]);
	}
	
	@Test
	public void testSortDescending() {

		// Arrange
		List<GrinderSortInfo> grinderSortInfoArray = new ArrayList<GrinderSortInfo>();
		
		GrinderSortInfo grinderSortInfo = new GrinderSortInfo();
		grinderSortInfo.sortIndex = 0;
		grinderSortInfo.sortDirection = SortInfo.SortDirection.DESC;
		grinderSortInfoArray.add(grinderSortInfo);
		
		RowComparator rowComparator = new RowComparator(grinderSortInfoArray);
		
		List<Row> rows = new ArrayList<Row>();
		
		String[] originalArray = new String[5];
		originalArray[0] = "a";
		originalArray[1] = "d";
		originalArray[2] = "c";
		originalArray[3] = "b";
		originalArray[4] = "z";
		
		for (int i = 0; i < originalArray.length; i++) {
			Row row = new Row();
			row.cells = new String[1];
			row.cells[0] = originalArray[i];
			rows.add(row);
		}
		
		Collections.sort(rows, rowComparator);
		
		// Assert
		assertEquals("z", rows.get(0).cells[0]);
		assertEquals("d", rows.get(1).cells[0]);
		assertEquals("c", rows.get(2).cells[0]);
		assertEquals("b", rows.get(3).cells[0]);
		assertEquals("a", rows.get(4).cells[0]);
	}
	
	@Test
	public void testSortNumeric() {

		// Arrange
		List<GrinderSortInfo> grinderSortInfoArray = new ArrayList<GrinderSortInfo>();
		
		GrinderSortInfo grinderSortInfo = new GrinderSortInfo();
		grinderSortInfo.sortIndex = 0;
		grinderSortInfo.sortType = SortInfo.SortType.NUMERIC;
		grinderSortInfoArray.add(grinderSortInfo);
		
		RowComparator rowComparator = new RowComparator(grinderSortInfoArray);
		
		List<Row> rows = new ArrayList<Row>();
		
		String[] originalArray = new String[5];
		originalArray[0] = "1";
		originalArray[1] = ".13240";
		originalArray[2] = "2414";
		originalArray[3] = "00123213";
		originalArray[4] = "90099900";
		
		for (int i = 0; i < originalArray.length; i++) {
			Row row = new Row();
			row.cells = new String[1];
			row.cells[0] = originalArray[i];
			rows.add(row);
		}
		
		Collections.sort(rows, rowComparator);
		
		// Assert
		assertEquals(".13240", rows.get(0).cells[0]);
		assertEquals("1", rows.get(1).cells[0]);
		assertEquals("2414", rows.get(2).cells[0]);
		assertEquals("00123213", rows.get(3).cells[0]);
		assertEquals("90099900", rows.get(4).cells[0]);
	}
}
