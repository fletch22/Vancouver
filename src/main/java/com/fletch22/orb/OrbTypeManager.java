package com.fletch22.orb;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.command.orbType.AddOrbTypeCommand;
import com.fletch22.orb.command.orbType.AddWholeOrbTypeCommand;
import com.fletch22.orb.command.orbType.DeleteOrbTypeCommand;
import com.fletch22.orb.command.orbType.DeleteOrbTypeDto;
import com.fletch22.orb.command.orbType.dto.AddOrbTypeDto;
import com.fletch22.orb.rollback.UndoActionBundle;
import com.fletch22.redis.ObjectInstanceCacheService;
import com.fletch22.redis.ObjectTypeCacheService;
import com.fletch22.util.JsonUtil;

@Component
public class OrbTypeManager {
	
	public static final String ORBTYPE_LABEL = "ORB_TYPE";
    public static final String ORBTYPE_QUERY_RESULT_LABEL = "ORB_TYPE_QUERY_RESULT";
	public static final int ORBTYPE_TYPE_ID_ORDINAL = 0;
	public static final int ORBTYPE_USERLABEL_FIELD_ORDINAL = 1;
	public static final int ORBTYPE_START_FIELD_ORDINAL = 2;
	public static final String ORBTYPE_DEFAULT_LABEL_X = "Orb Base Type";
    public static final int ORBTYPE_INTERNAL_ID_UNSET = -1;
    public static final int ORBTYPE_ATTR_ORDINAL_UNSET = -1;
	public static final int ORBTYPE_BASETYPE_ID = 0;
	
	@Autowired
	ObjectInstanceCacheService objectInstanceCacheService;
	
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
			NakedOrb nakedOrb = new NakedOrb(orbInternalTypeId, OrbTypeManager.ORBTYPE_BASETYPE_ID, addOrbTypeDto.label, tranDate);
			
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
}
