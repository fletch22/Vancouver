package com.fletch22.orb;

import java.math.BigDecimal;
import java.util.LinkedHashSet;

import com.fletch22.orb.cache.local.OrbTypeCollection.OrbType;
import com.fletch22.orb.command.orbType.DeleteOrbTypeDto;
import com.fletch22.orb.command.orbType.dto.AddOrbTypeDto;
import com.fletch22.orb.command.processor.CommandProcessActionPackageFactory.CommandProcessActionPackage;
import com.fletch22.orb.rollback.UndoActionBundle;

public interface OrbTypeManager {

	public long createOrbType(AddOrbTypeDto addOrbTypeDto, BigDecimal tranDate, final UndoActionBundle undoActionBundle);
	
	public long createOrbType(String label, long orbTypeInternalId, BigDecimal tranDate, LinkedHashSet<String> customFields);
	
	public long createOrbType(String label, LinkedHashSet<String> customFields);
	
	public void deleteOrbType(DeleteOrbTypeDto deleteOrbTypeDto, BigDecimal tranDate, UndoActionBundle rollbackAction);

	public void deleteAllTypes();
	
	public void addAttribute(long orbInternalId, String attributeName);
	
	public void deleteAttribute(long orbTypeInternalId, String attributeName, CommandProcessActionPackage commandProcessActionPackage);
	
	public void deleteOrbType(long orbTypeInternalId);

	public OrbType getOrbType(long orbTypeInternalId);
	
	public long getOrbTypeCount();
}
