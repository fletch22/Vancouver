package com.fletch22.app.designer.reference;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.Child;
import com.fletch22.app.designer.ComponentChildren;
import com.fletch22.app.designer.OrbBasedComponent;
import com.fletch22.app.designer.Parent;
import com.fletch22.app.designer.app.App;
import com.fletch22.app.designer.app.AppService;
import com.fletch22.app.designer.appContainer.AppContainer;
import com.fletch22.app.designer.appContainer.AppContainerService;
import com.fletch22.app.designer.dataField.DataField;
import com.fletch22.app.designer.dataField.DataFieldService;
import com.fletch22.app.designer.dataModel.DataModel;
import com.fletch22.app.designer.dataModel.DataModelService;
import com.fletch22.app.designer.dataUniverse.DataUniverse;
import com.fletch22.app.designer.dataUniverse.DataUniverseService;
import com.fletch22.app.designer.datastore.Datastore;
import com.fletch22.app.designer.datastore.DatastoreService;
import com.fletch22.app.designer.ddl.DropDownListbox;
import com.fletch22.app.designer.ddl.DropDownListboxService;
import com.fletch22.app.designer.div.Div;
import com.fletch22.app.designer.div.DivService;
import com.fletch22.app.designer.layout.LayoutService;
import com.fletch22.app.designer.layoutMinion.LayoutMinionService;
import com.fletch22.app.designer.page.Page;
import com.fletch22.app.designer.page.PageService;
import com.fletch22.app.designer.submit.ButtonSubmit;
import com.fletch22.app.designer.submit.ButtonSubmitService;
import com.fletch22.app.designer.webFolder.WebFolder;
import com.fletch22.app.designer.webFolder.WebFolderService;
import com.fletch22.app.designer.website.Website;
import com.fletch22.app.designer.website.WebsiteService;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.cache.reference.DecomposedKey;
import com.fletch22.orb.cache.reference.ReferenceUtil;

@Component
public class ReferenceResolverService {

	Logger logger = LoggerFactory.getLogger(ReferenceResolverService.class);

	@Autowired
	OrbTypeManager orbTypeManager;

	@Autowired
	ReferenceUtil referenceUtil;

	@Autowired
	OrbManager orbManager;

	@Autowired
	AppContainerService appContainerService;

	@Autowired
	AppService appService;

	@Autowired
	ButtonSubmitService buttonSubmitService;

	@Autowired
	WebsiteService websiteService;

	@Autowired
	WebFolderService webFolderService;

	@Autowired
	PageService pageService;

	@Autowired
	LayoutService layoutService;

	@Autowired
	LayoutMinionService layoutMinionService;

	@Autowired
	DivService divService;

	@Autowired
	DropDownListboxService dropDownListboxService;

	@Autowired
	DataUniverseService dataUniverseService;

	@Autowired
	DatastoreService datastoreService;

	@Autowired
	DataModelService dataModelService;

	@Autowired
	DataFieldService dataFieldService;

	public OrbType getAndCacheOrbType(Map<Long, OrbType> cachedOrbTypes, Orb orb) {
		OrbType orbType = null;
		if (!cachedOrbTypes.containsKey(orb.getOrbTypeInternalId())) {
			orbType = orbTypeManager.getOrbType(orb.getOrbTypeInternalId());
			cachedOrbTypes.put(orbType.id, orbType);
		} else {
			orbType = cachedOrbTypes.get(orb.getOrbTypeInternalId());
		}

		return orbType;
	}

	private void clearAndResolveAllDescendents(Parent orbBasedComponentParent, String references) {

		clearAndResolveNextGeneration(orbBasedComponentParent, references);

		for (Child child : orbBasedComponentParent.getChildren().getList()) {
			if (child instanceof Parent) {
				clearAndResolveAllDescendents((Parent) child);
			}
		}
	}

	public void clearAndResolveAllDescendents(Parent orbBasedComponentParent) {

		orbBasedComponentParent.getChildren().clear();
		String childReferences = orbBasedComponentParent.getOrbOriginal().getUserDefinedProperties().get(Parent.ATTR_CHILDREN);
		if (childReferences != null) {
			clearAndResolveAllDescendents(orbBasedComponentParent, childReferences);
		}
	}

	private void clearAndResolveNextGeneration(Parent orbBasedComponentParent, String childReferences) {

		Set<DecomposedKey> decomposedKeySet = referenceUtil.convertToKeySet(childReferences);
		Map<Long, OrbType> cachedOrbTypes = new HashMap<Long, OrbType>();

		for (DecomposedKey decomposedKey : decomposedKeySet) {
			validateChildKey(decomposedKey);

			Orb orbChild = orbManager.getOrb(decomposedKey.getOrbInternalId());
			OrbType orbType = getAndCacheOrbType(cachedOrbTypes, orbChild);

			addChild(orbBasedComponentParent, orbChild, orbType);
		}

		orbBasedComponentParent.getChildren().setHaveChildrenBeenResolved(true);
		orbBasedComponentParent.getChildren().sort();
		orbBasedComponentParent.getChildren().sortByOrdinal();
	}

	private void validateChildKey(DecomposedKey decomposedKey) {
		if (decomposedKey.isKeyPointingToAttribute()) {
			throw new RuntimeException("Encountered a problem resolving a child that was not an object reference; it was an orb attribute reference.");
		}
	}

	public void clearAndResolveNextGeneration(Parent orbBasedComponentParent) {

		orbBasedComponentParent.getChildren().clear();
		String childReferences = orbBasedComponentParent.getOrbOriginal().getUserDefinedProperties().get(Parent.ATTR_CHILDREN);

		if (childReferences != null) {
			clearAndResolveNextGeneration(orbBasedComponentParent, childReferences);
		} else {
			orbBasedComponentParent.getChildren().setHaveChildrenBeenResolved(true);
		}
	}

	private OrbBasedComponent addChild(Parent parent, Orb orbChild, OrbType orbType) {
		ComponentChildren componentChildren = parent.getChildren();
		long childId = orbChild.getOrbInternalId();

		OrbBasedComponent orbBaseComponentChild = null;
		switch (orbType.label) {
		case AppContainer.TYPE_LABEL:
			orbBaseComponentChild = appContainerService.get(childId);
			break;
		case App.TYPE_LABEL:
			orbBaseComponentChild = appService.get(childId);
			break;
		case ButtonSubmit.TYPE_LABEL:
			orbBaseComponentChild = buttonSubmitService.get(childId);
			break;
		case Website.TYPE_LABEL:
			orbBaseComponentChild = websiteService.get(childId);
			break;
		case WebFolder.TYPE_LABEL:
			orbBaseComponentChild = webFolderService.get(childId);
			break;
		case Page.TYPE_LABEL:
			orbBaseComponentChild = pageService.get(childId);
			break;
		case Div.TYPE_LABEL:
			orbBaseComponentChild = divService.get(childId);
			break;
		case DropDownListbox.TYPE_LABEL:
			orbBaseComponentChild = dropDownListboxService.get(childId);
			break;
		case DataUniverse.TYPE_LABEL:
			orbBaseComponentChild = dataUniverseService.get(childId);
			break;
		case Datastore.TYPE_LABEL:
			orbBaseComponentChild = datastoreService.get(childId);
			break;
		case DataModel.TYPE_LABEL:
			orbBaseComponentChild = dataModelService.get(childId);
			break;
		case DataField.TYPE_LABEL:
			orbBaseComponentChild = dataFieldService.get(childId);
			break;
		default:
			throw new RuntimeException("Encountered problem trying to determine type while resolving children.");
		}

		componentChildren.addChildIgnoreOrdinal(orbBaseComponentChild);

		return orbBaseComponentChild;
	}
}
