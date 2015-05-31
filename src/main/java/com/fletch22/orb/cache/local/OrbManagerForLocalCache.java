package com.fletch22.orb.cache.local;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fletch22.orb.InternalIdGenerator;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.command.orb.DeleteOrbCommand;
import com.fletch22.orb.command.orbType.dto.AddOrbDto;
import com.fletch22.orb.rollback.UndoActionBundle;

@Component
@Qualifier(value = "")
public class OrbManagerForLocalCache implements OrbManager {
	
	@Autowired
	InternalIdGenerator internalIdGenerator;
	
	@Autowired
	OrbInstancesLocalCache orbInstancesLocalCache;
	
	@Autowired
	DeleteOrbCommand deleteOrbCommand;
	
	public Orb createOrbInstance(AddOrbDto addOrbDto, BigDecimal tranDate, UndoActionBundle undoActionBundle) {

		long orbInternalId = this.internalIdGenerator.getNewId();
		
		orbInstancesLocalCache.add(orbInternalId, addOrbDto.orbTypeInternalId, tranDate);
		
		// Add delete to rollback action
		undoActionBundle.addUndoAction(this.deleteOrbCommand.toJson(orbInternalId, false), tranDate);
		
		return new Orb(orbInternalId, addOrbDto.orbTypeInternalId, tranDate, new HashMap<String, String>());
		
	}
}
