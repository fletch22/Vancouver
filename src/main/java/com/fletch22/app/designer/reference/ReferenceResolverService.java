package com.fletch22.app.designer.reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.OrbBasedComponent;
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
	
	private void resolveAllDescendents(OrbBasedComponent orbBasedComponent, String references) {

		Set<DecomposedKey> decomposedKeySet = referenceUtil.convertToKeySet(references);
		
		Map<Long, OrbType> cachedOrbTypes = new HashMap<Long, OrbType>();
		
		for (DecomposedKey decomposedKey : decomposedKeySet) {
			if (decomposedKey.isKeyPointingToAttribute()) {
				throw new RuntimeException("Encountered a problem resolving a child that was not an object reference; it was an orb attribute reference."); 
			}
			
			Orb orbChild = orbManager.getOrb(decomposedKey.getOrbInternalId());
			OrbType orbType = getAndCacheOrbType(cachedOrbTypes, orbChild);
			
			OrbBasedComponent orbBaseComponentChild = addChild(orbBasedComponent, orbChild, orbType);
			resolveAllDescendents(orbBaseComponentChild);
		}
		
		orbBasedComponent.getChildren().setHaveChildrenBeenResolved(true);
	}
	
	public void resolveAllDescendents(OrbBasedComponent orbBasedComponent) {
		
		orbBasedComponent.getChildren().clear();
		String childReferences = orbBasedComponent.getOrbOriginal().getUserDefinedProperties().get(OrbBasedComponent.ATTR_CHILDREN);
		if (childReferences != null) {
			resolveAllDescendents(orbBasedComponent, childReferences);
		}
	}

	private OrbBasedComponent addChild(OrbBasedComponent orbBasedComponentParent, Orb orbChild, OrbType orbType) {
		ArrayList<OrbBasedComponent> children = orbBasedComponentParent.getChildren().list();
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
