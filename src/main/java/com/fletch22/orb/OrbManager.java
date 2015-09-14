package com.fletch22.orb;

import java.math.BigDecimal;
import java.util.List;

import com.fletch22.orb.command.orbType.dto.AddOrbDto;
import com.fletch22.orb.rollback.UndoActionBundle;
import com.fletch22.util.json.MapLongString;

public interface OrbManager {
	
	public void createOrb(Orb orb);
	
	public Orb createOrb(long orbTypeInternalId);
	
	public Orb createOrb(OrbType orbType, BigDecimal tranDate);

	public Orb createOrb(long orbTypeInternalId, BigDecimal tranDate);
	
	public Orb createOrb(AddOrbDto addOrbDto, BigDecimal tranDate, UndoActionBundle undoActionBundle);
	
	public String getAttribute(long orbInternalId, String attributeName);
	
	public void setAttribute(long orbInternalId, String attributeName, String value);
	
	public void deleteOrb(long orbInternalId);
	
	public boolean doesOrbExist(long orbInternalId);
	
	public Orb getOrb(long orbInternalId);
	
	public List<Orb> getOrbsOfType(long orbInternalId);

	public void deleteAllOrbs();

	void addAttributeAndValueToInstances(MapLongString map, long orbTypeInternalId, int indexOfAttribute, String attributeName);

	void deleteOrbAttributeFromAllInstances(long orbTypeInternalId, String attributeName, int attributeIndex);

	public void deleteOrbsWithType(long orbTypeInternalId);

	public void renameAttribute(long orbTypeInternalId, String attributeNameOld, String attributeNameNew);
}
