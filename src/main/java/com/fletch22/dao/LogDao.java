package com.fletch22.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogDao {
	
	Logger logger = LoggerFactory.getLogger(LogDao.class);
	private Connection connection = null;
	
	private Connection getConnection(){
        try {
            if(connection == null
            || connection.isClosed()) {
                connection = DriverManager.getConnection("jdbc:mysql://localhost/orblog?user=root&password=rumgen999");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return connection;
    }
	
	private boolean isConnectionOpen() {
		try {
			return (null != connection && !connection.isClosed());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public int countCommands() {
		int numberOfCommands;
		
		try {
			connection = getConnection();
			
			String countLogItems = "{call countLogItems(?)}";
			CallableStatement callableStatement = connection.prepareCall(countLogItems);
			callableStatement.registerOutParameter(1, java.sql.Types.INTEGER);
			 
			callableStatement.executeUpdate();
			 
			numberOfCommands = callableStatement.getInt(1);
			
			logger.info("Number of commands: {}", numberOfCommands);
			
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			closeConnection();
		}
		
		return numberOfCommands;
	}

	private void closeConnection() {
		try {
			if (null != connection
			&& !connection.isClosed()) {
				connection.close();
			}
		} catch (Exception e) {
			logger.error("Encountered a problem closing the connection: " + e.getMessage(), e);
		}
	}
}
