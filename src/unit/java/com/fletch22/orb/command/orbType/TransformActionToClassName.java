package com.fletch22.orb.command.orbType;

public class TransformActionToClassName {

	public Class<?> transformAction(String actionId) {

		Class<?> clazz = null;
		if (AddOrbTypePackage.ACTION_ID.equals(actionId)) {
			clazz = AddOrbTypePackage.class;
		}
		
		if (null == clazz) {
			throw new RuntimeException("Encountered problem finding matching class.");
		}
		
		return clazz;
	}

}
