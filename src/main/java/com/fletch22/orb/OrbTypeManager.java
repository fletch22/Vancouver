package com.fletch22.orb;

import java.math.BigDecimal;
import java.util.LinkedHashSet;

import com.fletch22.orb.command.orbType.DeleteOrbTypeDto;
import com.fletch22.orb.command.orbType.dto.AddOrbTypeDto;
import com.fletch22.orb.rollback.UndoActionBundle;
import com.fletch22.util.json.LinkedHashSetString;

public interface OrbTypeManager {

	public void initializeOrbTypes();
	
	public long createOrbType(AddOrbTypeDto addOrbTypeDto, BigDecimal tranDate, final UndoActionBundle undoActionBundle);
	
	public long createOrbType(String label, long orbTypeInternalId, BigDecimal tranDate, LinkedHashSetString customFields);
	
	public long createOrbType(String label, LinkedHashSet<String> customFields);
	
	public void deleteOrbType(DeleteOrbTypeDto deleteOrbTypeDto, BigDecimal tranDate, UndoActionBundle rollbackAction);

	public void deleteAllTypes();
	
	public void addAttribute(long orbInternalId, String attributeName);
	
	public void deleteAttribute(long orbTypeInternalId, String attributeName);
	
	public void renameAttribute(long orbTypeInternalId, String attributeNameOld, String attributeNameNew);
	
	public void deleteOrbType(long orbTypeInternalId);

	public OrbType getOrbType(long orbTypeInternalId);
	
	public OrbType getOrbType(String label);
	
	public long getOrbTypeCount();

	int getIndexOfAttribute(long orbTypeInternalId, String attributeName);
	
	int getIndexOfAttribute(OrbType orbType, String attributeName);
}
