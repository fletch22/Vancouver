package com.fletch22.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.fletch22.orb.rollback.UndoActionBundle;

public class LogActionDaoImpl extends LogActionDao {

	Logger logger = LoggerFactory.getLogger(LogActionDaoImpl.class);

	@Value("${db.log.host}")
	private String host;

	@Value("${db.log.databaseName}")
	private String databaseName;

	@Value("${db.log.userName}")
	private String userName;

	@Value("${db.log.password}")
	private String password;
	
	public String getConnectionString() {
		return String.format("jdbc:mysql://%s/%s?user=%s&password=%s", host, databaseName, userName, password);
	}
	
	protected Connection getConnection() {

		if (dataSource == null) {
			dataSource = getDataSource(getConnectionString());
		}
		
		try {
			connection = dataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return connection;
		
	}

	@Override
	public int countCommands() {
		
		int numberOfCommands;
		CallableStatement callableStatement = null;	
		try {
			connection = getConnection();

			String countLogItems = "{call countLogItems(?)}";
			callableStatement = connection.prepareCall(countLogItems);
			callableStatement.registerOutParameter(1, java.sql.Types.INTEGER);

			callableStatement.executeUpdate();

			numberOfCommands = callableStatement.getInt(1);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			try { if (callableStatement != null) callableStatement.close(); } catch(Exception e) { }
			closeConnection();
		}

		return numberOfCommands;
	}

	@Override
	public void clearOutDatabase() {

		CallableStatement callableStatement = null;
		try {
			connection = getConnection();

			String clearOutDatabase = "{call blowAwayActionTable5}";
			callableStatement = connection.prepareCall(clearOutDatabase);

			callableStatement.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			try { if (callableStatement != null) callableStatement.close(); } catch(Exception e) { }
			closeConnection();
		}
	}

	@Override
	public void logAction(StringBuilder action, StringBuilder undoAction, BigDecimal tranId, BigDecimal tranDate) {

		CallableStatement callableStatement = null;
		try {
			connection = getConnection();

			String logAction = "{call logAction5(?, ?, ?, ?)}";
			callableStatement = connection.prepareCall(logAction);

			callableStatement.setString(1, action.toString());
			callableStatement.setString(2, undoAction.toString());
			callableStatement.setBigDecimal(3, tranDate);
			callableStatement.setBigDecimal(4, tranId);

			callableStatement.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			try { if (callableStatement != null) callableStatement.close(); } catch(Exception e) { }
			closeConnection();
		}
	}

	@Override
	public List<UndoActionBundle> getUndosForTransactionAndSubesequentTransactions(BigDecimal tranId) {
		List<UndoActionBundle> actions = new ArrayList<UndoActionBundle>();
		
		PreparedStatement pstmt = null;
		try {
			this.connection = getConnection();

			String transactionAndSubsequentUndo = "{call getTransactionAndSubsequentUndos2(?)}";

			pstmt = this.connection.prepareStatement(transactionAndSubsequentUndo);
			pstmt.setBigDecimal(1, tranId);
			ResultSet resultSet = pstmt.executeQuery();

			actions = transformUndos(resultSet);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			try { if (pstmt != null) pstmt.close(); } catch(Exception e) { }
			closeConnection();
		}
		return actions;
	}

	@Override
	public List<ActionInfo> getAllActions() {
		
		List<ActionInfo> actions = new ArrayList<ActionInfo>();
		PreparedStatement pstmt = null;
		try {
			this.connection = getConnection();

			String transactionAndSubsequentUndo = "{call getLog(0, 9999999999999999)}";

			pstmt = this.connection.prepareStatement(transactionAndSubsequentUndo);
			ResultSet resultSet = pstmt.executeQuery();

			actions = transformActions(resultSet);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			try { if (pstmt != null) pstmt.close(); } catch(Exception e) { }
			closeConnection();
		}
		return actions;
	}

	@Override
	public void rollbackToBeforeSpecificTransaction(BigDecimal tranId) {
		
		CallableStatement callableStatement = null;
		try {
			connection = getConnection();

			String logAction = "{call removeTransactionAndAllAfter6(?)}";
			callableStatement = connection.prepareCall(logAction);

			callableStatement.setBigDecimal(1, tranId);

			callableStatement.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			try { if (callableStatement != null) callableStatement.close(); } catch(Exception e) { }
			closeConnection();
		}
	}

	@Override
	public void recordTransactionStart(BigDecimal tranId) {
		
		CallableStatement callableStatement = null;
		try {
			connection = getConnection();

			String beginTransaction = "{call recordTransactionStart3(?, ?)}";
			callableStatement = connection.prepareCall(beginTransaction);

			callableStatement.setBigDecimal(1, tranId);
			callableStatement.registerOutParameter(2, java.sql.Types.INTEGER);

			callableStatement.executeUpdate();

			int result = callableStatement.getInt(2);

			if (result == 0) {
				throw new RuntimeException("Encountered problem while trying to begin transaction with id '" + tranId.toString() + "'. Transaction cannot be set -- perhaps because it already is set.");
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			try { if (callableStatement != null) callableStatement.close(); } catch(Exception e) { }
			closeConnection();
		}
	}

	@Override
	public BigDecimal getCurrentTransactionIfAny() {
		
		BigDecimal tranId = null;
		CallableStatement callableStatement = null;
		try {
			connection = getConnection();

			String beginTransaction = "{call getAnyOrphanedTransactions2(?)}";
			callableStatement = connection.prepareCall(beginTransaction);

			callableStatement.registerOutParameter(1, java.sql.Types.DECIMAL);

			callableStatement.executeUpdate();

			tranId = callableStatement.getBigDecimal(1);

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			try { if (callableStatement != null) callableStatement.close(); } catch(Exception e) { }
			closeConnection();
		}
		return tranId;
	}

	@Override
	public void clearCurrentTransaction() {
		
		CallableStatement callableStatement = null;
		try {
			connection = getConnection();

			String resetCurrentTransaction = "{call resetCurrentTransaction()}";
			callableStatement = connection.prepareCall(resetCurrentTransaction);

			callableStatement.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			try { if (callableStatement != null) callableStatement.close(); } catch(Exception e) { }
			closeConnection();
		}
	}
}
