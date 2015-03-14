package com.fletch22.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fletch22.orb.rollback.UndoAction;
import com.fletch22.orb.rollback.UndoActionBundle;

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
            if (connection == null
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
			callableStatement.setBigDecimal(3, tranDate);
			callableStatement.setBigDecimal(4, tranId);
			 
			callableStatement.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			closeConnection();
		}
	}
	
	public List<UndoActionBundle> getUndosForTransactionAndSubesequentTransactions(long tranId) {
		List<UndoActionBundle> actions = new ArrayList<UndoActionBundle>();
		try {
			this.connection = getConnection();
			
			String transactionAndSubsequentUndo = "{call getTransactionAndSubsequentUndos2(?)}";
			 
			PreparedStatement pstmt = this.connection.prepareStatement(transactionAndSubsequentUndo);
			pstmt.setLong(1, tranId);
			ResultSet resultSet = pstmt.executeQuery();
	        
			while (resultSet.next()) {
				
				String undoActionBundleJson = resultSet.getString("undoAction");
				
				undoActionBundleJson = (null == undoActionBundleJson) ? StringUtils.EMPTY: undoActionBundleJson;
				StringBuilder action = new StringBuilder(undoActionBundleJson);
				
				UndoActionBundle bundle = UndoActionBundle.fromJson(action);
				
				actions.add(bundle);
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			closeConnection();
		}
		return actions;
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

	public void rollbackLog(long tranId) {
		try {
			connection = getConnection();
			
			String logAction = "{call removeTransactionAndAllAfter6(?)}";
			CallableStatement callableStatement = connection.prepareCall(logAction);
			
			callableStatement.setLong(1, tranId);
			 
			callableStatement.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			closeConnection();
		}
	}
}
