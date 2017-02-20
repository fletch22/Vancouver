package com.fletch22.dao;

import org.springframework.stereotype.Component;

@Component
public class BackupFileCoder {

	public static final String REPLACEMENT_AMPERSAND = "&amp;";
	public static final String REPLACEMENT_LINE_FEED = "&lineFeed;";
	public static final String REPLACEMENT_NEW_LINE = "&newLine;";
	
	public StringBuilder encode(StringBuilder sb) {
		sb = replaceAll(sb, "&", REPLACEMENT_AMPERSAND);
		sb = replaceAll(sb, "\r", REPLACEMENT_LINE_FEED);
		sb = replaceAll(sb, "\n", REPLACEMENT_NEW_LINE);
		return sb;
	}
	
	public StringBuilder decode(StringBuilder sb) {
		sb = replaceAll(sb, REPLACEMENT_NEW_LINE, "\n");
		sb = replaceAll(sb, REPLACEMENT_LINE_FEED, "\r");
		sb = replaceAll(sb, REPLACEMENT_AMPERSAND, "&");
		return sb;
	}
	
	public StringBuilder replaceAll(StringBuilder builder, String from, String to)
	{
	    int index = builder.indexOf(from);
	    while (index != -1)
	    {
	        builder.replace(index, index + from.length(), to);
	        index += to.length();
	        index = builder.indexOf(from, index);
	    }
	    return builder;
	}
}
