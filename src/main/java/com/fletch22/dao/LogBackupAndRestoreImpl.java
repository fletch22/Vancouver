package com.fletch22.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.cache.LoadCacheFromPersistenceModule;
import com.fletch22.dao.BackupFileOperations.BackupFileWriter;
import com.fletch22.orb.IntegrationSystemInitializer;

@Component
public class LogBackupAndRestoreImpl implements LogBackupAndRestore {
	
	private static final Logger logger = LoggerFactory.getLogger(LogBackupAndRestore.class);

	@Autowired
	LogActionDao logActionDao;

	@Autowired
	BackupSerializer backupSerializer;
	
	@Autowired
	BackupFileOperations backupFileOperations;
	
	@Autowired
	IntegrationSystemInitializer integrationSystemInitializer;
	
	@Autowired
	LoadCacheFromPersistenceModule loadCacheFromPersistenceModule;

	@Override
	public void persistToDisk() {
		List<ActionUndoInfo> list = logActionDao.getAllActionsWithAssociatedUndos();

		BackupFileWriter backupFileWriter = null;
		try {
			backupFileWriter = this.backupFileOperations.getBackupFileWriter();
			
			for (ActionUndoInfo actionUndoInfo : list) {
				StringBuilder sb = this.backupSerializer.serializeRecord(actionUndoInfo);
				backupFileWriter.fileWriter.write(sb.toString());
				backupFileWriter.fileWriter.write("\r\n");
			}
		} catch (Exception e) {
			String message = "Encountered problem while trying to persist data to backup file.";
			logger.error(message, e);
			throw new RuntimeException(message, e);
		} finally {
			if (backupFileWriter != null && backupFileWriter.fileWriter != null) {
				try {
					backupFileWriter.fileWriter.close();
				} catch (Exception e) {
					logger.error("Encountered problem trying to close filewriter for persisting data to disk ", e);
				}
			}
		}
		backupFileOperations.saveBackupFileToDefaultName(backupFileWriter.path);
	}

	@Override
	public void restoreFromDisk() {
		File fileRestore = backupFileOperations.getDefaultMainBackupFile();
		
		if (!fileRestore.exists()) {
			throw new RuntimeException("Restore file does not exist. You cannot restore the database.");
		}
		
		this.integrationSystemInitializer.nukeAndPaveIntegratedSystems();
		
		String line;
		try (
		    InputStream fis = new FileInputStream(fileRestore);
		    InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
		    BufferedReader br = new BufferedReader(isr);
		) {
		    while ((line = br.readLine()) != null) {
		    	ActionUndoInfo actionUndoInfo = backupSerializer.deserializeRecord(new StringBuilder(line));
		    	logAction(actionUndoInfo);
		    }
		} catch (Exception e) {
			String message = "Encountered problem while trying to read backup file.";
			throw new RuntimeException(message, e);
		}
		loadCacheFromPersistenceModule.initialize();
	}
	
	private void logAction(ActionUndoInfo actionUndoInfo) {
		logActionDao.logAction(actionUndoInfo.action, actionUndoInfo.undoAction, actionUndoInfo.tranId, actionUndoInfo.tranDate);
	}
}
