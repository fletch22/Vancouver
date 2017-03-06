package com.fletch22.dao;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BackupFileOperations {

	private static Logger logger = LoggerFactory.getLogger(BackupFileOperations.class);

	public static final String BACKUP_FILE_NAME = "backup3";
	public static final String BACKUP_FILE_EXT = "txt";

	@Value("${backup.location.parentFolder}")
	String backupParentFolder;
	
	public static class BackupFileWriter {
		FileWriter fileWriter;
		String path;
		
		BackupFileWriter(FileWriter fileWriter, String path) {
			this.fileWriter = fileWriter;
			this.path = path;
		}
	}

	public BackupFileWriter getBackupFileWriter() {
		String backupFilePath = this.getBackupFilepath();

		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(backupFilePath);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new BackupFileWriter(fileWriter, backupFilePath);
	}
	
	public String getDefaultMainBackupFilename() {
		return BACKUP_FILE_NAME + "." + BACKUP_FILE_EXT;
	}
	
	public File getDefaultMainBackupFile() {
		String parentFolderPath = getParentFolderPath();

		String filename = getDefaultMainBackupFilename();
		return new File(parentFolderPath, filename);
	}

	public String getBackupFilepath() {
		File f = getDefaultMainBackupFile();
		
		String filename = f.getName();

		if (f.isDirectory()) {
			String message = String
					.format("Encountered problem trying to get backup file. Backup file name \'%s\' is the name of a directory. \'%s\' should not be a directory name. Process is aborting.",
							filename);
			throw new RuntimeException(message);
		}

		int count = 1;
		while (f.exists()) {
			filename = BACKUP_FILE_NAME + "-" + String.valueOf(count) + "." + BACKUP_FILE_EXT;
			Path parentPath = f.toPath().getParent();
			f = new File(parentPath.toFile().getAbsolutePath(), filename);
			count++;
		}

		return f.getAbsolutePath();
	}

	private String getParentFolderPath() {

		File parentFolder = new File(this.backupParentFolder);
		String parentFolderPath = parentFolder.getAbsolutePath();
		if (!parentFolder.exists()) {
			String message = String
					.format("Encountered problem trying to find backup folder parent \'%s\'. Path does not exist. Set the default parent folder path to a folder that exists. Alternately, create the parent folder manually. Then try again.",
							parentFolderPath);
			throw new RuntimeException(message);
		}

		if (!parentFolder.canRead()) {
			String message = String
					.format("Encountered problem trying to use backup folder parent \'%s\'. Users will be unable to read from (and possibly write to) this folder. Ensure the user has sufficient permission to read and write to this folder. Then try again.",
							parentFolderPath);
			throw new RuntimeException(message);
		}

		if (!parentFolder.canWrite()) {
			String message = String
					.format("Encountered problem trying to use backup folder parent \'%s\'. Users will be unable to write to this folder. Ensure the user has sufficient permission to read and write to this folder. Then try again.",
							parentFolderPath);
			throw new RuntimeException(message);
		}
		return parentFolderPath;
	}

	public void saveBackupFileToDefaultName(String newBackupFilePath) {
		Path mainBackupFile = new File(getParentFolderPath(), getDefaultMainBackupFilename()).toPath();
		
		if (newBackupFilePath.equals(mainBackupFile.toFile().getAbsolutePath())) return;
		
		Path pathBackupFile = Paths.get(newBackupFilePath);
		
		String suffix = String.valueOf(System.currentTimeMillis());
		
		try {
			if (mainBackupFile.toFile().exists()) {
				Files.move(mainBackupFile, Paths.get(mainBackupFile.toFile().getAbsolutePath() + "." + suffix));
			}
			Files.move(pathBackupFile, mainBackupFile);
		} catch (Exception e) {
			String message = "Encountered problem when trying to copy new backup file over the old one.";
			throw new RuntimeException(message, e);
		}
	}
}
