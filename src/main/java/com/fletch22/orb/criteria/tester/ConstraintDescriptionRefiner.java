package com.fletch22.orb.criteria.tester;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConstraintDescriptionRefiner {
	
	Logger logger = LoggerFactory.getLogger(ConstraintDescriptionRefiner.class);

	public StringBuffer refine(StringBuffer description) {
		
		String token = "and ";
		
		String comparison = description.toString().trim();
		
		if (comparison.toLowerCase().indexOf(token) == 0) {
			comparison = description.delete(0, token.length()).toString().trim();
			description = new StringBuffer(comparison);
			String firstLetter = description.substring(0, 1);
			description.replace(0, 1, firstLetter.toUpperCase());
		}
		
		return description;
		
	}
}
