package com.fletch22.dao.inmem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

public class SqlStatementStore {
	
	@Value("${h2.database.rootScriptPath}")
	private String rootScriptPath;
	
	Map<String, StringBuilder> sqlScripts = new HashMap<String, StringBuilder>();

	public String[] getSqlLines(String filePath) {
		
		filePath = this.rootScriptPath + "/" + filePath; 

		StringBuilder sb = null;
		if (sqlScripts.containsKey(filePath)) {
			sb = sqlScripts.get(filePath);
		} else {
			sb = getScriptLines(filePath);
			sqlScripts.put(filePath, sb);
		}

		// Note: Here is our splitter! We use ";" as a delimiter for each request
		// then we are sure to have well formed statements
		return sb.toString().split(";");
	}
	
	private StringBuilder getScriptLines(String filePath) {

		StringBuilder sb = new StringBuilder();
		FileReader fileReader = null;
		try {
			String s;
			URL resource = this.getClass().getResource(filePath);

			if (resource == null) {
				throw new RuntimeException("Could not find the resource at '" + rootScriptPath + "'.");
			}

			File file = Paths.get(resource.toURI()).toFile();
			fileReader = new FileReader(file);

			// be sure to not have line starting with "--" or "/*" or any other
			// non alphabetical character
			BufferedReader br = new BufferedReader(fileReader);

			while ((s = br.readLine()) != null) {
				sb.append(s);
			}
			br.close();

		} catch (Exception e) {
			throw new RuntimeException("Encountered a problem while opening the initial script path.", e);
		} finally {
			if (fileReader != null) {
				try {
					fileReader.close();
				} catch (IOException e) {
					throw new RuntimeException("Encountered error while trying to close file reader.", e);
				}
			}
		}
		return sb;
	}
	
	public String getSingleLineFromFile(String filePath) {
		String[] sqlLines = getSqlLines(filePath);

		if (sqlLines.length > 1) {
			throw new RuntimeException("Encountered problem while trying to get sql. More than one line of sql was encountered. This was not expected.");
		}

		return sqlLines[0];
	}

	public String getInsertIntoActionLog() {
		return getSingleLineFromFile("logAction5_insertIntoActionLog.sql");
	}

	public String getInsertIntoUndoActionLog() {
		return getSingleLineFromFile("logAction5_insertIntoUndoActionLog.sql");
	}
	
	public String getSelectUndoSql() {
		return getSingleLineFromFile("getTransactionAndSubsequentUndos2.sql");
	}
	
	public String[] getInitializeDatabaseSql() {
		return getSqlLines("initializeDatabase.sql");
	}
	
	public String[] getClearOutDatabaseSql() {
		return getSqlLines("blowAwayActionTable5.sql");
	}
	
	public String getAnyOrphanedTransactionsSql() {
		return getSingleLineFromFile("getAnyOrphanedTransactions2.sql");
	}
	
	public String getCountLogItemsSql() {
		return getSingleLineFromFile("countLogItems.sql");
	}

	public String getLogSql() {
		return getSingleLineFromFile("getLog.sql");
	}
	
	public String getDeleteUndoLogSql() {
		return getSingleLineFromFile("removeTransactionAndAllAfter6_UndoActionLog.sql");
	}
	
	public String getDeleteActionLogSql() {
		return getSingleLineFromFile("removeTransactionAndAllAfter6_ActionLog.sql");
	}

	public String getResetCurrenTransaction() {
		return getSingleLineFromFile("resetCurrentTransaction.sql");
	}
	
	public String getGetCurrentTransactionSql() {
		return getSingleLineFromFile("getCurrentTransaction.sql");
	}
	
	public String getGetRecordTransactionStartSql() {
		return getSingleLineFromFile("recordTransactionStart3.sql");
	}
}
