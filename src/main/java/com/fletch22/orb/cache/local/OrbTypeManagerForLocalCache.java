package com.fletch22.orb.cache.local;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.fletch22.orb.InternalIdGenerator;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.cache.external.OrbTypeManagerForExternalCache;
import com.fletch22.orb.cache.local.OrbTypeCollection.OrbType;
import com.fletch22.orb.command.orbType.AddWholeOrbTypeCommand;
import com.fletch22.orb.command.orbType.DeleteOrbTypeCommand;
import com.fletch22.orb.command.orbType.DeleteOrbTypeDto;
import com.fletch22.orb.command.orbType.dto.AddOrbTypeDto;
import com.fletch22.orb.rollback.UndoActionBundle;

@Component
@Qualifier(value = OrbTypeManagerForLocalCache.COMPONENT_QUALIFIER_ID)
public class OrbTypeManagerForLocalCache implements OrbTypeManager {

	public static final String COMPONENT_QUALIFIER_ID = "OrbTypeManagerForLocalCache";

	@Autowired
	OrbTypeCollection orbTypeCollection;

	@Autowired
	InternalIdGenerator internalIdGenerator;
	
	@Autowired
	DeleteOrbTypeCommand deleteOrbTypeCommand;
	
	@Autowired
	AddWholeOrbTypeCommand addWholeOrbTypeCommand;

	@Override
	public long createOrbType(AddOrbTypeDto addOrbTypeDto, BigDecimal tranDate, UndoActionBundle undoActionBundle) {

		long orbInternalTypeId = this.internalIdGenerator.getNewId();

		OrbType orbType = new OrbType(addOrbTypeDto.orbTypeInternalId, addOrbTypeDto.label, tranDate, null);
		orbTypeCollection.add(orbType);

		// Add delete to rollback action
		undoActionBundle.addUndoAction(this.deleteOrbTypeCommand.toJson(orbInternalTypeId, false), tranDate);

		return orbInternalTypeId;
	}

	@Override
	public void deleteOrbType(DeleteOrbTypeDto deleteOrbTypeDto, BigDecimal tranDate, UndoActionBundle rollbackAction) {
		OrbType orbType = orbTypeCollection.remove(deleteOrbTypeDto.orbTypeInternalId);
		
		Map<String, String> properties = new HashMap<String, String>();
		for (String customFieldNames : orbType.customFields) {
			properties.put(customFieldNames, customFieldNames);
		}
		
		// TODO: Transform to orb until we get rid of redis stuff. Then we can use orbType object.
		Orb orb = new Orb(orbType.id, OrbTypeManagerForExternalCache.ORBTYPE_BASETYPE_ID, tranDate, properties); 
		
		rollbackAction.addUndoAction(this.addWholeOrbTypeCommand.toJson(orb), tranDate);
	}

}
