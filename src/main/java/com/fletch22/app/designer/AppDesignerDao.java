package com.fletch22.app.designer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.fletch22.orb.cache.reference.ReferenceUtil;

public abstract class AppDesignerDao {

	@Autowired
	public ReferenceUtil referenceUtil;

	public  StringBuffer convertToReferences(ArrayList<? extends OrbBasedComponent> list) {
		Set<String> refSet = new HashSet<String>();
		for (OrbBasedComponent orbBasedComponent : list) {
			refSet.add(referenceUtil.composeReference(orbBasedComponent.getId()));
		}
		
		return referenceUtil.composeReferences(refSet);
	}
}
