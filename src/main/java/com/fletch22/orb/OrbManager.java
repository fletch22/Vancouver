package com.fletch22.orb;

import java.math.BigDecimal;
import java.util.List;

import com.fletch22.orb.command.orbType.dto.AddOrbDto;
import com.fletch22.orb.rollback.UndoActionBundle;
import com.fletch22.util.json.MapLongString;

public interface OrbManager {
	
	void addAttributeAndValueToInstances(MapLongString map, long orbTypeInternalId, int indexOfAttribute, String attributeName);
	
	public void addReference(long arrowOrbInternalId, String arrowAttributeName, long targetOrbInternalId);
	
	public void addReference(long arrowOrbInternalId, String arrowAttributeName, long targetOrbInternalId, String targetAttributeName);
	
	public long countOrbsOfType(long id);
	
	public Orb createOrb(AddOrbDto addOrbDto, BigDecimal tranDate, UndoActionBundle undoActionBundle);

	public Orb createOrb(long orbTypeInternalId);
	
	public Orb createOrb(Orb orb);
	
	public Orb deleteOrb(long orbInternalId, boolean isDeleteDependencies);
	
	void deleteOrbAttributeFromAllInstances(long orbTypeInternalId, String attributeName, int attributeIndex);
	
	public void deleteOrbsWithType(long orbTypeInternalId, boolean isDeleteDependencies);
	
	public boolean doesOrbExist(long orbInternalId);

	public boolean doesOrbWithTypeExist(long orbTypeInternalId);

	public String getAttribute(long orbInternalId, String attributeName);

	public Orb getOrb(long orbInternalId);

	public List<Orb> getOrbsOfType(long orbInternalId);

	public void nukeAndPave();
	
	void removeReference(long arrowOrbInternalId, String arrowAttributeName, long targetOrbInternalId);
	
	void removeReference(long arrowOrbInternalId, String arrowAttributeName, long targetOrbInternalId, String targetAttributeName);
	
	public void renameAttribute(long orbTypeInternalId, String attributeNameOld, String attributeNameNew);

	public void resetAllReferencesPointingToOrb(Orb orb);

	public void setAttribute(long orbInternalId, String attributeName, String value);

	void updateOrb(Orb orb);
	
	public Orb createUnsavedInitializedOrb(OrbType orbType);
	
	public Orb createUnsavedInitializedOrb(long orbTypeInternalId);
}
