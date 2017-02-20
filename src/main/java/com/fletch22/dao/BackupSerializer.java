package com.fletch22.dao;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.util.json.GsonFactory;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Component
public class BackupSerializer {
	
	private static final Logger logger = LoggerFactory.getLogger(BackupSerializer.class);
	
	public static final String PROP_ACTION = "action";
	public static final String PROP_UNDO_ACTION = "undoAction";
	public static final String PROP_TRAN_DATE = "tranDate";
	public static final String PROP_TRAN_ID = "tranId";

	@Autowired
	BackupFileCoder backupFileCoder;
	
	@Autowired
	GsonFactory gsonFactory;

	public StringBuilder serializeRecord(ActionUndoInfo actionUndoInfo) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb = appendPropPrefix(sb, PROP_ACTION, actionUndoInfo.action, false);
		sb.append(",");
		sb = appendPropPrefix(sb, PROP_UNDO_ACTION, actionUndoInfo.undoAction, false);
		sb.append(",");
		sb = appendPropPrefix(sb, PROP_TRAN_DATE, actionUndoInfo.tranDate, true);
		sb.append(",");
		sb = appendPropPrefix(sb, PROP_TRAN_ID, actionUndoInfo.tranId, true);
		sb.append("}");
		
		sb = backupFileCoder.encode(sb);
		
		return sb;
	}
	
	public ActionUndoInfo deserializeRecord(StringBuilder record) {
		ActionUndoInfo actionUndoInfo = new ActionUndoInfo();
			
		record = backupFileCoder.decode(record);
		
		JsonElement jelement = new JsonParser().parse(record.toString());
	    JsonObject  jobject = jelement.getAsJsonObject();
	    
	    actionUndoInfo.action = new StringBuilder(jobject.get(PROP_ACTION).toString());
	    
	    actionUndoInfo.tranDate = jobject.get(PROP_TRAN_DATE).getAsBigDecimal();
	    actionUndoInfo.undoAction = new StringBuilder(jobject.get(PROP_UNDO_ACTION).toString());
	    actionUndoInfo.tranId = jobject.get(PROP_TRAN_ID).getAsBigDecimal();
		
		return actionUndoInfo;
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
