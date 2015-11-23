package com.fletch22.orb.query.constraint;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class ConstraintKitchen {

	public Map<String, Class<?>> previouslyBespokeAttributeClassMap = new HashMap<String, Class<?>>();
	
}
