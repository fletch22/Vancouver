package com.fletch22.orb.cache.external;

import java.math.BigDecimal;
import java.util.LinkedHashSet;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;

import com.fletch22.orb.CommandExpressor;
import com.fletch22.orb.InternalIdGenerator;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeConstants;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.cache.local.LinkedHashSetString;
import com.fletch22.orb.command.orbType.AddOrbTypeCommand;
import com.fletch22.orb.command.orbType.AddWholeOrbTypeCommand;
import com.fletch22.orb.command.orbType.DeleteOrbTypeCommand;
import com.fletch22.orb.command.orbType.DeleteOrbTypeDto;
import com.fletch22.orb.command.orbType.dto.AddOrbTypeDto;
import com.fletch22.orb.rollback.UndoActionBundle;
import com.fletch22.redis.ObjectTypeCacheService;
import com.fletch22.redis.RedisObjectInstanceCacheService;
import com.fletch22.util.JsonUtil;

//@Component(value = "OrbTypeManagerExternalCache")
public class OrbTypeManagerExternalCache implements OrbTypeManager {
	
	@Autowired
	RedisObjectInstanceCacheService objectInstanceCacheService;
	
	@Autowired
	ObjectTypeCacheService objectTypeCacheService;
	
	@Autowired
	CommandExpressor commandExpressor;
	
	@Autowired
	AddOrbTypeCommand addOrbTypeCommand;
	
	@Autowired
	DeleteOrbTypeCommand deleteOrbTypeCommand;
	
	@Autowired
	NakedToClothedOrbTransformer nakedToClothedOrbTransformer;
	
	@Autowired
	AddWholeOrbTypeCommand addWholeOrbTypeCommand;
	
	@Autowired
	JsonUtil jsonUtil;
	
	@Autowired
	private InternalIdGenerator internalIdGenerator;
	
	public long createOrbType(AddOrbTypeDto addOrbTypeDto, BigDecimal tranDate, final UndoActionBundle undoActionBundle) {
		long orbInternalTypeId;
		
		boolean exists = this.objectTypeCacheService.doesObjectTypeExist(addOrbTypeDto.label);
		if (exists) {
			throw new RuntimeException("Encountered problem trying to create orb type. Appears orb type '" + addOrbTypeDto.label + "' already exists.");
		} else {
			orbInternalTypeId = this.internalIdGenerator.getNewId();
			NakedOrb nakedOrb = new NakedOrb(orbInternalTypeId, OrbTypeConstants.ORBTYPE_BASETYPE_ID, addOrbTypeDto.label, tranDate);
			
			objectTypeCacheService.createType(nakedOrb);
			
			// Add delete to rollback action
			undoActionBundle.addUndoAction(this.deleteOrbTypeCommand.toJson(orbInternalTypeId, false), tranDate);
		}
		return orbInternalTypeId;
	}

	public void deleteOrbType(DeleteOrbTypeDto deleteOrbTypeDto, BigDecimal tranDate, UndoActionBundle rollbackAction) {
		
		NakedOrb nakedOrb = objectTypeCacheService.deleteType(String.valueOf(deleteOrbTypeDto.orbTypeInternalId));
		
		// Add delete to rollback action
		Orb orb = nakedToClothedOrbTransformer.convertNakedToClothed(nakedOrb);
		rollbackAction.addUndoAction(this.addWholeOrbTypeCommand.toJson(orb), tranDate);
	}

	@Override
	public void deleteAllTypes() {
		throw new NotImplementedException("deleteAllTypes");
	}

	@Override
	public void addAttribute(long orbInternalId, String attributeName) {
		throw new NotImplementedException("Deprecated implementation.");
	}

	@Override
	public void deleteAttribute(long orbTypeInternalId, String attributeName) {
		throw new NotImplementedException("Deprecated implementation.");		
	}

	@Override
	public OrbType getOrbType(long orbTypeInternalId) {
		throw new NotImplementedException("Deprecated implementation.");
	}

	@Override
	public long getOrbTypeCount() {
		throw new NotImplementedException("Deprecated implementation.");
	}

	@Override
	public long createOrbType(String label, long orbTypeInternalId, BigDecimal tranDate, LinkedHashSetString customFields) {
		throw new NotImplementedException("Deprecated implementation.");
	}

	@Override
	public long createOrbType(String label, LinkedHashSet<String> customFields) {
		throw new NotImplementedException("Deprecated implementation.");
	}

	@Override
	public void deleteOrbType(long orbTypeInternalId) {
		throw new NotImplementedException("Deprecated implementation.");
	}

	@Override
	public int getIndexOfAttribute(long orbTypeInternalId, String attributeName) {
		throw new NotImplementedException("getIndexOfAttribute");
	}

	@Override
	public int getIndexOfAttribute(OrbType orbType, String attributeName) {
		throw new NotImplementedException("getIndexOfAttribute");
	}
}
