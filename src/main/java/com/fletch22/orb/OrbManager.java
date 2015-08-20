package com.fletch22.orb;

import java.math.BigDecimal;
import java.util.Map;

import com.fletch22.orb.command.orbType.dto.AddOrbDto;
import com.fletch22.orb.rollback.UndoActionBundle;

public interface OrbManager {
	
	public void createOrb(Orb orb);
	
	public Orb createOrb(OrbType orbType, BigDecimal tranDate);

	public Orb createOrb(long orbTypeInternalId, BigDecimal tranDate);
	
	public Orb createOrb(AddOrbDto addOrbDto, BigDecimal tranDate, UndoActionBundle undoActionBundle);
	
	public String getAttribute(long orbInternalId, String attributeName);
	
	public Orb setAttribute(long orbInternalId, String attributeName, String value);
	
	public void deleteOrb(long orbInternalId);
	
	public boolean doesOrbExist(long orbInternalId);
	
	public Orb getOrb(long orbInternalId);

	public void deleteAllOrbs();

	void addAttributeAndValueToInstances(Map<Long, String> map, long orbTypeInternalId, int indexOfAttribute, String attributeName);

	void removeOrbAttributeFromAllInstances(long orbTypeInternalId, String attributeName);
}
