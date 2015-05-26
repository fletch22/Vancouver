package com.fletch22.orb;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.command.orb.DeleteOrbCommand;
import com.fletch22.orb.command.orbType.dto.AddOrbDto;
import com.fletch22.orb.rollback.UndoActionBundle;
import com.fletch22.redis.ObjectInstanceCacheService;

@Component
public class OrbManager {
	
	@Autowired
	InternalIdGenerator internalIdGenerator;
	
	@Autowired
	ObjectInstanceCacheService objectInstanceCacheService;
	
	@Autowired
	NakedToClothedOrbTransformer nakedToClothedOrbTransformer;
	
	@Autowired
	DeleteOrbCommand deleteOrbCommand;

	public Orb createOrbInstance(AddOrbDto addOrbDto, BigDecimal tranDate, UndoActionBundle undoActionBundle) {
		long orbInternalId = this.internalIdGenerator.getNewId();
		
		NakedOrb nakedOrb = new NakedOrb(orbInternalId, addOrbDto.orbTypeInternalId, tranDate);
		
		this.objectInstanceCacheService.createInstance(nakedOrb);
		
		// Add delete to rollback action
		undoActionBundle.addUndoAction(this.deleteOrbCommand.toJson(orbInternalId, false), tranDate);
		
		return nakedToClothedOrbTransformer.convertNakedToClothed(nakedOrb);
	}
}
