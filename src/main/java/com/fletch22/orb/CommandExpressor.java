package com.fletch22.orb;

import java.math.BigDecimal;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.rollback.UndoActionBundle;
import com.fletch22.util.JsonUtil;

@Component
public class CommandExpressor {

	public static final String ACTIONS_FETCHED_AND_PROCESSED = "actionsFetchedAndProcessed";
	public static final String ADD_DATA_LIMITATION = "addDataLimitation";
	public static final String ADD_ATTR_TO_TYPE_AT_ORD = "addAttrToTypeAtOrd";
	public static final String ADD_FOLDER_TO_FILESYSTEM = "addFolderToFileSystem";
	public static final String ADD_NAME_MAP_REF = "addNameMapRef";
	public static final String ADD_ORB_ATTRIBUTE_TO_TYPE = "addOrbAttributeToType";
	public static final String ADD_ORB = "addOrb";
	public static final String ADD_ORB_TYPE = "addOrbType";
	public static final String ALLOW_CASCADING_DELETES = "allowCascadingDeletes";
	public static final String ATTRIBUTE_NAME_MAP_KEY = "attributeNameMapKey";
	public static final String ATTRIBUTE_ALIAS = "attributeAlias";
	public static final String ATTRIBUTE_ALIAS_LIST = "attributeAliasList";
	public static final String ATTRIBUTE_NAME = "attributeName";
	public static final String ATTRIBUTE_ORDINAL = "attributeOrdinal";
	public static final String BACKUP_NAME = "backupName";
	public static final String BACKUP_AMOUNT_TYPE = "backupAmountType";
	public static final String BACKUP_DATABASE = "backupDatabase";
	public static final String BEGIN_TRANSACTION = "beginTransaction";
	public static final String BEGIN_TRANSACTION_INNER = "beginTransactionInner";
	public static final String BURN_AND_REPLANT_FROM_SQL = "burnAndReplantFromSql";
	public static final String BURN_AND_REPLANT_FROM_DISK = "burnAndReplantFromDisk";

	public static final String COLLECTOR_LABEL = "collectorLabel";
	public static final String COLUMNS_REQUESTED = "columnsRequested";
	public static final String COMMAND_BUNDLE = "commandBundle";
	public static final String COMMAND_COLLECTION = "commandCollection";
	public static final String COMMAND_LABEL = "command";
	public static final String COUNT = "count";
	public static final String COMMIT_TRANSACTION = "commitTransaction";
	public static final String COMMIT_TRANSACTION_WITH_ID = "commitTransactionWithId";
	public static final String CREATE_INSTANCES = "createInstances";
	public static final String CREATE_FILESYSTEM_FOLDER = "createFileSystemFolder";
	public static final String CREATE_NAMESPACE_FOLDER = "createNamespaceFolder";
	public static final String CREATE_SYSTEM_TYPES = "createSystemTypes";
	public static final String DOES_ORB_TYPE_EXIST = "doesOrbTypeExist";
	public static final String DATA_LIMITATION_ID = "dataLimitationId";
	public static final String DELETE_QUERY = "deleteQuery";
	public static final String DELETE_FOLDER_SPACE_FOLDER = "deleteFolderSpaceFolder";
	public static final String DELETE_FILESYSTEM_FOLDER = "deleteFileSystemFolder";
	public static final String DELETE_FILESYSTEM_FILE = "deleteFileSystemFile";
	public static final String DELETE_FILESYSTEM_ITEM = "deleteFileSystemItem";
	public static final String DELETE_FOLDER_IF_NOT_EMPTY = "deleteFolderIfNotEmpty";
	public static final String DISK_PERSIST_FULL_FOLDER_HOLDER_PATH = "diskPersistFullFolderPath";
	public static final String ECHO = "echo";
	public static final String EXCEPTION = "exception";
	public static final String EXCEPTION_TEXT = "exceptionText";
	public static final String EXCEPTION_REASON_CODE = "exceptionReasonCode";
	public static final String FILESYSTEM_COMMAND_IS_COPY = "fileSystemCommandIsCopy";
	public static final String FILESYSTEM_PATH = "fileSystemPath";
	public static final String FILESYSTEM_FOLDER_PATH = "folderPath";
	public static final String FILESYSTEM_ITEM_PATH = "fileSystemItemPath";
	public static final String FILESYSTEM_ITEM_NAME = "fileSystemItemName";
	public static final String FILESYSTEM_PATH_BASE = "fileSystemPathBase";
	public static final String FILESYSTEM_PATH_DESTINATION = "fileSystemPathDestination";
	public static final String FILESYSTEM_PATH_SOURCE = "fileSystemPathSource";
	public static final String FOLDER_NAME = "folderName";
	public static final String FOLDER_SPACE_FOLDER_NAME = "folderSpaceFolderName";
	public static final String FOLDER_TO_ADD_TO_FILESYSTEM = "folderToAddToFileSystem";

	public static final String GET_COUNT_ORB_TYPE_INST = "getCountOrbTypeInst";
	public static final String GET_ALL_DATA_LIMIT_FOR_ATTR = "getAllDataLimitations";
	public static final String GET_ALL_DATA_LIMIT_FOR_ORB = "getAllDataLimitsForOrb";
	public static final String GET_ALL_QUERY_NAMES = "getAllQueryNames";
	public static final String GET_ALL_SYSTEM_COLLECTORS = "getAllSystemCollectors";
	public static final String GET_CURRENT_TRANSACTION_ID = "getCurrentTransactionId";
	public static final String GET_FILE_INFORMATION = "getFileInformation";
	public static final String GET_FORM = "getForm";
	public static final String GET_NAMESPACE_FOLDER = "getNamespaceFolder";
	public static final String GET_NAMESPACE_FOLDER_PATH = "getNamespaceFolderPath";
	public static final String GET_NAMESPACE_FOLDER_CONTENTS = "getNamespaceFolderContents";
	public static final String GET_ORB = "getOrb";
	public static final String GET_ORB_INSTANCES = "getOrbInstances";
	public static final String GET_ORB_TYPE_FROM_LABEL = "getOrbTypeFromLabel";
	public static final String GET_ORB_INST_FROM_LABEL = "getOrbInstFromLabel";
	public static final String GET_ORB_TYPE = "getOrbType";
	public static final String GET_ORBS_TYPE = "getOrbsType";
	public static final String GET_LIST_OF_ORB_TYPES = "getListOfOrbTypes";
	public static final String GET_FILESYSTEM_ITEMS_IN_FOLDER = "getFileSystemItemsInFolder";
	public static final String GET_QUERY = "getQuery";
	public static final String GET_QUERY_BY_ID = "getQueryId";
	public static final String GET_QUERY_RESULTS = "getQueryResults";
	public static final String GET_QUERY_RESULTS_BY_ID = "getQueryResultsById";
	public static final String GET_SYSTEM_COLLECTOR_ATTRIBUTES = "getSystemCollectorAttributes";
	public static final String GET_STARTUP_USER_BACKUP_PATH = "getStartupLogPath";
	public static final String GET_TOTAL_ORB_COUNT = "getTotalOrbCount";

	public static final String ID_BEFORE_OPERATION = "idBeforeOperation";

	// public static final String INSTANCE_LABEL = "instanceLabel";
	public static final String KILL_TRANSACTION = "killTransaction";
	public static final String KEEP_HISTORY = "keepHistory";

	// Start Length Helper
	public static final String LENGTH_HELPER = "lengthHelper";
	public static final String LOG_BUNDLE = "logBundle";
	// End Length Helper

	public static final String METHOD_CALL = "methodCall";
	public static final String MOVE_FOLDER_SPACE_FOLDER = "moveFolderSpaceFolder";
	public static final String MOVE_WORKSPACE_ITEM = "moveWorkspaceItem";
	public static final String NEW_ATTRIBUTE_NAME = "newAttributeName";
	public static final String NAME_MAP_KEY = "nameMapKey";
	public static final String NAME_MAP_VALUE = "nameMapValue";
	public static final String NAMESPACE_FOLDER_PATH = "namespaceFolderPath";
	public static final String NAMESPACE_FOLDER_NAME = "namespaceFolderName";
	public static final String NUMBER_INSTANCES = "numberInstances";
	public static final String OLD_ATTRIBUTE_NAME = "oldAttributeName";
	public static final String OBJECT_IDENTIFIER = "objectIdentifier";
	public static final String OID_FOLDER_TO_MOVE = "orbInternalFolderToMove";
	public static final String OID_FOLDER_DESTINATION = "orbInternalIdFolderDestination";
	public static final String ORB_ADD_WHOLE = "orbAddWhole";
	public static final String RESTORE_ORB_WITH_NO_PROCESSING = "orbAddWholeWithSimpleProcessing";
	public static final String ORB_ATTR_NAME = "orbAttributeName";
	public static final String ORB_COLLECTOR_ID = "orbCollectorId";
	public static final String ORB_INST_ATTR_VALUE = "orbAttributeInstanceValue";
	public static final String ORB_INST_LABEL = "orbInstanceLabel";
	public static final String ORB_INTERNAL_ID = "orbInternalId";
	public static final String ORB_TRAN_DATE = "orbTranDate";
	public static final String ORB_TYPE = "orbType";
	public static final String ORB_TYPE_ID = "orbTypeId";
	public static final String ORB_TYPE_INTERNAL_ID = "orbTypeInternalId";
	public static final String ORB_TYPE_LABEL = "orbTypeLabel";
	public static final String ORB_TYPE_NAME = "orbTypeName";

	public static final String POPULATE_INSTANCES_WITH_DATA = "populateInstancesWithData";
	public static final String PATH_OFF_WORKSPACE_ROOT = "pathOffWorkspaceRoot";
	public static final String PING = "ping";
	public static final String QUERY_BODY = "queryBody";
	public static final String QUERY_COLLECTOR_ID = "queryCollectorId";
	public static final String QUERY_COLLECTOR_LABEL = "queryCollectorLabel";
	public static final String QUERY_DISPLAY_COLUMNS = "queryDisplayColumns";
	public static final String QUERY_CLAUSE_ATOM = "queryClauseAtom";
	public static final String QUERY_CLAUSE_GROUP_OPERATOR = "queryClauseGroupOperator";
	public static final String QUERY_EXPRESSION = "queryExpression";
	public static final String QUERY_ID = "queryId";
	public static final String QUERY_NAME = "queryName";
	public static final String QUERY_NAME_OLD = "oldQueryName";
	public static final String QUERY_SUBGROUP = "querySubGroup";
	public static final String QUERY_SUBCLAUSE = "querySubClause";

	public static final String RELATIVE_FILE_PATH = "relativeFilePath";
	public static final String RELATIVE_FOLDER_PATH = "relativeFolderPath";
	public static final String RENAME_FOLDER_SPACE_FOLDER = "renameFolderSpaceFolder";
	public static final String RENAME_FILESYSTEM_ITEM = "renameFileSystemItem";
	public static final String REMOVE_ORB_INSTANCE = "removeOrbInstance";
	public static final String REMOVE_ORB_TYPE = "removeOrbType";
	public static final String REMOVE_ORB_ATTRIBUTE_FROM_TYPE = "removeOrbAttributeFromType";
	public static final String REMOVE_ORB_DATA_LIMITS = "removeOrbDataLimitations";
	public static final String RENAME_ORB_TYPE_ATTRIBUTE = "renameOrbTypeAttribute";
	public static final String REMOVE_DATA_LIMITATION = "removeDataLimitation";
	public static final String RESOLVE_REFERENCES = "resolveReferences";
	public static final String RESULT_SET = "resultSet";
	public static final String RESULT_SET_HEADER = "resultSetHeader";
	public static final String ROW_SET = "rowSet";
	public static final String ROOT_LABEL = "command";
	public static final String ROLLBACK_TRANSACTION = "rollbackTransaction";

	public static final String SAVE_NEW_QUERY = "saveQuery";
	public static final String SET_CURRENT_ID = "setCurrentId";
	public static final String SET_ORB_INSTANCE_ATTR = "setOrbInstanceAttribute";
	public static final String SET_ORB_INSTANCE_ATTR_AS_REF = "setOrbInstanceAttributeAsRef";
	public static final String SET_ORB_INST_LABEL = "setOrbInstLabel";
	public static final String SET_ORB_TYPE_LABEL = "setOrbTypeLabel";
	public static final String SET_STARTUP_USER_BACKUP_PATH = "setStartupLogPath";
	public static final String SYSTEM_COMMAND = "systemCommand";
	public static final String SYSTEM_COMMAND_INIT_DATABASE = "initDatabase";
	public static final String TIME_SPAN_START = "timeSpanStart";
	public static final String TIME_SPAN_END = "timeSpanEnd";
	public static final String TRANSACTION = "transaction";
	public static final String TRANSACTION_ID = "transactionId";
	public static final String TRANSACTION_INACTIVITY_TIMEOUT = "transactionInactivityTimeout";
	public static final int TRANSACTION_INACTIVITY_TIMEOUT_USE_DEFAULT = -1;

	public static final String UPDATE_DATA_LIMITATION = "updateDataLimitation";
	public static final String UNDO_BEGIN_TRANSACTION_INNER_ID = "undoBeginTransactionInnerId";
	public static final String UNDO_COMMIT_TRANSACTION_INNER_ID = "undoCommitTransactionInnerId";
	public static final String UPDATE_QUERY = "updateQuery";
	public static final String UNDO_BUNDLE = "undoBundle";
	
	@Autowired
	JsonUtil jsonUtil;
	
	@Autowired
	OrbManager orbManager;
	
	@Autowired
	OrbTransformer orbTransformer;

	public StringBuilder getJsonPing() {
		StringBuilder translation = new StringBuilder();

		String payload = "asdfjk;asdfjkl;fsjdakl;jfdsk;ljkfd;jak;fdjsk;fjkds;jfkd;sljfkd;sajkf;dsajkf;dsjkf;ldjskl;fjdksal;fjkds;jfkds;jfkd;lsajfkdl;sa";

		translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");
		translation.append(CommandExpressor.PING);
		translation.append("\":\"");
		translation.append(jsonUtil.escapeJsonIllegals(payload));
		translation.append("}");

		return translation;
	}

	public StringBuilder getJsonCommandSetStartupUserBackupPath(String startupRelativeLogPath) {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");
		translation.append(CommandExpressor.SET_STARTUP_USER_BACKUP_PATH);
		translation.append("\":{\"");
		translation.append(RELATIVE_FILE_PATH);
		translation.append("\":\"");
		translation.append(this.jsonUtil.escapeJsonIllegals(startupRelativeLogPath));
		translation.append("\"}}");

		return translation;
	}

	public StringBuilder getJsonCommandCreateSystemTypes() {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");
		translation.append(CommandExpressor.CREATE_SYSTEM_TYPES);
		translation.append("\"}}");

		return translation;
	}

	public StringBuilder getJsonCommandJsonCommandCollection(LinkedList<UndoActionBundle> commandCollection) {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");

		UndoActionBundle[] rollbackActions = commandCollection.toArray(new UndoActionBundle[commandCollection.size()]); // (RollbackAction[])commandCollection.ToArray();

		translation.append(CommandExpressor.COMMAND_COLLECTION);
		translation.append("\":[");
		for (int i = 0; i < rollbackActions.length; i++) {
			translation.append(rollbackActions[i].toJson());
			if (i + 1 < commandCollection.size()) {
				translation.append(",");
			}
		}
		translation.append("]}}");

		return translation;
	}

	public StringBuilder getJsonCommandBeginTransactionInner() {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");
		translation.append(CommandExpressor.BEGIN_TRANSACTION_INNER);
		translation.append("\"}}");

		return translation;
	}

	public StringBuilder getJsonCommandOrbInstances(int orbTypeInternalId) {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(CommandExpressor.GET_ORB_INSTANCES);
		translation.append("\":{\"");
		translation.append(CommandExpressor.ORB_TYPE_INTERNAL_ID);
		translation.append("\":\"");
		translation.append(String.valueOf(orbTypeInternalId));
		translation.append("\"}}}");

		return translation;
	}

	public StringBuilder getJsonCommandSetCurrentId(int currentId) {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");
		translation.append(CommandExpressor.SET_CURRENT_ID);
		translation.append("\":\"");
		translation.append(currentId);
		translation.append("\"}}");

		return translation;
	}

	public StringBuilder getJsonTransactionCommandWrapper(BigDecimal transactionId, StringBuilder jsonCommandToWrap) {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"");
		translation.append(CommandExpressor.TRANSACTION);
		translation.append("\":[{\"");
		translation.append(CommandExpressor.TRANSACTION_ID);
		translation.append("\":\"");
		translation.append(transactionId);
		translation.append("\"},");
		translation.append(jsonCommandToWrap.toString().trim());
		translation.append("]}");

		return translation;
	}

	public StringBuilder getJsonCommandRollbackTransaction(BigDecimal transactionId) {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(CommandExpressor.ROLLBACK_TRANSACTION);
		translation.append("\":{\"");
		translation.append(CommandExpressor.TRANSACTION_ID);
		translation.append("\":\"");
		translation.append(transactionId);
		translation.append("\"}}}");

		return translation;
	}

	public StringBuilder getJsonCommandInitDatabase() {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"");
		translation.append(CommandExpressor.SYSTEM_COMMAND);
		translation.append("\":\"");
		translation.append(CommandExpressor.SYSTEM_COMMAND_INIT_DATABASE);
		translation.append("\"}");

		return translation;
	}

	public StringBuilder getJsonCommandGetAllSystemCollectors() {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");
		translation.append(CommandExpressor.GET_ALL_SYSTEM_COLLECTORS);
		translation.append("\"}}");

		return translation;
	}

	public StringBuilder getJsonCommandSystemCollectorAttributes(String collectorLabel) {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");
		translation.append(CommandExpressor.GET_SYSTEM_COLLECTOR_ATTRIBUTES);
		translation.append("\":{\"");
		translation.append(CommandExpressor.COLLECTOR_LABEL);
		translation.append("\":\"");
		translation.append(collectorLabel);
		translation.append("\"}}}");

		return translation;
	}

	public StringBuilder getJsonCommandGetQuery(String uniqueQueryLabel) {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");

		translation.append(CommandExpressor.GET_QUERY);
		translation.append("\":{\"");
		translation.append(QUERY_NAME);
		translation.append("\":\"");
		uniqueQueryLabel = this.jsonUtil.escapeJsonIllegals(uniqueQueryLabel);
		translation.append(uniqueQueryLabel);
		translation.append("\"}}}");

		return translation;
	}

	public StringBuilder getJsonCommandGetQueryById(int queryId) {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");

		translation.append(CommandExpressor.GET_QUERY_BY_ID);
		translation.append("\":{\"");
		translation.append(QUERY_ID);
		translation.append("\":\"");
		translation.append(queryId);
		translation.append("\"}}}");

		return translation;
	}

	public StringBuilder getJsonCommandRestoreOrbWithNoProcessing(Orb orb) {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");

		translation.append(CommandExpressor.RESTORE_ORB_WITH_NO_PROCESSING);
		translation.append("\":");
		translation.append(this.orbTransformer.convertToJson(orb));
		translation.append("}}");

		return translation;
	}

	public StringBuilder getJsonCommandBurnAndReplantFromSql(long start, long end) {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(BURN_AND_REPLANT_FROM_SQL);
		translation.append("\":[{\"");
		translation.append(TIME_SPAN_START);
		translation.append("\":\"");
		translation.append(start);
		translation.append("\"},{\"");
		translation.append(TIME_SPAN_END);
		translation.append("\":\"");
		translation.append(end);
		translation.append("\"}]}}");

		return translation;
	}

	public StringBuilder getJsonCommandAddOrb(int orbType) {
		StringBuilder translation = new StringBuilder();

		if (orbType == -1) {
			orbType = OrbTypeManager.ORBTYPE_BASETYPE_ID;
		}

		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(CommandExpressor.ADD_ORB);
		translation.append("\":[{\"");
		translation.append(ORB_TYPE);
		translation.append("\":\"");
		translation.append(String.valueOf(orbType));
		translation.append("\"}]}}");

		return translation;
	}

	public StringBuilder getJsonCommandGetOrb(int orbId, boolean resolveReferences) {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(GET_ORB);
		translation.append("\":[{\"");
		translation.append(ORB_INTERNAL_ID);
		translation.append("\":\"");
		translation.append(String.valueOf(orbId));
		translation.append("\"},{\"");
		translation.append(RESOLVE_REFERENCES);
		translation.append("\":\"");
		translation.append(resolveReferences);
		translation.append("\"}]}}");

		return translation;
	}

	public StringBuilder getJsonCommandGetTotalOrbCount() {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":\"");
		translation.append(GET_TOTAL_ORB_COUNT);
		translation.append("\"}");

		return translation;
	}

	public StringBuilder getJsonCommandGetCountOrbTypeInstances(int orbTypeId) {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(GET_COUNT_ORB_TYPE_INST);
		translation.append("\":{\"");
		translation.append(ORB_TYPE_ID);
		translation.append("\":\"");
		translation.append(String.valueOf(orbTypeId));
		translation.append("\"}}}");

		return translation;
	}

	public StringBuilder getJsonCommandAddAttrToOrbType(int orbInternalId, String attributeName) {
		StringBuilder translation = new StringBuilder();

		attributeName = this.jsonUtil.escapeJsonIllegals(attributeName);
		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(CommandExpressor.ADD_ORB_ATTRIBUTE_TO_TYPE);
		translation.append("\":[{\"");
		translation.append(ORB_INTERNAL_ID);
		translation.append("\":\"");
		translation.append(String.valueOf(orbInternalId));
		translation.append("\"},{\"");
		translation.append(ATTRIBUTE_NAME);
		translation.append("\":\"");
		translation.append(attributeName);
		translation.append("\"}]}}");

		return translation;
	}

	public StringBuilder getJsonCommandAddNameMapRef(String objectIdentifier, String key, String attributeName) {
		StringBuilder translation = new StringBuilder();
		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(CommandExpressor.ADD_NAME_MAP_REF);
		translation.append("\":[{\"");
		translation.append(OBJECT_IDENTIFIER);
		translation.append("\":\"");
		translation.append(objectIdentifier);
		translation.append("\"},{\"");
		translation.append(CommandExpressor.NAME_MAP_KEY);
		translation.append("\":\"");
		translation.append(key);
		translation.append("\"},{\"");
		translation.append(CommandExpressor.NAME_MAP_VALUE);
		translation.append("\":\"");
		translation.append(attributeName);

		translation.append("\"}]}}");

		return translation;
	}

	public StringBuilder getJsonCommandAddAttrToOrbType(int orbInternalId, String attributeName, int ordinal) {
		StringBuilder translation = new StringBuilder();

		attributeName = this.jsonUtil.escapeJsonIllegals(attributeName);
		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(CommandExpressor.ADD_ATTR_TO_TYPE_AT_ORD);
		translation.append("\":[{\"");
		translation.append(ORB_INTERNAL_ID);
		translation.append("\":\"");
		translation.append(String.valueOf(orbInternalId));
		translation.append("\"},{\"");
		translation.append(CommandExpressor.ATTRIBUTE_NAME);
		translation.append("\":\"");
		translation.append(attributeName);
		translation.append("\"},{\"");
		translation.append(CommandExpressor.ATTRIBUTE_ORDINAL);
		translation.append("\":\"");
		translation.append(String.valueOf(ordinal));
		translation.append("\"}]}}");

		return translation;
	}

	public StringBuilder getJsonCommandDoesOrbTypeExist(String orbTypeLabel) {
		StringBuilder translation = new StringBuilder();

		orbTypeLabel = this.jsonUtil.escapeJsonIllegals(orbTypeLabel);
		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(DOES_ORB_TYPE_EXIST);
		translation.append("\":{\"");
		translation.append(ORB_TYPE_LABEL);
		translation.append("\":\"");
		translation.append(orbTypeLabel);
		translation.append("\"}}}");

		return translation;
	}

	public StringBuilder getJsonCommandSetOrbAttrValue(int orbInternalId, String attributeName, String attributeValue) {
		StringBuilder translation = new StringBuilder();

		attributeName = this.jsonUtil.escapeJsonIllegals(attributeName);
		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(SET_ORB_INSTANCE_ATTR);
		translation.append("\":[{\"");
		translation.append(ORB_INTERNAL_ID);
		translation.append("\":\"");
		translation.append(String.valueOf(orbInternalId));
		translation.append("\"},{\"");
		translation.append(ORB_ATTR_NAME);
		translation.append("\":\"");
		translation.append(attributeName);
		translation.append("\"},{\"");
		translation.append(ORB_INST_ATTR_VALUE);
		translation.append("\":\"");
		translation.append(attributeValue + "\"}]}}");

		return translation;
	}

	public StringBuilder getJsonCommandRemoveOrbInstance(int orbInternalId) {
		StringBuilder translation = new StringBuilder();
		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(REMOVE_ORB_INSTANCE);
		translation.append("\":{\"");
		translation.append(ORB_INTERNAL_ID);
		translation.append("\":\"");
		translation.append(String.valueOf(orbInternalId) + "\"}}}");

		return translation;
	}

	public StringBuilder getJsonCommandRemoveOrbTypeAttribute(int orbTypeInternalId, String orbAttributeName) {
		StringBuilder translation = new StringBuilder();

		orbAttributeName = this.jsonUtil.escapeJsonIllegals(orbAttributeName);
		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(REMOVE_ORB_ATTRIBUTE_FROM_TYPE);
		translation.append("\":[{\"");
		translation.append(ORB_TYPE_ID);
		translation.append("\":\"");
		translation.append(String.valueOf(orbTypeInternalId));
		translation.append("\"},{\"");
		translation.append(ORB_ATTR_NAME);
		translation.append("\":\"");
		translation.append(orbAttributeName);
		translation.append("\"}]}}");

		return translation;
	}

	public StringBuilder getJsonCommandRenameOrbTypeAttribute(int orbTypeInternalId, String oldName, String newName) {
		StringBuilder translation = new StringBuilder();

		oldName = this.jsonUtil.escapeJsonIllegals(oldName);
		newName = this.jsonUtil.escapeJsonIllegals(newName);
		translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"" + RENAME_ORB_TYPE_ATTRIBUTE + "\":{\"" + ORB_INTERNAL_ID + "\":\"" + orbTypeInternalId + "\",\"" + OLD_ATTRIBUTE_NAME + "\":\"" + oldName + "\",\""
				+ NEW_ATTRIBUTE_NAME + "\":\"" + newName + "\"}}}");

		return translation;
	}

	public StringBuilder getJsonCommandCreateOrbInstances(int orbTypeInternalId, int numberOfInstances, boolean populateInstancesWithData) {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(CommandExpressor.CREATE_INSTANCES);
		translation.append("\":[{\"");
		translation.append(CommandExpressor.ORB_TYPE_INTERNAL_ID);
		translation.append("\":\"");
		translation.append(orbTypeInternalId);
		translation.append("\"},{\"");
		translation.append(CommandExpressor.NUMBER_INSTANCES);
		translation.append("\":\"");
		translation.append(String.valueOf(numberOfInstances));
		translation.append("\"},{\"");
		translation.append(CommandExpressor.POPULATE_INSTANCES_WITH_DATA);
		translation.append("\":\"");
		translation.append(String.valueOf(populateInstancesWithData));
		translation.append("\"}]}}");

		return translation;
	}

	public StringBuilder echo(String StringToEcho) {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"" + ECHO + "\":\"" + StringToEcho + "\"}}");

		return translation;
	}

	public StringBuilder getJsonCommandGetOrbsType(int orbInternalId) {
		StringBuilder translation = new StringBuilder();

		String oidClean = this.jsonUtil.escapeJsonIllegals(String.valueOf(orbInternalId));

		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(GET_ORBS_TYPE);
		translation.append("\":{\"");
		translation.append(ORB_INTERNAL_ID);
		translation.append("\":\"");
		translation.append(oidClean);
		translation.append("\"}}}");

		return translation;
	}

	// TODO: This should not be exposed as part of the "public" API.
	public StringBuilder getJsonCommandUndoBeginInnerTransaction(BigDecimal transactionIdInner) {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(CommandExpressor.UNDO_BEGIN_TRANSACTION_INNER_ID);
		translation.append("\":\"");
		translation.append(String.valueOf(transactionIdInner));
		translation.append("\"}}");

		return translation;
	}

	// TODO: This should not be exposed as part of the "public" API.
	public StringBuilder getJsonCommandUndoCommitInnerTransaction(BigDecimal transactionIdInner) {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(CommandExpressor.UNDO_COMMIT_TRANSACTION_INNER_ID);
		translation.append("\":\"");
		translation.append(String.valueOf(transactionIdInner));
		translation.append("\"}}");

		return translation;
	}

	public StringBuilder getJsonCommandSetOrbInstLabel(int orbInternalId, String label) {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(CommandExpressor.SET_ORB_INST_LABEL);
		translation.append("\":[{\"");
		translation.append(CommandExpressor.ORB_INTERNAL_ID);
		translation.append("\":\"");
		translation.append(String.valueOf(orbInternalId));
		translation.append("\"},{\"");
		translation.append(CommandExpressor.ORB_INST_LABEL);
		translation.append("\":\"");
		translation.append(this.jsonUtil.escapeJsonIllegals(label));
		translation.append("\"}]}}");

		return translation;
	}

	public StringBuilder getJsonCommandGetOrbInstFromLabel(String label) {
		StringBuilder sb = new StringBuilder();

		label = this.jsonUtil.escapeJsonIllegals(label);
		sb.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"" + CommandExpressor.GET_ORB_INST_FROM_LABEL + "\":{\"" + CommandExpressor.ORB_INST_LABEL + "\":\"" + label + "\"}}}");

		return sb;
	}
	
	// public StringBuilder getJsonGetMethodCall(OrbDaemonMethodCall methodCall)
	// {
	// StringBuilder translation = new StringBuilder();
	//
	// translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");
	// translation.append(CommandExpressor.METHOD_CALL);
	// translation.append("\":");
	// translation.append(methodCall.toJson());
	// translation.append("}");
	//
	// return translation;
	// }
//		public StringBuilder getJsonCommandGetStartupUserBackupPath() {
//		StringBuilder translation = new StringBuilder();
	//
//		translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");
//		translation.append(CommandExpressor.GET_STARTUP_USER_BACKUP_PATH);
//		translation.append("\"}}");
	//
//		return translation;
	//}
//		public StringBuilder getJsonCommandCreateNamespaceFolder(String path, String folderName) {
//		StringBuilder translation = new StringBuilder();
	//
//		translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");
//		translation.append(CommandExpressor.CREATE_NAMESPACE_FOLDER);
//		translation.append("\":[{\"");
//		translation.append(CommandExpressor.NAMESPACE_FOLDER_PATH);
//		translation.append("\":\"");
//		translation.append(this.jsonUtil.escapeJsonIllegals(path));
//		translation.append("\"},{\"");
//		translation.append(CommandExpressor.NAMESPACE_FOLDER_NAME);
//		translation.append("\":\"");
//		translation.append(this.jsonUtil.escapeJsonIllegals(folderName));
//		translation.append("\"}]}}");
	//
//		return translation;
	//}

	// public StringBuilder
	// getJsonCommandGetFileInformation(CommonDaemon.FileSystemOps.FolderContents.FileSystemBase
	// fileSystemBase, String relativeFilePath)
	// {
	// StringBuilder translation = new StringBuilder();
	//
	// translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");
	// translation.append(CommandExpressor.GET_FILE_INFORMATION);
	// translation.append("\":{\"");
	// translation.append(CommandExpressor.FILESYSTEM_PATH_BASE);
	// translation.append("\":\"");
	// translation.append(fileSystemBase.ToString());
	// translation.append("\",\"");
	// translation.append(CommandExpressor.FILESYSTEM_ITEM_PATH);
	// translation.append("\":\"");
	// translation.append(this.jsonUtil.escapeJsonIllegals(relativeFilePath));
	// translation.append("\"}}}");
	//
	// return translation;
	// }

	// public StringBuilder getJsonCommandBackupDatabase(String backupName,
	// String relativeFolderPath, BackupAmountType backupAmountType)
	// {
	// StringBuilder translation = new StringBuilder();
	//
	// translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");
	// translation.append(CommandExpressor.BACKUP_DATABASE);
	// translation.append("\":{\"");
	// translation.append(CommandExpressor.RELATIVE_FOLDER_PATH);
	// translation.append("\":\"");
	// translation.append(this.jsonUtil.escapeJsonIllegals(relativeFolderPath));
	// translation.append("\",\"");
	// translation.append(CommandExpressor.BACKUP_NAME);
	// translation.append("\":\"");
	// translation.append(this.jsonUtil.escapeJsonIllegals(backupName));
	// translation.append("\",\"");
	// translation.append(CommandExpressor.BACKUP_AMOUNT_TYPE);
	// translation.append("\":\"");
	// translation.append(backupAmountType.ToString());
	// translation.append("\"}}");
	//
	// return translation;
	// }

	// public StringBuilder
	// getJsonCommandDeleteFileSystemItem(CommonDaemon.FileSystemOps.FolderContents.FileSystemBase
	// fileSystemBase, String pathOffWorkspace)
	// {
	// StringBuilder translation = new StringBuilder();
	// translation.append("{\"");
	// translation.append(CommandExpressor.ROOT_LABEL);
	// translation.append("\":{\"");
	// translation.append(CommandExpressor.DELETE_FILESYSTEM_ITEM);
	// translation.append("\":{\"");
	// translation.append(FILESYSTEM_PATH);
	// translation.append("\":\"");
	// translation.append(this.jsonUtil.escapeJsonIllegals(pathOffWorkspace));
	// translation.append("\",\"");
	// translation.append(CommandExpressor.FILESYSTEM_PATH_BASE);
	// translation.append("\":\"");
	// translation.append(fileSystemBase.ToString());
	// translation.append("\"}}}");
	//
	// return translation;
	// }
//		public StringBuilder getJsonCommandGetItemsInFolder(String pathOffWorkspaceRoot, CommonDaemon.FileSystemOps.FolderContents.FileSystemBase fileSystemFolderType) {
//			StringBuilder translation = new StringBuilder();
	//
//			translation.append("{\"");
//			translation.append(CommandExpressor.ROOT_LABEL);
//			translation.append("\":{\"");
//			translation.append(CommandExpressor.GET_FILESYSTEM_ITEMS_IN_FOLDER);
//			translation.append("\":{\"");
//			translation.append(CommandExpressor.FILESYSTEM_FOLDER_PATH);
//			translation.append("\":\"");
//			translation.append(this.jsonUtil.escapeJsonIllegals(pathOffWorkspaceRoot));
//			translation.append("\",\"");
//			translation.append(CommandExpressor.FILESYSTEM_PATH_BASE);
//			translation.append("\":\"");
//			translation.append(fileSystemFolderType.ToString());
//			translation.append("\"}}}");
	//
//			return translation;
//		}

//		public StringBuilder getJsonCommandCreateWorkspaceFolder(CommonDaemon.FileSystemOps.FolderContents.FileSystemBase fileSystemBase, String pathOffWorkspaceRoot, String addedFolderName) {
//			StringBuilder translation = new StringBuilder();
	//
//			translation.append("{\"");
//			translation.append(CommandExpressor.ROOT_LABEL);
//			translation.append("\":{\"");
//			translation.append(CommandExpressor.ADD_FOLDER_TO_FILESYSTEM);
//			translation.append("\":[{\"");
//			translation.append(CommandExpressor.PATH_OFF_WORKSPACE_ROOT);
//			translation.append("\":\"");
//			translation.append(this.jsonUtil.escapeJsonIllegals(pathOffWorkspaceRoot));
//			translation.append("\"},{\"");
//			translation.append(CommandExpressor.FOLDER_TO_ADD_TO_FILESYSTEM);
//			translation.append("\":\"");
//			translation.append(this.jsonUtil.escapeJsonIllegals(addedFolderName));
//			translation.append("\"},{\"");
//			translation.append(CommandExpressor.FILESYSTEM_PATH_BASE);
//			translation.append("\":\"");
//			translation.append(fileSystemBase.ToString());
//			translation.append("\"}]}}");
	//
//			return translation;
//		}

//		public StringBuilder getJsonCommandMoveWorkspaceItem(CommonDaemon.FileSystemOps.FolderContents.FileSystemBase fileSystemBase, String pathToMove, String pathDestination, boolean isCopy) {
//			StringBuilder translation = new StringBuilder();
	//
//			translation.append("{\"");
//			translation.append(CommandExpressor.ROOT_LABEL);
//			translation.append("\":{\"");
//			translation.append(CommandExpressor.MOVE_WORKSPACE_ITEM);
//			translation.append("\":{\"");
//			translation.append(CommandExpressor.FILESYSTEM_PATH_SOURCE);
//			translation.append("\":\"");
//			translation.append(this.jsonUtil.escapeJsonIllegals(pathToMove));
//			translation.append("\",\"");
//			translation.append(FILESYSTEM_PATH_DESTINATION);
//			translation.append("\":\"");
//			translation.append(this.jsonUtil.escapeJsonIllegals(pathDestination));
//			translation.append("\",\"");
//			translation.append(FILESYSTEM_COMMAND_IS_COPY);
//			translation.append("\":\"");
//			translation.append(isCopy.ToString());
//			translation.append("\",\"");
//			translation.append(CommandExpressor.FILESYSTEM_PATH_BASE);
//			translation.append("\":\"");
//			translation.append(fileSystemBase.ToString());
//			translation.append("\"}}}");
	//
//			return translation;
//		}
	//	
//		public StringBuilder getJsonCommandDeleteFileSystemFile(String filePathOffWorkspace) {
//			StringBuilder translation = new StringBuilder();
//			translation.append("{\"");
//			translation.append(CommandExpressor.ROOT_LABEL);
//			translation.append("\":{\"");
//			translation.append(CommandExpressor.DELETE_FILESYSTEM_FILE);
//			translation.append("\":{\"");
//			translation.append(FILESYSTEM_PATH);
//			translation.append("\":\"");
//			translation.append(this.jsonUtil.escapeJsonIllegals(filePathOffWorkspace));
//			translation.append("\"}}}");
	//
//			return translation;
//		}
	//
//		public StringBuilder getJsonCommandGetNamespaceFolder(String path) {
//			StringBuilder translation = new StringBuilder();
	//
//			translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");
//			translation.append(CommandExpressor.GET_NAMESPACE_FOLDER);
//			translation.append("\":{\"");
//			translation.append(CommandExpressor.NAMESPACE_FOLDER_PATH);
//			translation.append("\":\"");
//			translation.append(this.jsonUtil.escapeJsonIllegals(path));
//			translation.append("\"}}}");
	//
//			return translation;
//		}
	//
//		public StringBuilder getJsonCommandGetNamespaceFolderPath(int oidFolder) {
//			StringBuilder translation = new StringBuilder();
	//
//			translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");
//			translation.append(CommandExpressor.GET_NAMESPACE_FOLDER);
//			translation.append("\":{\"");
//			translation.append(CommandExpressor.ORB_INTERNAL_ID);
//			translation.append("\":\"");
//			translation.append(String.valueOf(oidFolder));
//			translation.append("\"}}}");
//			return translation;
//		}
	//
//		public StringBuilder getJsonCommandGetNamespaceFolderContents(String path) {
//			StringBuilder translation = new StringBuilder();
//			translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");
//			translation.append(CommandExpressor.GET_NAMESPACE_FOLDER_CONTENTS);
//			translation.append("\":\"");
//			translation.append(this.jsonUtil.escapeJsonIllegals(path));
//			translation.append("\"}}");
//			return translation;
//		}
		
//		public StringBuilder getJsonCommandDeleteQuery(String queryName) {
//		StringBuilder translation = new StringBuilder();
	//
//		translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");
	//
//		translation.append(CommandExpressor.DELETE_QUERY);
//		translation.append("\":{\"");
//		translation.append(QUERY_NAME);
//		translation.append("\":\"");
//		queryName = this.jsonUtil.escapeJsonIllegals(queryName);
//		translation.append(queryName);
//		translation.append("\"}}}");
	//
//		return translation;
	//}
	//	
//		public StringBuilder getJsonCommandGetAllQueryNames() {
//			StringBuilder translation = new StringBuilder();
	//
//			translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");
	//
//			translation.append(CommandExpressor.GET_ALL_QUERY_NAMES);
//			translation.append("\"}}");
	//
//			return translation;
//		}

//		public StringBuilder getJsonCommandUpdateQuery(String oldQueryName, CommonDaemon.Database.Query.Query query) {
//			StringBuilder translation = new StringBuilder();
	//
//			translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");
	//
//			translation.append(CommandExpressor.UPDATE_QUERY);
//			translation.append("\":[{\"");
//			translation.append(QUERY_NAME_OLD);
//			translation.append("\":\"");
//			oldQueryName = this.jsonUtil.escapeJsonIllegals(oldQueryName);
//			translation.append(oldQueryName);
//			translation.append("\"},{\"");
//			translation.append(QUERY_BODY);
//			translation.append("\":");
//			translation.append(query.toJson());
//			translation.append("}]}}");
	//
//			return translation;
//		}

//		public StringBuilder getJsonCommandSaveNewQuery(CommonDaemon.Database.Query.Query query) {
//			StringBuilder translation = new StringBuilder();
	//
//			translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"");
	//
//			translation.append(CommandExpressor.SAVE_NEW_QUERY);
//			translation.append("\":{\"");
//			translation.append(QUERY_BODY);
//			translation.append("\":");
//			translation.append(query.toJson());
//			translation.append("}}}");
	//
//			return translation;
//		}
		
//		public StringBuilder getJsonCommandSetOrbAttrValueAsRef(int orbInternalIdArrow, String attributeNameArrow, int orbInternalIdReferenceTarget) {
//		return getJsonCommandSetOrbAttrValue(orbInternalIdArrow, attributeNameArrow, OrbReference.composeReferenceKey(orbInternalIdReferenceTarget));
	//}
	//
	//public StringBuilder getJsonCommandSetOrbAttrValueAsRef(int orbInternalIdArrow, String attributeNameArrow, Orb orbReferenceTarget) {
//		return getJsonCommandSetOrbAttrValue(orbInternalIdArrow, attributeNameArrow, OrbReference.composeReferenceKey(orbReferenceTarget.internalId));
	//}
	//
	//public StringBuilder getJsonCommandSetOrbAttrValueAsRef(int orbInternalId, String attributeName, Orb orb, String orbAttributeName) {
//		return getJsonCommandSetOrbAttrValue(orbInternalId, attributeName, OrbReference.composeReferenceKey(orb.internalId, orbAttributeName));
	//}
	//
	//public StringBuilder getJsonCommandSetOrbAttrValueAsRef(int orbInternalId, String attributeName, CommonDaemon.Database.Query.Query query) {
//		return getJsonCommandSetOrbAttrValue(orbInternalId, attributeName, QueryReference.composeReferenceKey(query));
	//}
	//
	//public StringBuilder getJsonCommandSetOrbAttrValueAsRef(int orbInternalId, String attributeName, CommonDaemon.Database.Query.Query query, String queryAttributeName) {
//		return getJsonCommandSetOrbAttrValue(orbInternalId, attributeName, QueryReference.composeReferenceKey(query, queryAttributeName));
	//}

	//public StringBuilder getJsonCommandRenameFileSystemItem(CommonDaemon.FileSystemOps.FolderContents.FileSystemBase fileSystemBase, String relativePath, String newName) {
//		StringBuilder translation = new StringBuilder();
//		translation.append("{\"");
//		translation.append(CommandExpressor.ROOT_LABEL);
//		translation.append("\":{\"");
//		translation.append(CommandExpressor.RENAME_FILESYSTEM_ITEM);
//		translation.append("\":{\"");
//		translation.append(FILESYSTEM_ITEM_PATH);
//		translation.append("\":\"");
//		translation.append(this.jsonUtil.escapeJsonIllegals(relativePath));
//		translation.append("\",\"");
//		translation.append(FILESYSTEM_ITEM_NAME);
//		translation.append("\":\"");
//		translation.append(newName);
//		translation.append("\",\"");
//		translation.append(CommandExpressor.FILESYSTEM_PATH_BASE);
//		translation.append("\":\"");
//		translation.append(fileSystemBase.ToString());
//		translation.append("\"}}}");
	//
//		return translation;
	//}
	//	
//		public StringBuilder getJsonCommandDeleteNameSpaceFolder(int oidFolder) {
//			StringBuilder translation = new StringBuilder();
//			translation.append("{\"");
//			translation.append(CommandExpressor.ROOT_LABEL);
//			translation.append("\":{\"");
//			translation.append(DELETE_FOLDER_SPACE_FOLDER);
//			translation.append("\":{\"");
//			translation.append(ORB_INTERNAL_ID);
//			translation.append("\":\"");
//			translation.append(String.valueOf(oidFolder));
//			translation.append("\"}}}");
	//
//			return translation;
//		}
	//
//		public StringBuilder getJsonCommandMoveNameSpaceFolder(int oidFolderToMove, int oidFolderDestination) {
//			StringBuilder translation = new StringBuilder();
//			translation.append("{\"");
//			translation.append(CommandExpressor.ROOT_LABEL);
//			translation.append("\":{\"");
//			translation.append(MOVE_FOLDER_SPACE_FOLDER);
//			translation.append("\":[{\"");
//			translation.append(OID_FOLDER_TO_MOVE);
//			translation.append("\":\"");
//			translation.append(String.valueOf(oidFolderToMove));
//			translation.append("\"},{\"");
//			translation.append(OID_FOLDER_DESTINATION);
//			translation.append("\":\"");
//			translation.append(String.valueOf(oidFolderDestination));
//			translation.append("\"}]}}");
	//
//			return translation;
//		}
//		public StringBuilder getJsonCommandRemoveOrbDls(int orbInternalId) {
//			StringBuilder translation = new StringBuilder();
	//
//			translation.append("{\"");
//			translation.append(CommandExpressor.ROOT_LABEL);
//			translation.append("\":{\"");
	//
//			translation.append(CommandExpressor.REMOVE_ORB_DATA_LIMITS);
//			translation.append("\":{\"");
//			translation.append(ORB_INTERNAL_ID);
//			translation.append("\":\"");
//			translation.append(String.valueOf(orbInternalId));
//			translation.append("\"}");
//			translation.append("\"}}");
	//
//			return translation;
//		}
//		public StringBuilder getJsonCommandBurnAndReplantFromDisk(String relativeFilePath) {
//		StringBuilder translation = new StringBuilder();
	//
//		translation.append("{\"");
//		translation.append(CommandExpressor.ROOT_LABEL);
//		translation.append("\":{\"");
//		translation.append(CommandExpressor.BURN_AND_REPLANT_FROM_DISK);
//		translation.append("\":{\"");
//		translation.append(CommandExpressor.RELATIVE_FILE_PATH);
//		translation.append("\":\"");
//		translation.append(this.jsonUtil.escapeJsonIllegals(relativeFilePath));
//		translation.append("\"}}}");
	//
//		return translation;
	//}

//		public StringBuilder getJsonCommandGetQueryResults(CommonDaemon.Database.Query.Query query) {
//			StringBuilder translation = new StringBuilder();
	//
//			String jsonQuery = query.toJson().ToString();
	//
//			translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"" + GET_QUERY_RESULTS + "\":" + jsonQuery + "}}");
	//
//			return translation;
//		}
	//
//		public StringBuilder getJsonCommandGetQueryResults(int queryId) {
//			StringBuilder translation = new StringBuilder();
	//
//			translation.append("{\"");
//			translation.append(CommandExpressor.ROOT_LABEL);
//			translation.append("\":{\"");
//			translation.append(GET_QUERY_RESULTS_BY_ID);
//			translation.append("\":{\"");
//			translation.append(QUERY_ID);
//			translation.append("\":\"");
//			translation.append(String.valueOf(queryId));
//			translation.append("\"}}}");
	//
//			return translation;
//		}
	//
//		public StringBuilder getJsonCommandGetDataLimitsForAttr(int orbInternalId, String attributeName) {
//			StringBuilder translation = new StringBuilder();
	//
//			attributeName = this.jsonUtil.escapeJsonIllegals(attributeName);
//			translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"" + GET_ALL_DATA_LIMIT_FOR_ATTR + "\":[{\"" + ORB_INTERNAL_ID + "\":\"" + orbInternalId + "\"},{\"" + CommandExpressor.ORB_ATTR_NAME + "\":\"" + attributeName
//					+ "\"}] }}");
	//
//			return translation;
//		}
	//
//		public StringBuilder getJsonCommandGetDataLimitsForOrb(int orbInternalId) {
//			StringBuilder translation = new StringBuilder();
	//
//			translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"" + GET_ALL_DATA_LIMIT_FOR_ORB + "\":{\"" + ORB_INTERNAL_ID + "\":\"" + String.valueOf(orbInternalId) + "\"}}}");
	//
//			return translation;
//		}

	// public StringBuilder getJsonCommandAddDataLimitation(DataLimitation dl)
	// {
	// StringBuilder translation = new StringBuilder();
	//
	// translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"" +
	// ADD_DATA_LIMITATION + "\":" + dl.toJson() + "}}");
	//
	// return translation;
	// }
//		public StringBuilder getJsonCommandRenameNameSpaceFolder(int oidFolder, String newFolderName) {
//		StringBuilder translation = new StringBuilder();
//		translation.append("{\"");
//		translation.append(CommandExpressor.ROOT_LABEL);
//		translation.append("\":{\"");
//		translation.append(RENAME_FOLDER_SPACE_FOLDER);
//		translation.append("\":[{\"");
//		translation.append(ORB_INTERNAL_ID);
//		translation.append("\":\"");
//		translation.append(String.valueOf(oidFolder));
//		translation.append("\"},{\"");
//		translation.append(FOLDER_SPACE_FOLDER_NAME);
//		translation.append("\":\"");
//		translation.append(this.jsonUtil.escapeJsonIllegals(newFolderName));
//		translation.append("\"}]}}");
	//
//		return translation;
	//}
	// public StringBuilder getJsonCommandUpdateDataLimitation(DataLimitation
	// dl)
	// {
	// StringBuilder translation = new StringBuilder();
	//
	// translation.append("{\"" + CommandExpressor.ROOT_LABEL + "\":{\"" +
	// UPDATE_DATA_LIMITATION + "\":" + dl.toJson() + "}}");
	//
	// return translation;
	// }
	//public StringBuilder getJsonCommandRemoveDataLimitation(String dataLimitationId) {
//		StringBuilder translation = new StringBuilder();
	//
//		translation.append("{\"");
//		translation.append(CommandExpressor.ROOT_LABEL);
//		translation.append("\":{\"");
//		translation.append(CommandExpressor.REMOVE_DATA_LIMITATION);
//		translation.append("\":{\"");
//		translation.append(CommandExpressor.DATA_LIMITATION_ID);
//		translation.append("\":\"");
//		translation.append(dataLimitationId);
//		translation.append("\"}}}");
	//
//		return translation;
	//}
}
