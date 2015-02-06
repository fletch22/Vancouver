package com.fletch22.orb;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.command.orbType.dto.AddOrbTypeDto;
import com.fletch22.orb.rollback.RollbackAction;
import com.fletch22.redis.ObjectInstanceCacheService;
import com.fletch22.redis.ObjectTypeCacheService;
import com.fletch22.util.OrbUtil;

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
	OrbUtil orbUtil;
	
	@Autowired
	ObjectTypeCacheService objectTypeCacheService;
	
	@Autowired
	CommandExpressor commandExpressor;
	
	@Autowired
	private InternalIdGenerator internalIdGenerator;
	
	public long createOrbType(AddOrbTypeDto addOrbTypeDto, BigDecimal tranDate, final RollbackAction rollbackAction) {
		long orbInternalTypeId;
		
		boolean exists = objectTypeCacheService.doesObjectTypeExist(addOrbTypeDto.label);
		if (exists) {
			throw new RuntimeException("Encountered problem trying to create orb type. Appears orb type '" + addOrbTypeDto.label + "' already exists.");
		} else {
			orbInternalTypeId = this.internalIdGenerator.getNewId();
			HashMap<String, String> orbPropertyMap = this.orbUtil.createCoreProperties(orbInternalTypeId, addOrbTypeDto.label, tranDate);
			
			objectTypeCacheService.createType(addOrbTypeDto.label, orbPropertyMap);
			
			// add delete to rollback action
			rollbackAction.addAction(this.commandExpressor.getJsonCommandRemoveOrbType(orbInternalTypeId, false));
		}
		return orbInternalTypeId;
	}
}
