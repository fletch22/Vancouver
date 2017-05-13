package com.fletch22.orb.query;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.serialization.GsonSerializable;

public class RichOrbResult implements GsonSerializable {
	
	private static final Logger logger = LoggerFactory.getLogger(RichOrbResult.class);
	
	public long orbTypeInternalId;
	public List<Orb> orbList;
	public ArrayList<String> fields = new ArrayList<>();
	
	public RichOrbResult(List<Orb> orbList, OrbType orbParent) {
		this.orbTypeInternalId = orbParent.id;
		LinkedHashSet<String> customFields = orbParent.customFields;
		logger.info("Size of customFields: {}", orbParent.customFields.size());
		for (String custField : customFields) {
			logger.info("Adding field {}", custField);
			this.fields.add(custField);
		}
	}
}
