package com.fletch22.orb;

import java.math.BigDecimal;

import com.fletch22.orb.command.orbType.dto.AddOrbDto;
import com.fletch22.orb.rollback.UndoActionBundle;

public interface OrbManager {
	
	public void createOrb(Orb orb);

	public Orb createOrb(long orbTypeInternalId, BigDecimal tranDate);
	
	public Orb createOrb(AddOrbDto addOrbDto, BigDecimal tranDate, UndoActionBundle undoActionBundle);
	
	public Orb setAttribute(long orbInternalId, String attributeName, String value);
	
	public void deleteOrb(long orbInternalId);
	
	public Orb getOrb(long orbInternalId);

	public void deleteAllOrbs();
}
