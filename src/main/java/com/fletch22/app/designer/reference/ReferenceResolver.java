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
public class ReferenceResolver {
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
	
	public void resolve(OrbBasedComponent orbBasedComponent, String references, boolean isResolveAllChildren) {

		Set<DecomposedKey> decomposedKeySet = referenceUtil.convertToKeySet(references);
		
		Map<Long, OrbType> cachedOrbTypes = new HashMap<Long, OrbType>();
		
		for (DecomposedKey decomposedKey : decomposedKeySet) {
			if (decomposedKey.isKeyPointingToAttribute()) {
				throw new RuntimeException("Encountered a problem resolving a child that was not an object reference; it was an orb attribute reference."); 
			}
			
			Orb orb = orbManager.getOrb(decomposedKey.getOrbInternalId());
			OrbType orbType = getAndCacheOrbType(cachedOrbTypes, orb);
			
			addChildren(orbBasedComponent, isResolveAllChildren, orb, orbType);
		}
	}

	private void addChildren(OrbBasedComponent orbBasedComponent, boolean isResolveAllChildren, Orb orb, OrbType orbType) {
		ArrayList<OrbBasedComponent> children = orbBasedComponent.getChildren();
		long childId = orb.getOrbInternalId();
		
		OrbBasedComponent orbBaseComponentChild = null;
		String attributeWithChildReferences = null;
		switch (orbType.label) {
			case AppContainer.TYPE_LABEL:
				orbBaseComponentChild = appContainerService.get(childId);
				attributeWithChildReferences = AppContainer.ATTR_APPS;
				break;
			case App.TYPE_LABEL:
				orbBaseComponentChild = appService.get(childId);
				attributeWithChildReferences = App.ATTR_WEBSITES;
				break;
			default:
				throw new RuntimeException("Encountered problem trying to determine type while resolving children.");
		}
		
		if (isResolveAllChildren) {
			String references = orb.getUserDefinedProperties().get(AppContainer.ATTR_APPS);
			if (references != null) {
				resolve(orbBaseComponentChild, orb.getUserDefinedProperties().get(attributeWithChildReferences), isResolveAllChildren);
			}
		}
		
		children.add(orbBaseComponentChild);
	}
}
