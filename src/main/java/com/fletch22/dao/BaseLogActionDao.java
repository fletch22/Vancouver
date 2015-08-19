package com.fletch22.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DriverManagerConnectionFactory;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.dbcp2.PoolingDataSource;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.fletch22.orb.rollback.UndoActionBundle;

public abstract class BaseLogActionDao {

	Logger logger = LoggerFactory.getLogger(LogActionDao.class);

	protected static final BigDecimal NO_TRANSACTION_FOUND = new BigDecimal("-1");
	protected Connection connection = null;

	@Value("${db.log.host}")
	private String host;

	@Value("${db.log.databaseName}")
	private String databaseName;

	@Value("${db.log.userName}")
	private String userName;

	@Value("${db.log.password}")
	private String password;
	
	DataSource dataSource;

	protected Connection getConnection() {

		if (dataSource == null) {
			dataSource = setupDataSource(getConnectionString());
		}
		
		try {
			connection = dataSource.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return connection;
		
	}

	protected String getConnectionString() {
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

	public List<UndoActionBundle> getUndosForTransactionAndSubesequentTransactions(long tranId) {
		List<UndoActionBundle> actions = new ArrayList<UndoActionBundle>();
		
		PreparedStatement pstmt = null;
		try {
			this.connection = getConnection();

			String transactionAndSubsequentUndo = "{call getTransactionAndSubsequentUndos2(?)}";

			pstmt = this.connection.prepareStatement(transactionAndSubsequentUndo);
			pstmt.setLong(1, tranId);
			ResultSet resultSet = pstmt.executeQuery();

			while (resultSet.next()) {

				String undoActionBundleJson = resultSet.getString("undoAction");

				undoActionBundleJson = (null == undoActionBundleJson) ? StringUtils.EMPTY : undoActionBundleJson;
				StringBuilder action = new StringBuilder(undoActionBundleJson);

				UndoActionBundle bundle = UndoActionBundle.fromJson(action);

				actions.add(bundle);
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			try { if (pstmt != null) pstmt.close(); } catch(Exception e) { }
			closeConnection();
		}
		return actions;
	}

	public List<ActionInfo> getAllActions() {
		
		List<ActionInfo> actions = new ArrayList<ActionInfo>();
		PreparedStatement pstmt = null;
		try {
			this.connection = getConnection();

			String transactionAndSubsequentUndo = "{call getLog(0, 9999999999999999)}";

			pstmt = this.connection.prepareStatement(transactionAndSubsequentUndo);
			ResultSet resultSet = pstmt.executeQuery();

			while (resultSet.next()) {
				ActionInfo actionInfo = new ActionInfo();
				actionInfo.action = new StringBuilder(resultSet.getString("action"));
				actionInfo.tranDate = resultSet.getBigDecimal("tranDate");
				actions.add(actionInfo);
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			try { if (pstmt != null) pstmt.close(); } catch(Exception e) { }
			closeConnection();
		}
		return actions;
	}

	public class ActionInfo {
		public StringBuilder action;
		public BigDecimal tranDate;
	}

	protected void closeConnection() {
		try {
			if (null != connection && !connection.isClosed()) {
				connection.close();
			}
		} catch (Exception e) {
			logger.error("Encountered a problem closing the connection: " + e.getMessage(), e);
		}
	}

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

	public void rollbackCurrentTransaction() {
		try {
			BigDecimal tranId = getCurrentTransactionIfAny();

			if (NO_TRANSACTION_FOUND.compareTo(tranId) != 0) {
				this.rollbackToBeforeSpecificTransaction(tranId);
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public boolean isTransactionInFlight() {
		return NO_TRANSACTION_FOUND.compareTo(getCurrentTransactionIfAny()) != 0;
	}

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

	public void resetCurrentTransaction() {
		
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
	
	public static DataSource setupDataSource(String connectURI) {
        //
        // First, we'll create a ConnectionFactory that the
        // pool will use to create Connections.
        // We'll use the DriverManagerConnectionFactory,
        // using the connect string passed in the command line
        // arguments.
        //
        ConnectionFactory connectionFactory =
            new DriverManagerConnectionFactory(connectURI,null);

        //
        // Next we'll create the PoolableConnectionFactory, which wraps
        // the "real" Connections created by the ConnectionFactory with
        // the classes that implement the pooling functionality.
        //
        PoolableConnectionFactory poolableConnectionFactory =
            new PoolableConnectionFactory(connectionFactory, null);

        //
        // Now we'll need a ObjectPool that serves as the
        // actual pool of connections.
        //
        // We'll use a GenericObjectPool instance, although
        // any ObjectPool implementation will suffice.
        //
        ObjectPool<PoolableConnection> connectionPool =
                new GenericObjectPool<>(poolableConnectionFactory);
        
        // Set the factory's pool property to the owning pool
        poolableConnectionFactory.setPool(connectionPool);

        //
        // Finally, we create the PoolingDriver itself,
        // passing in the object pool we created.
        //
        PoolingDataSource<PoolableConnection> dataSource =
                new PoolingDataSource<>(connectionPool);

        return dataSource;
    }
}
