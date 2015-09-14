package com.fletch22.orb.cache.local;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

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
import com.fletch22.orb.OrbTypeConstants;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.TranDateGenerator;
import com.fletch22.orb.command.orbType.AddWholeOrbTypeCommand;
import com.fletch22.orb.command.orbType.DeleteOrbTypeCommand;
import com.fletch22.orb.command.orbType.DeleteOrbTypeDto;
import com.fletch22.orb.command.orbType.dto.AddOrbTypeDto;
import com.fletch22.orb.rollback.UndoActionBundle;
import com.fletch22.util.json.LinkedHashSetString;

@Component(value = "OrbTypeManagerLocalCache")
public class OrbTypeManagerLocalCache implements OrbTypeManager {
	
	Logger logger = LoggerFactory.getLogger(OrbTypeManagerLocalCache.class);

	@Autowired
	Cache cache;
	
	@Autowired
	InternalIdGenerator internalIdGenerator;
	
	@Autowired
	TranDateGenerator tranDateGenerator;	
	
	@Autowired
	DeleteOrbTypeCommand deleteOrbTypeCommand;
	
	@Autowired
	AddWholeOrbTypeCommand addWholeOrbTypeCommand;
	
	@Autowired
	OrbManager orbManager;
	
	@Override
	public long createOrbType(AddOrbTypeDto addOrbTypeDto, BigDecimal tranDate, UndoActionBundle undoActionBundle) {

		long orbInternalTypeId = addOrbTypeDto.orbTypeInternalId;
		if (orbInternalTypeId == OrbTypeConstants.ORBTYPE_INTERNAL_ID_UNSET) {
			orbInternalTypeId = this.internalIdGenerator.getNewId();
		}
		
		OrbType orbType = new OrbType(orbInternalTypeId, addOrbTypeDto.label, tranDate, new LinkedHashSet<>());
		cache.orbTypeCollection.add(orbType);
		
		// Add delete to rollback action
		undoActionBundle.addUndoAction(this.deleteOrbTypeCommand.toJson(orbInternalTypeId, false), tranDate);
		
		return orbInternalTypeId;
	}
	
	@Override
	public long createOrbType(String label, LinkedHashSet<String> customFields) {
		BigDecimal tranDate = tranDateGenerator.getTranDate();
		
		return createOrbType(label, OrbTypeConstants.ORBTYPE_INTERNAL_ID_UNSET, tranDate, new LinkedHashSetString(customFields));
	}
	
	@Override
	@Loggable4Event
	public long createOrbType(String label, long orbTypeInternalId, BigDecimal tranDate, LinkedHashSetString customFields) {
		if (orbTypeInternalId == OrbTypeConstants.ORBTYPE_INTERNAL_ID_UNSET) {
			orbTypeInternalId = this.internalIdGenerator.getNewId();
		}
		
		OrbType orbType = new OrbType(orbTypeInternalId, label, tranDate, customFields.linkedHashSet);
		cache.orbTypeCollection.add(orbType);
		
		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		deleteOrbType(orbTypeInternalId);
		
		return orbTypeInternalId;
	}

	@Override
	public void deleteOrbType(DeleteOrbTypeDto deleteOrbTypeDto, BigDecimal tranDate, UndoActionBundle rollbackAction) {
		OrbType orbType = cache.orbTypeCollection.remove(deleteOrbTypeDto.orbTypeInternalId);
		
		// TODO: Transform to orb until we get rid of redis stuff. Then we can use OrbType natively.
		Orb orb = convertToOrb(tranDate, orbType); 
		
		rollbackAction.addUndoAction(this.addWholeOrbTypeCommand.toJson(orb), tranDate);
	}
	
	@Override
	@Loggable4Event
	public void deleteOrbType(long orbTypeInternalId) {
		
		orbManager.deleteOrbsWithType(orbTypeInternalId);
		
		OrbType orbType = cache.orbTypeCollection.remove(orbTypeInternalId);
		
		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		createOrbType(orbType.label, orbType.id, orbType.tranDate, new LinkedHashSetString(orbType.customFields));
	}
	
	private Orb convertToOrb(BigDecimal tranDate, OrbType orbType) {
		LinkedHashMap<String, String> properties = new LinkedHashMap<String, String>();
		for (String customFieldNames : orbType.customFields) {
			properties.put(customFieldNames, customFieldNames);
		}
		return new Orb(orbType.id, OrbTypeConstants.ORBTYPE_BASETYPE_ID, tranDate, properties);
	}

	@Override
	public void deleteAllTypes() {
		cache.orbTypeCollection.deleteAll();
	}

	@Override
	@Loggable4Event
	public void addAttribute(long orbTypeInternalId, String attributeName) {
		// NOTE: Add it to the type.
		OrbType orbType = cache.orbTypeCollection.get(orbTypeInternalId);
		orbType.addField(attributeName);
		
		// NOTE: Add it to the instances.
		cache.orbCollection.addAttribute(orbTypeInternalId, attributeName);
		
		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		deleteAttribute(orbTypeInternalId, attributeName);
	}

	@Override
	@Loggable4Event
	public void deleteAttribute(long orbTypeInternalId, String attributeName) {
		
		// NOTE: Delete it from the instance.\
		int attributeIndex = getIndexOfAttribute(orbTypeInternalId, attributeName);
		orbManager.deleteOrbAttributeFromAllInstances(orbTypeInternalId, attributeName, attributeIndex);
		
		// NOTE: Delete it from the type.
		OrbType orbType = cache.orbTypeCollection.get(orbTypeInternalId);
		orbType.customFields.remove(attributeName);
		
		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		addAttribute(orbTypeInternalId, attributeName);
	}
	
	@Override
	public int getIndexOfAttribute(long orbTypeInternalId, String attributeName) {
		OrbType orbType = cache.orbTypeCollection.get(orbTypeInternalId);
		return  getIndexOfAttribute(orbType, attributeName);
	}
	
	@Override
	public int getIndexOfAttribute(OrbType orbType, String attributeName) {
		if (!orbType.customFields.contains(attributeName)) {
			throw new RuntimeException("Attribute '" + attributeName + "' not found on type " + orbType.id + ".");
		}
		
		int index = 0;
		for (String key: orbType.customFields) {
			if (key.equals(attributeName)) {
				break;
			}
			index++;
		}
		
		return index;
	}

	@Override
	public OrbType getOrbType(long orbTypeInternalId) {
		return cache.orbTypeCollection.get(orbTypeInternalId);
	}

	@Override
	public long getOrbTypeCount() {
		return cache.orbTypeCollection.getCount();
	}

	@Override
	@Loggable4Event
	public void renameAttribute(long orbTypeInternalId, String attributeNameOld, String attributeNameNew) {
		
		orbManager.renameAttribute(orbTypeInternalId, attributeNameOld, attributeNameNew);
		
		cache.orbTypeCollection.renameAttribute(orbTypeInternalId, attributeNameOld, attributeNameNew);
		
		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		renameAttribute(orbTypeInternalId, attributeNameNew, attributeNameOld);
	}
}
