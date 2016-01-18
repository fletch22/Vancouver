package com.fletch22.orb.cache.local;

import java.math.BigDecimal;
import java.util.LinkedHashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.aop.Log4EventAspect;
import com.fletch22.aop.Loggable4Event;
import com.fletch22.orb.InternalIdGenerator;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeConstants;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.TranDateGenerator;
import com.fletch22.orb.command.orbType.AddWholeOrbTypeCommand;
import com.fletch22.orb.command.orbType.DeleteOrbTypeCommand;
import com.fletch22.orb.command.orbType.dto.AddOrbTypeDto;
import com.fletch22.orb.limitation.DefLimitationManagerImpl;
import com.fletch22.orb.limitation.LimitationManagerImpl;
import com.fletch22.orb.query.QueryManager;
import com.fletch22.orb.rollback.UndoActionBundle;
import com.fletch22.orb.systemType.OrbTypeInitializer;
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

	@Autowired
	QueryManager queryManager;

	@Autowired
	OrbTypeInitializer orbTypeInitializer;

	@Autowired
	LimitationManagerImpl limitationManager;

	@Autowired
	DefLimitationManagerImpl defLimitationManager;

	@Override
	public void initializeOrbTypes() {
		orbTypeInitializer.init();
	}

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
		logger.info("Trying to create object with label {}", label);
		BigDecimal tranDate = tranDateGenerator.getTranDate();

		customFields = (customFields == null) ? new LinkedHashSet<String>() : customFields;

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
		deleteOrbType(orbTypeInternalId, true);

		return orbTypeInternalId;
	}

	private void ensureNotASystemOrbType(long orbTypeInternalId) {
		if (orbTypeInternalId < InternalIdGenerator.START_ID) {
			String message = String.format("Encountered a problem trying to delete orb type '%s'. Because this id is a system orb type id this type cannot be deleted.", orbTypeInternalId);
			throw new RuntimeException(message);
		}
	}

	@Override
	@Loggable4Event
	public void deleteOrbType(long orbTypeInternalId, boolean isDeleteDependencies) {

		ensureNotASystemOrbType(orbTypeInternalId);

		handleOrbTypeDeletionDependencies(orbTypeInternalId, isDeleteDependencies);

		OrbType orbType = cache.orbTypeCollection.remove(orbTypeInternalId);

		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		createOrbType(orbType.label, orbType.id, orbType.tranDate, new LinkedHashSetString(orbType.customFields));
	}

	private void handleOrbTypeDeletionDependencies(long orbTypeInternalId, boolean isDeleteDependencies) {
		if (isDeleteDependencies) {
			orbManager.deleteOrbsWithType(orbTypeInternalId, isDeleteDependencies);
		} else {
			boolean doesExist = orbManager.doesOrbWithTypeExist(orbTypeInternalId);
			if (doesExist) {
				String message = String
						.format("Encountered problem trying to delete orb type '%s'. The orb type has orb instances. These instances are (of course) dependent on the type '%s'. Pass 'true' to delete these depenendencies along with the type.",
								orbTypeInternalId, orbTypeInternalId);
				throw new RuntimeException(message);
			}
		}
		queryManager.handleTypeDeleteEvent(orbTypeInternalId, isDeleteDependencies);
		defLimitationManager.handleTypeDeleteEvent(orbTypeInternalId, isDeleteDependencies);
		limitationManager.handleTypeDeleteEvent(orbTypeInternalId, isDeleteDependencies);
	}

	@Override
	public void nukeAndPave() {
		cache.orbTypeCollection.deleteAll();
	}

	@Override
	@Loggable4Event
	public void addAttribute(long orbTypeInternalId, String attributeName) {

		ensureNotASystemOrbType(orbTypeInternalId);

		// NOTE: Add it to the type.
		OrbType orbType = cache.orbTypeCollection.get(orbTypeInternalId);
		orbType.addField(attributeName);

		// NOTE: Add it to the instances.
		cache.orbCollection.addAttribute(orbTypeInternalId, attributeName);

		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		deleteAttribute(orbTypeInternalId, attributeName, true);
	}

	@Override
	@Loggable4Event
	public void deleteAttribute(long orbTypeInternalId, String attributeName, boolean isDeleteDependencies) {

		ensureNotASystemOrbType(orbTypeInternalId);

		handleAttributeDeletionDependencies(orbTypeInternalId, attributeName, isDeleteDependencies);

		int attributeIndex = getIndexOfAttribute(orbTypeInternalId, attributeName);
		orbManager.deleteOrbAttributeFromAllInstances(orbTypeInternalId, attributeName, attributeIndex);

		OrbType orbType = cache.orbTypeCollection.get(orbTypeInternalId);
		orbType.customFields.remove(attributeName);

		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		addAttribute(orbTypeInternalId, attributeName);
	}

	private void handleAttributeDeletionDependencies(long orbTypeInternalId, String attributeName, boolean isDeleteDependencies) {
		queryManager.handleAttributeDeleteEvent(orbTypeInternalId, attributeName, isDeleteDependencies);
		defLimitationManager.handleAttributeDeleteEvent(orbTypeInternalId, attributeName, isDeleteDependencies);
		limitationManager.handleAttributeDeleteEvent(orbTypeInternalId, attributeName, isDeleteDependencies);
	}

	@Override
	public int getIndexOfAttribute(long orbTypeInternalId, String attributeName) {
		OrbType orbType = cache.orbTypeCollection.get(orbTypeInternalId);
		return getIndexOfAttribute(orbType, attributeName);
	}

	@Override
	public int getIndexOfAttribute(OrbType orbType, String attributeName) {
		if (!orbType.customFields.contains(attributeName)) {
			throw new RuntimeException("Attribute '" + attributeName + "' not found on type " + orbType.id + ".");
		}

		int index = 0;
		for (String key : orbType.customFields) {
			if (key.equals(attributeName)) {
				break;
			}
			index++;
		}

		return index;
	}

	@Override
	public OrbType getOrbType(long orbTypeInternalId) {
		OrbType orbType = cache.orbTypeCollection.get(orbTypeInternalId);
		if (orbType == null) {
			throw new RuntimeException(String.format("Encountered problem finding orb type with id '%s'.", orbTypeInternalId));
		}
		return orbType;
	}

	@Override
	public OrbType getOrbType(String label) {
		OrbType orbType = cache.orbTypeCollection.getFromLabel(label);
		if (orbType == null) {
			throw new RuntimeException(String.format("Encountered problem finding orb type label '%s'.", label));
		}
		return orbType;
	}

	@Override
	public long getOrbTypeCount() {
		return cache.orbTypeCollection.getCount();
	}

	@Override
	@Loggable4Event
	public void renameAttribute(long orbTypeInternalId, String attributeNameOld, String attributeNameNew) {

		ensureNotASystemOrbType(orbTypeInternalId);

		handleAttributeDeletionDependencies(orbTypeInternalId, attributeNameOld, attributeNameNew);

		orbManager.renameAttribute(orbTypeInternalId, attributeNameOld, attributeNameNew);

		cache.orbTypeCollection.renameAttribute(orbTypeInternalId, attributeNameOld, attributeNameNew);

		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		renameAttribute(orbTypeInternalId, attributeNameNew, attributeNameOld);
	}

	private void handleAttributeDeletionDependencies(long orbTypeInternalId, String attributeNameOld, String attributeNameNew) {
		queryManager.handleAttributeRenameEvent(orbTypeInternalId, attributeNameOld, attributeNameNew);
		defLimitationManager.handleAttributeRenameEvent(orbTypeInternalId, attributeNameOld, attributeNameNew);
		limitationManager.handleAttributeRenameEvent(orbTypeInternalId, attributeNameOld, attributeNameNew);
	}

	@Override
	public boolean doesOrbTypeExist(long orbTypeInternalId) {
		return cache.orbTypeCollection.get(orbTypeInternalId) != null;
	}
}
