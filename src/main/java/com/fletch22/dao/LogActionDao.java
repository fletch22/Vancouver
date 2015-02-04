package com.fletch22.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LogActionDao {
	
	Logger logger = LoggerFactory.getLogger(LogActionDao.class);
	private Connection connection = null;
	
	@Value("${db.log.host}")
	private String host;
	
	@Value("${db.log.databaseName}")
	private String databaseName; 
	
	@Value("${db.log.userName}")
	private String userName;
	
	@Value("${db.log.password}")
	private String password;
	
	private Connection getConnection(){
        try {	
            if(connection == null
            || connection.isClosed()) {
                connection = DriverManager.getConnection(getConnectionString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return connection;
    }
	
	private String getConnectionString() {
		return String.format("jdbc:mysql://%s/%s?user=%s&password=%s", host, databaseName, userName, password);
	}

	public boolean isConnectionOpen() {
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
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			closeConnection();
		}
		
		return numberOfCommands;
	}
	
	public void clearOutDatabase() {

		try {
			connection = getConnection();
			
			String clearOutDatabase = "{call blowAwayActionTable5}";
			CallableStatement callableStatement = connection.prepareCall(clearOutDatabase);
			
			callableStatement.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			closeConnection();
		}
	}
	
	public void logAction(StringBuilder action, StringBuilder undoAction, BigDecimal tranId, BigDecimal tranDate) {
		
		try {
			connection = getConnection();
			
			String logAction = "{call logAction5(?, ?, ?, ?)}";
			CallableStatement callableStatement = connection.prepareCall(logAction);
			
			callableStatement.setString(1, action.toString());
			callableStatement.setString(2, undoAction.toString());
			callableStatement.setBigDecimal(3, tranId);
			callableStatement.setBigDecimal(4, tranDate);
			 
			callableStatement.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			closeConnection();
		}
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
