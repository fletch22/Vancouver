package com.fletch22.orb.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fletch22.aop.Log4EventAspect;
import com.fletch22.aop.Loggable4Event;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.cache.query.CriteriaCollection;
import com.fletch22.orb.query.CriteriaFactory.Criteria;

public abstract class AbstractCriteriaManager implements CriteriaManager {
	
	static Logger logger = LoggerFactory.getLogger(AbstractCriteriaManager.class);

	public abstract CriteriaCollection getCriteriaCollection();
	protected abstract OrbType getParentOrbType();
	
	@Autowired
	QueryAttributeRenameHandler queryAttributeRenameHandler;

	@Autowired
	QueryAttributeDeleteHandler queryAttributeDeleteHandler;
	
	@Autowired
	protected OrbManager orbManager;
	
	@Override
	public long create(Criteria criteria) {
		
		OrbType orbType = getParentOrbType();

		Orb orb = new Orb();
		orb.setOrbTypeInternalId(orbType.id);
		
		orb = orbManager.createOrb(orb);
		
		criteria.setId(orb.getOrbInternalId());

		addToCollection(criteria);

		return orb.getOrbInternalId();
	}
	
	@Loggable4Event
	@Override
	public void addToCollection(Criteria criteria) {
		getCriteriaCollection().add(criteria);
		
		logger.info("Adding to collection.");
		
		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		removeFromCollection(criteria.getCriteriaId());
	}
	
	@Loggable4Event
	@Override
	public void removeFromCollection(long criteriaId) {
		Criteria criteria = getCriteriaCollection().removeByCriteriaId(criteriaId);
		
		logger.info("Removing from collection.");

		if (criteria != null) {
			Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
			addToCollection(criteria);
		}
	}

	@Override
	public void nukeAllCriteria() {
		getCriteriaCollection().clear();
	}

	@Override
	public Criteria get(long criteriaId) {
		return getCriteriaCollection().getByQueryId(criteriaId);
	}
	
	@Override
	public void handleAttributeRenameEvent(long orbTypeInternalId, String attributeOldName, String attributeNewName) {
		queryAttributeRenameHandler.handleAttributeRename(getCriteriaCollection(), orbTypeInternalId, attributeOldName, attributeNewName);
	}

	@Override
	public void handleAttributeDeleteEvent(long orbTypeInternalId, String attributeName, boolean isDeleteDependencies) {
		queryAttributeDeleteHandler.handleAttributeDeletion(orbTypeInternalId, attributeName, isDeleteDependencies);
	}
}
