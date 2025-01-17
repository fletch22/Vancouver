package com.fletch22.app.designer.dao;

import java.util.HashSet;
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
import com.fletch22.app.designer.dataField.DataField;
import com.fletch22.app.designer.dataModel.DataModel;
import com.fletch22.app.designer.dataUniverse.DataUniverse;
import com.fletch22.app.designer.datastore.Datastore;
import com.fletch22.app.designer.ddl.DropDownListbox;
import com.fletch22.app.designer.div.Div;
import com.fletch22.app.designer.page.Page;
import com.fletch22.app.designer.submit.ButtonSubmit;
import com.fletch22.app.designer.webFolder.WebFolder;
import com.fletch22.app.designer.website.Website;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbCloner;
import com.fletch22.orb.OrbComparer;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.cache.local.ComparisonResult;
import com.fletch22.orb.cache.reference.ReferenceUtil;
import com.fletch22.orb.query.QueryManager;
import com.google.common.collect.ImmutableList;

public abstract class AppDesignerDao<T extends OrbBasedComponent, U extends DomainTransformer<T>> extends BaseDao {

	static Logger logger = LoggerFactory.getLogger(AppDesignerDao.class);

	@Autowired
	public ReferenceUtil referenceUtil;

	@Autowired
	protected OrbTypeManager orbTypeManager;

	@Autowired
	protected QueryManager queryManager;

	@Autowired
	DaoJunction daoJunction;

	@Autowired
	OrbComparer orbComparer;

	@Autowired
	OrbCloner orbCloner;

	protected abstract void create(T t);

	protected void create(T t, OrbType orbType) {

		Orb orbToCreate = orbManager.createUnsavedInitializedOrb(orbType.id);
		orbToCreate.getUserDefinedProperties().put(OrbBasedComponent.ATTR_PARENT, String.valueOf(t.getParentId()));

		setNonChildrenAttributes(t, orbToCreate);

		if (t instanceof Parent) {
			this.setOrbChildrenAttribute((Parent) t, orbToCreate);
		}

		orbToCreate = orbManager.createOrb(orbToCreate);

		t.setOrbOriginal(orbToCreate);
		t.setId(orbToCreate.getOrbInternalId());
	}

	public StringBuffer convertToChildReferences(ComponentChildren componentChildren) {

		ImmutableList<Child> list = componentChildren.getList();
		Set<String> refSet = new HashSet<String>();
		for (Child child : list) {
			refSet.add(referenceUtil.composeReference(child.getId()));
		}

		return referenceUtil.composeReferences(refSet);
	}

	protected Orb getOrbMustExist(long orbInternalId) {
		Orb orb = this.orbManager.getOrb(orbInternalId);

		if (orb == null) {
			throw new RuntimeException("Encountered problem trying to find AppContainer orb type. Could not find orb.");
		}

		return orb;
	}

	protected void setOrbChildrenAttribute(Parent parent, Orb orbToUpdate) {
		ComponentChildren componentChildren = parent.getChildren();
		if (componentChildren.isHaveChildrenBeenResolved()) {
			orbToUpdate.getUserDefinedProperties().put(Parent.ATTR_CHILDREN, convertToChildReferences(componentChildren).toString());
		}
	}

	protected abstract U getTransformer();

	protected void saveChildren(Orb orbToUpdate, Parent parent) {

		ComponentChildren componentChildren = parent.getChildren();
		if (componentChildren.isHaveChildrenBeenResolved()) {

			for (Child orbBasedComponentChild : componentChildren.getList()) {

				switch (orbBasedComponentChild.getTypeLabel()) {
				case AppContainer.TYPE_LABEL:
					daoJunction.appContainerDao.update((AppContainer) orbBasedComponentChild);
					break;
				case App.TYPE_LABEL:
					daoJunction.appDao.update((App) orbBasedComponentChild);
					break;
				case ButtonSubmit.TYPE_LABEL:
					daoJunction.buttonSubmitDao.update((ButtonSubmit) orbBasedComponentChild);
					break;
				case Website.TYPE_LABEL:
					daoJunction.websiteDao.update((Website) orbBasedComponentChild);
					break;
				case WebFolder.TYPE_LABEL:
					daoJunction.webFolderDao.update((WebFolder) orbBasedComponentChild);
					break;
				case Page.TYPE_LABEL:
					daoJunction.pageDao.update((Page) orbBasedComponentChild);
					break;
				case Div.TYPE_LABEL:
					daoJunction.divDao.update((Div) orbBasedComponentChild);
					break;
				case DropDownListbox.TYPE_LABEL:
					daoJunction.dropDownListboxDao.update((DropDownListbox) orbBasedComponentChild);
					break;
				case DataUniverse.TYPE_LABEL:
					daoJunction.dataUniverseDao.update((DataUniverse) orbBasedComponentChild);
					break;
				case Datastore.TYPE_LABEL:
					daoJunction.datastoreDao.update((Datastore) orbBasedComponentChild);
					break;
				case DataModel.TYPE_LABEL:
					daoJunction.dataModelDao.update((DataModel) orbBasedComponentChild);
					break;
				case DataField.TYPE_LABEL:
					daoJunction.dataFieldDao.update((DataField) orbBasedComponentChild);
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

		Orb orbToUpdate = orbCloner.cloneOrb(t.getOrbOriginal());

		orbToUpdate.getUserDefinedProperties().put(OrbBasedComponent.ATTR_PARENT, String.valueOf(t.getParentId()));
		if (t instanceof Parent) {
			logger.debug("OrbToUpdate before mods: {}: childrenRefs: {}", orbToUpdate.getOrbInternalId(), orbToUpdate.getUserDefinedProperties().get(Parent.ATTR_CHILDREN));
		}

		setNonChildrenAttributes(t, orbToUpdate);

		if (t instanceof Parent) {
			this.setOrbChildrenAttribute((Parent) t, orbToUpdate);
		}

		ComparisonResult comparisonResult = this.orbComparer.areSame(orbToUpdate, t.getOrbOriginal());

		if (!comparisonResult.isSame) {
			if (t instanceof Parent) {
				logger.debug("OrbToUpdate: {}: childrenRefs: {}", orbToUpdate.getOrbInternalId(), orbToUpdate.getUserDefinedProperties().get(Parent.ATTR_CHILDREN));
			}
			orbManager.updateOrb(orbToUpdate);
		}

		t.setOrbOriginal(orbToUpdate);
	}

	protected abstract void setNonChildrenAttributes(T t, Orb orb);
}
