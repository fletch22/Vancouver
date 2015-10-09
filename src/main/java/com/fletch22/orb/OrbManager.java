package com.fletch22.orb;

import java.math.BigDecimal;
import java.util.List;

import com.fletch22.orb.command.orbType.dto.AddOrbDto;
import com.fletch22.orb.rollback.UndoActionBundle;
import com.fletch22.util.json.MapLongString;

public interface OrbManager {
	
	public Orb createOrb(Orb orb);
	
	public Orb createOrb(long orbTypeInternalId);
	
	public Orb createOrb(OrbType orbType, BigDecimal tranDate);

	public Orb createOrb(long orbTypeInternalId, BigDecimal tranDate);
	
	public Orb createOrb(AddOrbDto addOrbDto, BigDecimal tranDate, UndoActionBundle undoActionBundle);
	
	public String getAttribute(long orbInternalId, String attributeName);
	
	public void setAttribute(long orbInternalId, String attributeName, String value);
	
	public Orb deleteOrb(long orbInternalId, boolean isDeleteDependencies);
	
	public Orb deleteOrbIgnoreQueryDependencies(long orbInternalId, boolean isDeleteDependencies);
	
	public boolean doesOrbExist(long orbInternalId);
	
	public Orb getOrb(long orbInternalId);
	
	public List<Orb> getOrbsOfType(long orbInternalId);

	public void nukeAllOrbs();

	void addAttributeAndValueToInstances(MapLongString map, long orbTypeInternalId, int indexOfAttribute, String attributeName);

	void deleteOrbAttributeFromAllInstances(long orbTypeInternalId, String attributeName, int attributeIndex);

	public void deleteOrbsWithType(long orbTypeInternalId, boolean isDeleteDependencies);

	public void renameAttribute(long orbTypeInternalId, String attributeNameOld, String attributeNameNew);
	
	public void resetAllReferencesPointingToOrb(Orb orb);

	public long countOrbsOfType(long id);

	public boolean doesOrbWithTypeExist(long orbTypeInternalId);
}
