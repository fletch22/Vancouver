package com.fletch22.dao;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BackupSerializer {
	
	public static final String PROP_ACTION = "action";
	public static final String PROP_UNDO_ACTION = "undoAction";
	public static final String PROP_TRAN_DATE = "tranDate";
	public static final String PROP_TRAN_ID = "tranId";

	@Autowired
	BackupFileCoder backupFileCoder;

	public StringBuilder serializeRecord(ActionUndoInfo actionUndoInfo) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb = appendPropPrefix(sb, PROP_ACTION, actionUndoInfo.action, false);
		sb.append(", ");
		sb = appendPropPrefix(sb, PROP_UNDO_ACTION, actionUndoInfo.undoAction, false);
		sb.append(", ");
		sb = appendPropPrefix(sb, PROP_TRAN_DATE, actionUndoInfo.tranDate, true);
		sb.append(", ");
		sb = appendPropPrefix(sb, PROP_TRAN_ID, actionUndoInfo.tranId, true);
		sb.append("}");
		
		sb = backupFileCoder.encode(sb);
		
		return sb;
	}

	public StringBuilder appendPropPrefix(StringBuilder sb, String propertyName, StringBuilder propertyValue, boolean wrapPropValInQuotes) {
		sb.append("\"");
		sb.append(propertyName);
		sb.append("\":");
		if (wrapPropValInQuotes) {
			sb.append("\"");
		}		
		sb.append(propertyValue);
		if (wrapPropValInQuotes) {
			sb.append("\"");
		}
		return sb;
	}

	public StringBuilder appendPropPrefix(StringBuilder sb, String propertyName, BigDecimal propertyValue, boolean wrapPropValInQuotes) {
		return appendPropPrefix(sb, propertyName, new StringBuilder(propertyValue.toString()), wrapPropValInQuotes);
	}
}
