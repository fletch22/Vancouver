package com.fletch22.orb.systemType;

import java.math.BigDecimal;
import java.util.LinkedHashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.InternalIdGenerator;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.TranDateGenerator;
import com.fletch22.orb.cache.local.Cache;

@Component
public class OrbTypeInitializer {
	
	Logger logger = LoggerFactory.getLogger(OrbTypeInitializer.class);
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	TranDateGenerator tranDateGenerator;
	
	@Autowired
	Cache cache;

	public void init() {
		createQuerySystemType();
	}

	private void createQuerySystemType() {
		SystemType query = SystemType.CRITERIA;
		
		createSystemOrbType(query.getLabel(), query.getId(), query.getAttributeHashSet());
	}

	private OrbType createSystemOrbType(String label, long id, LinkedHashSet<String> fields) {

		if (id > InternalIdGenerator.START_ID) {
			String message = String.format("Encountered a problem trying to create system type with id %s; however no system orb type id should be greater than numeric value %s.", id, InternalIdGenerator.START_ID);
			throw new RuntimeException(message);
		}

		BigDecimal tranDate = this.tranDateGenerator.getTranDate();
		
		OrbType orbType = new OrbType(id, label, tranDate, fields);
		cache.orbTypeCollection.add(orbType);

		return orbType;
	}
}
