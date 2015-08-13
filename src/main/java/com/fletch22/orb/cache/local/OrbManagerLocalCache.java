package com.fletch22.orb.cache.local;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.aop.Log4EventAspect;
import com.fletch22.aop.Loggable4Event;
import com.fletch22.orb.InternalIdGenerator;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.cache.local.OrbTypeCollection.OrbType;
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
	
	@Override
	@Loggable4Event
	public void createOrb(Orb orb) {
		
		OrbType orbType = orbTypeManager.getOrbType(orb.getOrbTypeInternalId());
		
		cache.orbCollection.add(orbType, orb);
		
		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		deleteOrb(orb.getOrbInternalId());
	}
	
	@Override
	@Loggable4Event
	public Orb createOrb(long orbTypeInternalId, BigDecimal tranDate) {
		long orbInternalId = this.internalIdGenerator.getNewId();
		
		Orb orb = cache.orbCollection.add(orbInternalId, orbTypeInternalId, tranDate);
		
		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		deleteOrb(orbInternalId);
		
		return orb;
	}
	
	@Override
	public Orb createOrb(AddOrbDto addOrbDto, BigDecimal tranDate, UndoActionBundle undoActionBundle) {
		
		long orbInternalId = this.internalIdGenerator.getNewId();
		
		Orb orb = cache.orbCollection.add(orbInternalId, addOrbDto.orbTypeInternalId, tranDate);
		
		// Add delete to rollback action
		undoActionBundle.addUndoAction(this.deleteOrbCommand.toJson(orbInternalId, false), tranDate);
		
		return orb;
	}
	
	@Override
	@Loggable4Event
	public void deleteOrb(long orbInternalId) {
		
		Orb orb = cache.orbCollection.delete(orbInternalId);
		
		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		createOrb(orb);
	}
	
	@Override
	@Loggable4Event
	public Orb setAttribute(long orbInternalId, String attributeName, String value) {
		Orb orb = cache.orbCollection.get(orbInternalId);
		
		if (!orb.getUserDefinedProperties().containsKey(attributeName)) {
			throw new RuntimeException("Orb '" + orbInternalId + "' does not contain attribute '" + attributeName + "'.");
		}
		
		String oldValue = orb.getUserDefinedProperties().get(attributeName);
		
		orb.getUserDefinedProperties().put(attributeName, value);
		
		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		setAttribute(orbInternalId, attributeName, oldValue);
		
		return orb;
	}
	
	@Override
	public Orb getOrb(long orbInternalId) {
		return cache.orbCollection.get(orbInternalId);
	}

	@Override
	public void deleteAllOrbs() {
		cache.orbCollection.deleteAll();
	}
}
