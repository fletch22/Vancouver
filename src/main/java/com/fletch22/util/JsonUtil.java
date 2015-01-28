package com.fletch22.util;

import org.springframework.stereotype.Component;

@Component
public class JsonUtil {

	public String escapeJsonIllegals(String value) {
		if (value != null) {
			value = value.replace("&", "&amp;");
			value = value.replace("{", "&leftCurly;");
			value = value.replace("}", "&rightCurly;");
			value = value.replace("[", "&leftBracket;");
			value = value.replace("]", "&rightBracket;");
			value = value.replace("\n", "&n;");
			value = value.replace("\r", "&r;");
			value = value.replace("\\", "\\\\");
			value = value.replace("'", "&apos;");
			value = value.replace("\"", "&doubleQuote;");
		} else {
			value = "&null";
		}
		return value;
	}
	
	public String unescapeJsonIllegals(String value) 
	{
        if (value != null)
        {
            if (value == "&null")
            {
                return null;
            }
            value = value.replace("&doubleQuote;", "\"");
            value = value.replace("&apos;", "'");
            value = value.replace("\\\\", "\\");
            value = value.replace("&r;", "\r");
            value = value.replace("&n;", "\n");
            value = value.replace("&rightBracket;", "]");
            value = value.replace("&leftBracket;", "[");
            value = value.replace("&rightCurly;", "}");
            value = value.replace("&leftCurly;", "{");
            value = value.replace("&amp;", "&");
        }
        return value;
	}
}
