package com.fletch22.orb.cache.local;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.aop.Log4EventAspect;
import com.fletch22.aop.Loggable4Event;
import com.fletch22.orb.InternalIdGenerator;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbCloner;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.TranDateGenerator;
import com.fletch22.orb.cache.reference.ReferenceUtil;
import com.fletch22.orb.command.orb.DeleteOrbCommand;
import com.fletch22.orb.command.orbType.dto.AddOrbDto;
import com.fletch22.orb.criteria.tester.ConstraintChecker;
import com.fletch22.orb.limitation.DefLimitationManager;
import com.fletch22.orb.limitation.LimitationManager;
import com.fletch22.orb.query.Criteria;
import com.fletch22.orb.query.QueryManager;
import com.fletch22.orb.rollback.UndoActionBundle;
import com.fletch22.util.json.MapLongString;

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
	
	@Autowired
	OrbCloner orbCloner;
	
	@Autowired
	TranDateGenerator tranDateGenerator;
	
	@Autowired
	QueryManager queryManager;
	
	@Autowired
	ReferenceUtil referenceUtil;
	
	@Autowired
	DefLimitationManager defLimitationManager;
	
	@Autowired
	LimitationManager limitationManager;
	
	@Autowired
	ConstraintChecker constraintChecker;
	
	@Override
	@Loggable4Event
	public Orb createOrb(Orb orb) {

		if (orb.getOrbInternalId() == Orb.INTERNAL_ID_UNSET) {
			orb.setOrbInternalId(this.internalIdGenerator.getNewId());
		}

		OrbType orbType = orbTypeManager.getOrbType(orb.getOrbTypeInternalId());

		populateOrbMap(orbType, orb);
		
		checkDefaultDataLimitations(orb);
		
		cache.orbCollection.add(orbType, orb);

		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		deleteOrb(orb.getOrbInternalId(), true);
		
		return orb;
	}

	private void checkDefaultDataLimitations(Orb orb) {
		
		List<Criteria> limitations = defLimitationManager.getOrbsTypeCriteria(orb.getOrbTypeInternalId());
		
		for (Criteria criteria : limitations) {
			constraintChecker.checkConstraint(criteria, orb);
		}
	}

	@Override
	public Orb createOrb(long orbTypeInternalId) {

		long orbInternalId = this.internalIdGenerator.getNewId();
		Orb orb = new Orb(orbInternalId, orbTypeInternalId, new LinkedHashMap<String, String>());

		return createOrb(orb);
	}

	@Override
	public Orb createOrb(AddOrbDto addOrbDto, BigDecimal tranDate, UndoActionBundle undoActionBundle) {

		long orbInternalId = this.internalIdGenerator.getNewId();
		Orb orb = new Orb(orbInternalId, addOrbDto.orbTypeInternalId, new LinkedHashMap<String, String>());
		OrbType orbType = orbTypeManager.getOrbType(orb.getOrbTypeInternalId());

		populateOrbMap(orbType, orb);

		cache.orbCollection.add(orbType, orb);

		// Add delete to rollback action
		undoActionBundle.addUndoAction(this.deleteOrbCommand.toJson(orbInternalId, false), tranDate);

		return orb;
	}

	private void populateOrbMap(OrbType orbType, Orb orb) {

		LinkedHashMap<String, String> propertyMap = orb.getUserDefinedProperties();
		LinkedHashSet<String> customFields = orbType.customFields;
		
		for (String field : customFields) {
			if (!propertyMap.containsKey(field)) {
				propertyMap.put(field, null);
			}
		}
	}

	@Override
	@Loggable4Event
	public Orb deleteOrb(long orbInternalId, boolean isDeleteDependencies) {
		
		OrbCollection orbCollection = cache.orbCollection;
		
		Orb orb = orbCollection.get(orbInternalId);
		
		Orb orbCopy = orbCloner.cloneOrb(orb);
		
		OrbType orbType = orbTypeManager.getOrbType(orb.getOrbTypeInternalId());
		
		handleDependenciesForOrbDeletion(orb, isDeleteDependencies);
		
		// Process references inside of orb here.
		cache.orbCollection.delete(orbType, orbInternalId);

		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		createOrb(orbCopy);
		
		return orbCopy;
	}
	
	private void handleDependenciesForOrbDeletion(Orb orb, boolean isDeleteDependencies) {
		queryManager.handleInstanceDeleteEvent(orb.getOrbInternalId(), isDeleteDependencies);
		defLimitationManager.handleInstanceDeleteEvent(orb.getOrbInternalId(), isDeleteDependencies);
		limitationManager.handleInstanceDeleteEvent(orb.getOrbInternalId(), isDeleteDependencies);
		handleOrbReferenceDependenciesForOrbDeletion(orb, isDeleteDependencies);
	}
	
	public void handleOrbReferenceDependenciesForOrbDeletion(Orb orb, boolean isDeleteDependencies) {
		
		if (isDeleteDependencies) {
			resetAllReferencesPointingToOrb(orb);
		} else {
			long orbInternalId = orb.getOrbInternalId();
			boolean doesExist = this.cache.orbCollection.doesReferenceToOrbExist(orb);
			if (doesExist) {
				String message = String.format("Encountered problem deleting orb '%s'. Orb has at least one dependency. Specify that dependencies should be deleted automatically by passing 'true' for 'isDeleteDependencies'.", orbInternalId);
				throw new RuntimeException(message);
			}
		}
	}

	@Override
	public void resetAllReferencesPointingToOrb(Orb orb) {
		
		OrbCollection orbCollection = cache.orbCollection;
		Map<Long, Set<String>> attributeReferenceMap = orbCollection.getAttributeReferencesToOrb(orb);

		Set<Long> orbInternalIdSet = attributeReferenceMap.keySet();
		for (long orbInternalId : orbInternalIdSet) {
			Set<String> attributeNameList = attributeReferenceMap.get(orbInternalId);
			for (String attributeArrow: attributeNameList) {
				setAttribute(orbInternalId, attributeArrow, null);
				
				throw new NotImplementedException("Should not use set attribute here. Should remove reference from ref list.");
			}
		}
	}

	@Override
	public String getAttribute(long orbInternalId, String attributeName) {

		Orb orb = cache.orbCollection.get(orbInternalId);

		return orb.getUserDefinedProperties().get(attributeName);
	}

	@Override
	@Loggable4Event
	public void addAttributeAndValueToInstances(MapLongString longStringMap, long orbTypeInternalId, int indexAttribute, String attributeName) {
		
		cache.orbCollection.addAttributeValues(longStringMap.map, orbTypeInternalId, indexAttribute, attributeName);

		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		deleteOrbAttributeFromAllInstances(orbTypeInternalId, attributeName, indexAttribute);
	}

	@Override
	@Loggable4Event
	public void deleteOrbAttributeFromAllInstances(long orbTypeInternalId, String attributeName, int attributeIndex) {

		Map<Long, String> mapDeleted = cache.orbCollection.removeAttribute(orbTypeInternalId, attributeIndex, attributeName);
		
		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		addAttributeAndValueToInstances(new MapLongString(mapDeleted), orbTypeInternalId, attributeIndex, attributeName);
	}

	@Override
	@Loggable4Event
	public void setAttribute(long orbInternalId, String attributeName, String value) {
		
		String oldValue = cache.orbCollection.setAttribute(orbInternalId, attributeName, value);
		
		if (!Objects.equals(oldValue, value)) {
			Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
			setAttribute(orbInternalId, attributeName, oldValue);
		}
	}

	@Override
	public Orb getOrb(long orbInternalId) {
		Orb orb = cache.orbCollection.get(orbInternalId);

		if (orb == null) {
			throw new RuntimeException("Encountered problem getting orb. Couldn't find orb with id '" + orbInternalId + "'.");
		}

		return orb;
	}
	
	@Override
	public List<Orb> getOrbsOfType(long orbTypeInternalId) {
		return cache.orbCollection.getOrbsWithType(orbTypeInternalId);
	}

	@Override
	public void nukeAllOrbs() {
		cache.orbCollection.deleteAll();
	}

	@Override
	public boolean doesOrbExist(long orbInternalId) {
		return cache.orbCollection.doesOrbExist(orbInternalId);
	}

	@Override
	public void deleteOrbsWithType(long orbTypeInternalId, boolean isDeleteDependencies) {
		List<Orb> orbsWithType = cache.orbCollection.getOrbsWithType(orbTypeInternalId);
		
		for (Orb orb : orbsWithType) {
			deleteOrb(orb.getOrbInternalId(), isDeleteDependencies);
		}
	}

	@Override
	public void renameAttribute(long orbTypeInternalId, String attributeNameOld, String attributeNameNew) {
		cache.orbCollection.renameAttribute(orbTypeInternalId, attributeNameOld, attributeNameNew);
	}

	@Override
	public long countOrbsOfType(long orbTypeInternalId) {
		return cache.orbCollection.getCountOrbsOfType(orbTypeInternalId);
	}

	@Override
	public boolean doesOrbWithTypeExist(long orbTypeInternalId) {
		return countOrbsOfType(orbTypeInternalId) > 0;
	}
	
	@Override
	@Loggable4Event
	public void addReference(long orbInternalIdArrow, String attributeNameArrow, long orbInternalIdTarget, String attributeNameTarget) {

		Orb orb = cache.orbCollection.get(orbInternalIdArrow);

		String oldValue = orb.getUserDefinedProperties().get(attributeNameArrow);

		if (StringUtils.isEmpty(oldValue) || referenceUtil.isValueAReference(oldValue)) {
			StringBuffer sb = new StringBuffer((StringUtils.isBlank(oldValue) ? "": oldValue));
			
			StringBuffer newValue = cache.orbCollection.orbReference.addReference(orbInternalIdArrow, attributeNameArrow, sb, orbInternalIdTarget, attributeNameTarget);

			if (!Objects.equals(oldValue, newValue)) {
				orb.getUserDefinedProperties().put(attributeNameArrow, newValue.toString());

				Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
				setAttribute(orbInternalIdArrow, attributeNameArrow, oldValue);
			}
		} else {
			throw new RuntimeException(String.valueOf(orbInternalIdArrow) + "'s original value '" + oldValue + "' is not a reference.");
		}
	}

	@Override
	@Loggable4Event
	public void addReference(long arrowOrbInternalId, String arrowAttributeName, long targetOrbInternalId) {
		
		Orb orb = cache.orbCollection.get(arrowOrbInternalId);

		String oldValue = orb.getUserDefinedProperties().get(arrowAttributeName);

		if (StringUtils.isEmpty(oldValue) || referenceUtil.isValueAReference(oldValue)) {
			
			StringBuffer sb = new StringBuffer((StringUtils.isBlank(oldValue) ? "": oldValue));
			
			StringBuffer newValue = cache.orbCollection.orbReference.addReference(arrowOrbInternalId, arrowAttributeName, sb, targetOrbInternalId);

			if (!Objects.equals(oldValue, newValue)) {
				orb.getUserDefinedProperties().put(arrowAttributeName, newValue.toString());

				Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
				removeReference(arrowOrbInternalId, arrowAttributeName, targetOrbInternalId);
			}
		} else {
			throw new RuntimeException(String.valueOf(arrowOrbInternalId) + "'s original value '" + oldValue + "' is not a reference.");
		}
	}
	
	@Override
	@Loggable4Event
	public void removeReference(long arrowOrbInternalId, String arrowAttributeName, long targetOrbInternalId) {
		Orb orb = cache.orbCollection.get(arrowOrbInternalId);

		String oldValue = orb.getUserDefinedProperties().get(arrowAttributeName);
		
		if (StringUtils.isEmpty(oldValue) || referenceUtil.isValueAReference(oldValue)) {
			StringBuffer sb = new StringBuffer((StringUtils.isBlank(oldValue) ? "": oldValue));
			
			StringBuffer newValue = cache.orbCollection.orbReference.removeReference(arrowOrbInternalId, arrowAttributeName, sb, targetOrbInternalId);

			if (!Objects.equals(oldValue, newValue)) {
				orb.getUserDefinedProperties().put(arrowAttributeName, newValue.toString());

				Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
				addReference(arrowOrbInternalId, arrowAttributeName, targetOrbInternalId);
			}
		} else {
			throw new RuntimeException(String.valueOf(arrowOrbInternalId) + "'s original value '" + oldValue + "' is not a reference.");
		}
	}
	
	@Override
	@Loggable4Event
	public void removeReference(long arrowOrbInternalId, String arrowAttributeName, long targetOrbInternalId, String targetAttributeName) {
		Orb orb = cache.orbCollection.get(arrowOrbInternalId);

		String oldValue = orb.getUserDefinedProperties().get(arrowAttributeName);
		
		logger.debug("Removing ref: {}", oldValue);

		if (StringUtils.isEmpty(oldValue) || referenceUtil.isValueAReference(oldValue)) {
			StringBuffer sb = new StringBuffer((StringUtils.isBlank(oldValue) ? "": oldValue));
			
			StringBuffer newValue = cache.orbCollection.orbReference.removeReference(arrowOrbInternalId, arrowAttributeName, sb, targetOrbInternalId, targetAttributeName);

			if (!Objects.equals(oldValue, newValue)) {
				orb.getUserDefinedProperties().put(arrowAttributeName, newValue.toString());

				Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
				addReference(arrowOrbInternalId, arrowAttributeName, targetOrbInternalId, targetAttributeName);
			}
		} else {
			throw new RuntimeException(String.valueOf(arrowOrbInternalId) + "'s original value '" + oldValue + "' is not a reference.");
		}
	}

	@Override
	@Loggable4Event
	public void updateOrb(Orb orb) {
		
		Orb orbToUpdate = cache.orbCollection.get(orb.getOrbInternalId());
		
		Orb orbClone = this.orbCloner.cloneOrb(orbToUpdate);
			
		LinkedHashMap<String, String> userDefinedPropMap = orbToUpdate.getUserDefinedProperties();
		Set<String> keySet = userDefinedPropMap.keySet();
		for (String fieldName: keySet) {
			setAttribute(orb.getOrbInternalId(), fieldName, orb.getUserDefinedProperties().get(fieldName));
		}
		
		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		updateOrb(orbClone);
	}
}
