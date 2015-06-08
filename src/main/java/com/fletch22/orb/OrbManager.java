package com.fletch22.orb;

import java.math.BigDecimal;

import com.fletch22.orb.command.orbType.dto.AddOrbDto;
import com.fletch22.orb.rollback.UndoActionBundle;

public interface OrbManager {

	public Orb createOrbInstance(AddOrbDto addOrbDto, BigDecimal tranDate, UndoActionBundle undoActionBundle);

	public void deleteAllOrbInstances();
}
