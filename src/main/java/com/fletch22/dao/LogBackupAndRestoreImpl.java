package com.fletch22.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.dao.BackupFileOperations.BackupFileWriter;

@Component
public class LogBackupAndRestoreImpl implements LogBackupAndRestore {
	
	private static final Logger logger = LoggerFactory.getLogger(LogBackupAndRestore.class);

	@Autowired
	LogActionDao logActionDao;

	@Autowired
	BackupSerializer backupSerializer;
	
	@Autowired
	BackupFileOperations backupFileOperations;

	@Override
	public void persistToDisk() {
		List<ActionUndoInfo> list = logActionDao.getAllActionsWithAssociatedUndos();

		BackupFileWriter backupFileWriter = null;
		try {
			backupFileWriter = backupFileOperations.getBackupFileWriter();
			for (ActionUndoInfo actionUndoInfo : list) {
				StringBuilder sb = backupSerializer.serializeRecord(actionUndoInfo);
				backupFileWriter.fileWriter.write(sb.toString());
				backupFileWriter.fileWriter.write("\r\n");
			}
			backupFileWriter.fileWriter.close();
		} catch (Exception e) {
			String message = "Encountered problem while trying to persist data to backup file.";
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
}
