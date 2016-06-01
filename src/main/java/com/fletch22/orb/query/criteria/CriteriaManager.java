package com.fletch22.orb.query.criteria;

import java.util.List;
import java.util.Map;

import com.fletch22.orb.attribute.OrbEventAware;
import com.fletch22.orb.cache.query.CriteriaCollection;

public interface CriteriaManager extends OrbEventAware {

	public long addToCollection(Criteria criteria);
	
	public void delete(long criteriaId, boolean isDeleteDependencies);
	
	public void attach(Criteria criteria);
	
	public void detach(long criteriaId);
	
	public Map<Long, Criteria> getOrbsTypeCriteria(long orbTypeInternalId);
	
	public void nukeAndPave();
	
	public boolean doesCriteriaExist(long orbInternalIdQuery);
	
	public Criteria get(long orbInternalIdQuery);
	
	public void collectCriteriaChildren(final Criteria criteria, List<Criteria> criteriaListe);
	
	public CriteriaCollection getCriteriaCollection();
}
