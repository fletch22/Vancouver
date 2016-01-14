package com.fletch22.app.designer.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fletch22.app.designer.Child;
import com.fletch22.app.designer.ComponentChildren;
import com.fletch22.app.designer.DomainTransformer;
import com.fletch22.app.designer.OrbBasedComponent;
import com.fletch22.app.designer.Parent;
import com.fletch22.app.designer.app.App;
import com.fletch22.app.designer.appContainer.AppContainer;
import com.fletch22.app.designer.website.Website;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.cache.reference.ReferenceUtil;
import com.fletch22.orb.query.QueryManager;

public abstract class AppDesignerDao<T extends OrbBasedComponent, U extends DomainTransformer<T>> {
	
	static Logger logger = LoggerFactory.getLogger(AppDesignerDao.class);
	
	@Autowired
	public OrbManager orbManager;

	@Autowired
	public ReferenceUtil referenceUtil;
	
	@Autowired
	protected OrbTypeManager orbTypeManager;
	
	@Autowired
	protected QueryManager queryManager;
	
	@Autowired
	DaoJunction daoJunction;
	
	protected abstract void create(T t);
	
	protected void create(T t, OrbType orbType) {

		Orb orbToCreate = craftProtoOrb(t, orbType);
		
		setNonChildrenAttributes(t, orbToCreate);
		
		if (t instanceof Parent) {
			this.setOrbChildrenAttribute((Parent) t, orbToCreate);
		}
		
		orbToCreate = orbManager.createOrb(orbToCreate);
		
		t.setOrbOriginal(orbToCreate);
		t.setId(orbToCreate.getOrbInternalId());
	}
		
	
	public StringBuffer convertToChildReferences(ComponentChildren componentChildren) {
		
		ArrayList<Child> list = componentChildren.list();
		Set<String> refSet = new HashSet<String>();
		for (Child child : list) {
			refSet.add(referenceUtil.composeReference(child.getId()));
		}
		
		return referenceUtil.composeReferences(refSet);
	}
	
	public void delete(long id) {
		orbManager.deleteOrb(id, false);
	}

	protected Orb getOrbMustExist(long orbInternalId) {
		Orb orb = this.orbManager.getOrb(orbInternalId);
	
		if (orb == null) {
			throw new RuntimeException("Encountered problem trying to find AppContainer orb type. Could not find orb.");
		}
		
		return orb;
	}

	protected void setOrbChildrenAttribute(Parent parent, Orb orbToUpdate) {
		orbToUpdate.getUserDefinedProperties().put(Parent.ATTR_CHILDREN, convertToChildReferences(parent.getChildren()).toString());
	}

	protected Orb craftProtoOrb(OrbBasedComponent orbBasedComponent, OrbType orbType) {
		Orb orb = new Orb();
		orb.setOrbTypeInternalId(orbType.id);
		
		initializeOrbFields(orbType, orb);
		
		orb.getUserDefinedProperties().put(OrbBasedComponent.ATTR_PARENT, String.valueOf(orbBasedComponent.getParentId()));
		return orb;
	}

	private void initializeOrbFields(OrbType orbType, Orb orb) {
		LinkedHashSet<String> linkedHashSet = orbType.customFields;
		for (String field: linkedHashSet) {
			orb.getUserDefinedProperties().put(field, null);
		}
	}
	
	protected abstract U getTransformer();

	protected void saveChildren(Orb orbToUpdate, Parent parent) {
		
		ComponentChildren componentChildren = parent.getChildren();
		if (componentChildren.isHaveChildrenBeenResolved()) {
			
			for (Child orbBasedComponentChild : componentChildren.list()) {

				switch (orbBasedComponentChild.getTypeLabel()) {
					case AppContainer.TYPE_LABEL:
						daoJunction.appContainerDao.save( (AppContainer) orbBasedComponentChild);
						break;
					case App.TYPE_LABEL:
						daoJunction.appDao.update( (App) orbBasedComponentChild);
						break;
					case Website.TYPE_LABEL:
						daoJunction.websiteDao.update( (Website) orbBasedComponentChild);
						break;
					default:
						throw new RuntimeException("Encountered problem while processing children for update. Found an unrecognized type.");
				}
			}
		}
	}
	
	public T read(long orbInternalId) {
		Orb orb = getOrbMustExist(orbInternalId);
		
		return getTransformer().transform(orb);
	}
	
	public T save(T t) {
		t = ensureSavedWithoutChildren(t);
		
		if (t instanceof Parent) {
			saveChildren(t.getOrbOriginal(), (Parent) t);
		}
		
		return t;
	}
	
	public T ensureSavedWithoutChildren(T t) {
		
		if (t.isNew()) {
			create(t);
		} else {
			update(t);
		}
		
		return t;
	}
	
	protected void update(T t) {

		Orb orbToUpdate = getOrbMustExist(t.getId());

		setNonChildrenAttributes(t, orbToUpdate);
		
		if (t instanceof Parent) {
			this.setOrbChildrenAttribute((Parent) t, orbToUpdate);
		}
		
		orbManager.updateOrb(orbToUpdate);

		t.setOrbOriginal(orbToUpdate);
	}
	
	protected abstract void setNonChildrenAttributes(T t, Orb orb);
}
