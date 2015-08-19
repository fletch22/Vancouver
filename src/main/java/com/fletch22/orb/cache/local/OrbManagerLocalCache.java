package com.fletch22.orb.cache.local;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.aop.Log4EventAspect;
import com.fletch22.aop.Loggable4Event;
import com.fletch22.orb.InternalIdGenerator;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.command.orb.DeleteOrbCommand;
import com.fletch22.orb.command.orbType.dto.AddOrbDto;
import com.fletch22.orb.rollback.UndoActionBundle;

@Component(value = "OrbManagerLocalCache")
public class OrbManagerLocalCache implements OrbManager {
	
	Logger logger = LoggerFactory.getLogger(OrbManagerLocalCache.class);
	
	@Autowired
	InternalIdGenerator internalIdGenerator;
	
	@Autowired
	Cache cache;
	
	@Autowired
	DeleteOrbCommand deleteOrbCommand;
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	OrbReference orbReference;
	
	@Override
	@Loggable4Event
	public void createOrb(Orb orb) {
		
		if (orb.getOrbInternalId() == Orb.INTERNAL_ID_UNSET) {
			orb.setOrbInternalId(this.internalIdGenerator.getNewId());
		}
		
		OrbType orbType = orbTypeManager.getOrbType(orb.getOrbTypeInternalId());
		
		populateOrbMap(orbType, orb);
		
		cache.orbCollection.add(orbType, orb);
		
		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		deleteOrb(orb.getOrbInternalId());
	}
	
	@Override
	@Loggable4Event
	public Orb createOrb(OrbType orbType, BigDecimal tranDate) {
		
		long orbInternalId = this.internalIdGenerator.getNewId();
		Orb orb = new Orb(orbInternalId, orbType.id, tranDate, new LinkedHashMap<String, String>());
		
		populateOrbMap(orbType, orb);
		
		cache.orbCollection.add(orbType, orb);
		
		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		deleteOrb(orb.getOrbInternalId());
		
		return orb;
	}
	
	@Override
	public Orb createOrb(long orbTypeInternalId, BigDecimal tranDate) {
		
		long orbInternalId = this.internalIdGenerator.getNewId();
		Orb orb = new Orb(orbInternalId, orbTypeInternalId, tranDate, new LinkedHashMap<String, String>());
		
		createOrb(orb);
		
		return orb;
	}
	
	@Override
	public Orb createOrb(AddOrbDto addOrbDto, BigDecimal tranDate, UndoActionBundle undoActionBundle) {
		
		long orbInternalId = this.internalIdGenerator.getNewId();
		Orb orb = new Orb(orbInternalId, addOrbDto.orbTypeInternalId, tranDate, new LinkedHashMap<String, String>());
		OrbType orbType = orbTypeManager.getOrbType(orb.getOrbTypeInternalId());
		
		populateOrbMap(orbType, orb);
		
		cache.orbCollection.add(orbType, orb);
		
		// Add delete to rollback action
		undoActionBundle.addUndoAction(this.deleteOrbCommand.toJson(orbInternalId, false), tranDate);
		
		return orb;
	}
	
	private void populateOrbMap(OrbType orbType, Orb orb) {
		
		LinkedHashMap<String, String> propertyMap = orb.getUserDefinedProperties();
		LinkedHashSet<String> customFields = orbType.customFields;
		for (String field : customFields) {
			if (!propertyMap.containsKey(field)) {
				propertyMap.put(field, null);
			}
		}
	}
	
	@Override
	@Loggable4Event
	public void deleteOrb(long orbInternalId) {
		
		Orb orb = cache.orbCollection.delete(orbInternalId);
		
		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		createOrb(orb);
	}
	
	@Override
	public String getAttribute(long orbInternalId, String attributeName) {

		Orb orb = cache.orbCollection.get(orbInternalId);
		
		return orb.getUserDefinedProperties().get(attributeName);
	}
	
	@Override
	@Loggable4Event
	public Orb setAttribute(long orbInternalId, String attributeName, String value) {
		Orb orb = cache.orbCollection.get(orbInternalId);
		
		String oldValue = orb.getUserDefinedProperties().get(attributeName);
		
		if (!areEqualAttributes(oldValue, value)) {
			if (orbReference.isValueAReference(oldValue)) {
				orbReference.removeReferences(orbInternalId, attributeName, value);
			}
			
			if (orbReference.isValueAReference(value)) {
				orbReference.addReferences(orbInternalId, attributeName, value);
			}
			
			orb.getUserDefinedProperties().put(attributeName, value);
			
			Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
			setAttribute(orbInternalId, attributeName, oldValue);
		}
		
		return orb;
	}
	
	public void addReference(long orbInternalIdArrow, String attributeNameArrow, long orbInternalIdTarget, String attributeNameTarget) {
		
		Orb orb = cache.orbCollection.get(orbInternalIdArrow);
		
		String oldValue = orb.getUserDefinedProperties().get(attributeNameArrow);
		
		if (StringUtils.isEmpty(oldValue) || orbReference.isValueAReference(oldValue)) {
			String newValue = orbReference.addReference(orbInternalIdArrow, attributeNameArrow, oldValue, orbInternalIdTarget, attributeNameTarget);
			
			if (!oldValue.equals(newValue)) {
				orb.getUserDefinedProperties().put(attributeNameArrow, newValue);
				
				Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
				setAttribute(orbInternalIdArrow, attributeNameArrow, oldValue);
			}
		} else {
			throw new RuntimeException(String.valueOf(orbInternalIdArrow) + "'s original value '" + oldValue + "' is not a reference.");
		}
	}
	
	public void removeReference(long orbInternalIdArrow, String attributeNameArrow, long orbInternalIdTarget, String attributeNameTarget) {
		throw new NotImplementedException("RemoveReference not done yet.");
	}
	
	private boolean areEqualAttributes(String value1, String value2) {
		return (value1 == null ? value2 == null : value1.equals(value2));
	}
	
	@Override
	public Orb getOrb(long orbInternalId) {
		Orb orb = cache.orbCollection.get(orbInternalId);
		
		if (orb == null) throw new RuntimeException("Encountered problem getting orb. Couldn't find orb with id '" + orbInternalId + "'.");
		
		return orb;
	}

	@Override
	public void deleteAllOrbs() {
		cache.orbCollection.deleteAll();
	}

	@Override
	public boolean doesOrbExist(long orbInternalId) {
		return cache.orbCollection.doesOrbExist(orbInternalId);
	}

}
