package com.fletch22.orb.query;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fletch22.aop.Log4EventAspect;
import com.fletch22.aop.Loggable4Event;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.cache.query.CriteriaCollection;
import com.fletch22.orb.query.Criteria;
import com.fletch22.orb.query.constraint.ConstraintDeleteChildCriteriaVisitor;
import com.fletch22.orb.query.constraint.ConstraintRegistrationVisitor;
import com.fletch22.orb.query.constraint.ConstraintSetParentVisitor;
import com.fletch22.orb.systemType.SystemType;

public abstract class AbstractCriteriaManager implements CriteriaManager {
	
	static Logger logger = LoggerFactory.getLogger(AbstractCriteriaManager.class);

	public abstract CriteriaCollection getCriteriaCollection();
	
	@Autowired
	CriteriaAttributeRenameHandler criteriaAttributeRenameHandler;

	@Autowired
	protected OrbManager orbManager;
	
	@Autowired
	protected OrbTypeManager orbTypeManager;
	
	public abstract CriteriaAttributeDeleteHandler getCriteriaAttributeDeleteHandler();
	
	@Override
	public long addToCollection(Criteria criteria) {
		
		initializeCriteria(criteria);

		attach(criteria);
		
		if (criteria.hasConstraints()) {
			ConstraintRegistrationVisitor constraintRegistrationVisitor = new ConstraintRegistrationVisitor(this);
			criteria.logicalConstraint.acceptConstraintRegistrationVisitor(constraintRegistrationVisitor);
		}

		return criteria.getCriteriaId();
	}
	
	protected void initializeCriteria(Criteria criteria) {
		OrbType orbType = getParentOrbType();

		Orb orb = new Orb();
		orb.setOrbTypeInternalId(orbType.id);
		
		orb = orbManager.createOrb(orb);
		
		logger.info("Type: {}, ID: {}", orbType.id, orb.getOrbInternalId());
		
		criteria.setId(orb.getOrbInternalId());
		
		if (criteria.hasConstraints()) {
			ConstraintSetParentVisitor constraintSetParentVisitor = new ConstraintSetParentVisitor(criteria);
			criteria.logicalConstraint.acceptConstraintSetParentVisitor(constraintSetParentVisitor);
		}
	}
	
	protected OrbType getParentOrbType() {
		return orbTypeManager.getOrbType(SystemType.CRITERIA.getLabel());
	}
	
	public boolean doesCriteriaExist(long criteriaId) {
		return getCriteriaCollection().doesQueryExist(criteriaId);
	}
	
	@Loggable4Event
	@Override
	public void attach(Criteria criteria) {
		getCriteriaCollection().add(criteria);
		
		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		detach(criteria.getCriteriaId());
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
		criteriaAttributeRenameHandler.handleAttributeRename(getCriteriaCollection(), orbTypeInternalId, attributeOldName, attributeNewName);
	}

	@Override
	public void handleAttributeDeleteEvent(long orbTypeInternalId, String attributeName, boolean isDeleteDependencies) {
		getCriteriaAttributeDeleteHandler().handleAttributeDeletion(orbTypeInternalId, attributeName, isDeleteDependencies);
	}
	
	@Override
	public void handleInstanceDeleteEvent(long criteriaId, boolean isDeleteDependencies) {
		boolean doesExist = doesCriteriaExist(criteriaId);
		if (isDeleteDependencies) {
			if (doesExist) {
				deleteCriteriaChildren(criteriaId, isDeleteDependencies);
				
				detach(criteriaId);
			}
		} else {
			if (doesExist) {
				String message = String.format("Encountered problem deleting orb '%s'. Orb has at least one dependency. A query exists that depends on the orb.", criteriaId);
				throw new RuntimeException(message);
			}
		}
	}

	private void deleteCriteriaChildren(long criteriaId, boolean isDeleteDependencies) {
		Criteria criteria = get(criteriaId);
		if (criteria.hasConstraints()) {
			ConstraintDeleteChildCriteriaVisitor constraintDeleteChildCriteriaVisitor = new ConstraintDeleteChildCriteriaVisitor(this, isDeleteDependencies);
			criteria.logicalConstraint.acceptConstraintDeleteChildCriteriaVisitor(constraintDeleteChildCriteriaVisitor);
		}
	}
	
	@Loggable4Event
	@Override
	public void detach(long criteriaId) {
		Criteria criteria = this.getCriteriaCollection().removeByCriteriaId(criteriaId);
		
		Log4EventAspect.preventNextLineFromExecutingAndLogTheUndoAction();
		attach(criteria);
	}

	public void handleTypeDeleteEvent(long orbTypeInternalId, boolean isDeleteDependencies) {
		
		if (isDeleteDependencies) {
			boolean doesCriteriaExist = getCriteriaCollection().doesCriteriaExistWithOrbTypeInternalId(orbTypeInternalId);
			if (doesCriteriaExist) {
				deleteAllCriteriaBackedOrbsRelatedToType(orbTypeInternalId, isDeleteDependencies);
			}
		} else {
			throw new RuntimeException("Encountered problem handling orb type delete in query manager. Orb type has a dependency in the query collection. There is a criteria whose internal orb type internal ID references the orb type you are trying to delete.");
		}
	}
	
	private void deleteAllCriteriaBackedOrbsRelatedToType(long orbTypeInternalId, boolean isDeleteDependencies) {
		for (long id: getCriteriaIdsByTypeIds(orbTypeInternalId)) {
			orbManager.deleteOrb(id, isDeleteDependencies);
		}
	}
	
	private List<Long> getCriteriaIdsByTypeIds(long orbTypeInternalId) {
		List<Criteria> criteriaList = getCriteriaCollection().getByOrbTypeInsideCriteria(orbTypeInternalId);
		List<Long> idList = new ArrayList<Long>();
		for (Criteria criteria : criteriaList) {
			idList.add(criteria.getCriteriaId());
		}
		return idList;
	}
	
	@Override
	public void delete(long criteriaId, boolean isDeleteDependencies) {
		orbManager.deleteOrb(criteriaId, isDeleteDependencies);
	}
	
	public List<Criteria> getOrbsTypeCriteria(long orbTypeInternalId) {
		return getCriteriaCollection().getByOrbTypeInsideCriteria(orbTypeInternalId);
	}
}
