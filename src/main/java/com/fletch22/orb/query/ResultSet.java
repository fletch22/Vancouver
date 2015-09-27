package com.fletch22.orb.query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fletch22.orb.Orb;

public class ResultSet {

	Header header;
	List<Row> rows = new ArrayList<Row>();
	
	public ResultSet(Set<String> customFields) {
		
		Header header = new Header();
		header.columnNames = customFields.toArray(new String[customFields.size()]);
	}
	
	public void addRow(Orb orb) {
		Row row = new Row(); 
		Map<String, String> map = orb.getUserDefinedProperties();
		row.cells = map.values().toArray(new String[map.values().size()]);
	}
	
	public static class Row {
		public String[] cells;
	}
	
	public static class Header {
		public String[] columnNames;
	}

	public void addRow(ArrayList<String> attributes) {
		Row row = new Row(); 
		row.cells = attributes.toArray(new String[attributes.size()]);
		rows.add(row);
	}
	
	public long getSize() {
		return rows.size();
	}
}
