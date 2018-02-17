Setting up Eclipse on Windows

Create Project

Connect Project to Folder with Git

Install Buildship Gradle Integration

Install Chocolately

choco install gradle

Install MySQL Community Edition

Give it the same credentials found in this project.

Create javaorblog database

Execute the javaorblog-backup.sql SQL script.

Add the aspectjweaver command args to Java

	1. Select: Window -> Preferences -> Java -> Installed JREs -> <Your current JDK>
	2. Click Edit Button
	2. Dialog will open. Find the JVM args textbox. Add the following:
	
		-javaagent: <Your Full Path To Project>\aspectjweaver\aspectjweaver-1.8.4.jar
		
From the command line run the following:
	
	gradle appRun

Add the Eclipse Groovy Plugin

Restart Eclipse 

Change the project to a Maven Type Project:

	Right mouse click 'vancover' project root folder.
	
	Select Configure
	
	Select Convert to Maven Project
	
Change application.properties 'backup.location.parentFolder'





