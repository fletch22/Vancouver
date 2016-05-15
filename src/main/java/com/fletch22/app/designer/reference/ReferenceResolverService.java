package com.fletch22.app.designer.reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.Child;
import com.fletch22.app.designer.OrbBasedComponent;
import com.fletch22.app.designer.Parent;
import com.fletch22.app.designer.app.App;
import com.fletch22.app.designer.app.AppService;
import com.fletch22.app.designer.appContainer.AppContainer;
import com.fletch22.app.designer.appContainer.AppContainerService;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.orb.cache.reference.DecomposedKey;
import com.fletch22.orb.cache.reference.ReferenceUtil;
import com.fletch22.util.StopWatch;

@Component
public class ReferenceResolverService {
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
				clearAndResolveNextGeneration((Parent) child);
			}
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
	}

	private void validateChildKey(DecomposedKey decomposedKey) {
		if (decomposedKey.isKeyPointingToAttribute()) {
			throw new RuntimeException("Encountered a problem resolving a child that was not an object reference; it was an orb attribute reference."); 
		}
	}
	
	public void clearAResolveAllDescendents(Parent orbBasedComponentParent) {
		
		orbBasedComponentParent.getChildren().clear();
		String childReferences = orbBasedComponentParent.getOrbOriginal().getUserDefinedProperties().get(Parent.ATTR_CHILDREN);
		if (childReferences != null) {
			clearAndResolveAllDescendents(orbBasedComponentParent, childReferences);
		}
	}
	
	public void clearAndResolveNextGeneration(Parent orbBasedComponentParent) {
		
		orbBasedComponentParent.getChildren().clear();
		String childReferences = orbBasedComponentParent.getOrbOriginal().getUserDefinedProperties().get(Parent.ATTR_CHILDREN);
		if (childReferences != null) {
			clearAndResolveNextGeneration(orbBasedComponentParent, childReferences);
		}
	}

	private OrbBasedComponent addChild(Parent parent, Orb orbChild, OrbType orbType) {
		ArrayList<Child> children = parent.getChildren().getList();
		long childId = orbChild.getOrbInternalId();
		
		OrbBasedComponent orbBaseComponentChild = null;
		switch (orbType.label) {
			case AppContainer.TYPE_LABEL:
				orbBaseComponentChild = appContainerService.get(childId);
				break;
			case App.TYPE_LABEL:
				orbBaseComponentChild = appService.get(childId);
				break;
			default:
				throw new RuntimeException("Encountered problem trying to determine type while resolving children.");
		}
		
		children.add(orbBaseComponentChild);
		
		return orbBaseComponentChild;
	}
}
