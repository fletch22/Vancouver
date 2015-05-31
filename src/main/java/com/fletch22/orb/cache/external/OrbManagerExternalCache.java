package com.fletch22.orb.cache.external;

import java.math.BigDecimal;

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
@Qualifier(value = OrbManagerExternalCache.COMPONENT_QUALIFIER_ID)
public class OrbManagerExternalCache implements OrbManager {
	
	public static final String COMPONENT_QUALIFIER_ID = "OrbManagerExternalCache";
	
	@Autowired
	InternalIdGenerator internalIdGenerator;
	
	@Autowired
	ObjectInstanceExternalCacheService objectInstanceCacheService;
	
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
