package com.fletch22.dao;

import java.math.BigDecimal;
import java.sql.Connection;
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

import com.fletch22.orb.rollback.UndoActionBundle;

public abstract class LogActionDao {

	Logger logger = LoggerFactory.getLogger(LogActionDao.class);

	protected static final BigDecimal NO_TRANSACTION_FOUND = new BigDecimal("-1.000000000000");
	protected Connection connection = null;

	DataSource dataSource;

	protected abstract Connection getConnection();

	public abstract BigDecimal getCurrentTransactionIfAny();

	public abstract void clearOutDatabase();

	public abstract void logAction(StringBuilder action, StringBuilder undoAction, BigDecimal tranId, BigDecimal tranDate);

	public abstract List<UndoActionBundle> getUndosForTransactionAndSubesequentTransactions(BigDecimal tranId);

	public abstract int countCommands();

	public abstract List<ActionInfo> getAllActions();
	
	public abstract List<ActionUndoInfo> getAllActionsWithAssociatedUndos();

	public abstract void rollbackToBeforeSpecificTransaction(BigDecimal tranId);
	
	public abstract void recordTransactionStart(BigDecimal tranId);

	public abstract void clearCurrentTransaction();

	public abstract String getConnectionString();

	public boolean isConnectionOpen() {
		try {
			return (null != connection && !connection.isClosed());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
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

	protected void closeConnection() {
		try {
			if (null != connection && !connection.isClosed()) {
				connection.close();
			}
		} catch (Exception e) {
			logger.error("Encountered a problem closing the connection: " + e.getMessage(), e);
		}
	}

	public boolean isTransactionInFlight() {
		return NO_TRANSACTION_FOUND.compareTo(getCurrentTransactionIfAny()) != 0;
	}

	protected List<UndoActionBundle> transformUndos(ResultSet resultSet) throws SQLException {
		List<UndoActionBundle> actions = new ArrayList<UndoActionBundle>();

		while (resultSet.next()) {

			String undoActionBundleJson = resultSet.getString("undoAction");

			undoActionBundleJson = (null == undoActionBundleJson) ? StringUtils.EMPTY : undoActionBundleJson;
			StringBuilder action = new StringBuilder(undoActionBundleJson);

			UndoActionBundle bundle = UndoActionBundle.fromJson(action);

			actions.add(bundle);
		}
		return actions;
	}

	public List<ActionInfo> transformActions(ResultSet resultSet) throws SQLException {

		List<ActionInfo> actions = new ArrayList<ActionInfo>();
		while (resultSet.next()) {
			ActionInfo actionInfo = new ActionInfo();
			actionInfo.action = new StringBuilder(resultSet.getString("action"));
			actionInfo.tranDate = resultSet.getBigDecimal("tranDate");
			actions.add(actionInfo);
		}
		return actions;
	}
	
	public List<ActionUndoInfo> transformActionsAndUndos(ResultSet resultSet) throws SQLException {
		List<ActionUndoInfo> actions = new ArrayList<ActionUndoInfo>();
		while (resultSet.next()) {
			ActionUndoInfo actionUndoInfo = new ActionUndoInfo();
			actionUndoInfo.action = new StringBuilder(resultSet.getString("action"));
			actionUndoInfo.undoAction = new StringBuilder(resultSet.getString("undoAction"));
			actionUndoInfo.tranId = resultSet.getBigDecimal("tranId");
			actionUndoInfo.tranDate = resultSet.getBigDecimal("tranDate");
			actions.add(actionUndoInfo);
		}
		return actions;
	}

	public class ActionInfo {
		public StringBuilder action;
		public BigDecimal tranDate;
	}
	
	public static DataSource getDataSource(String connectURI) {
		//
		// First, we'll create a ConnectionFactory that the
		// pool will use to create Connections.
		// We'll use the DriverManagerConnectionFactory,
		// using the connect string passed in the command line
		// arguments.
		//
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(connectURI, null);

		//
		// Next we'll create the PoolableConnectionFactory, which wraps
		// the "real" Connections created by the ConnectionFactory with
		// the classes that implement the pooling functionality.
		//
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);

		//
		// Now we'll need a ObjectPool that serves as the
		// actual pool of connections.
		//
		// We'll use a GenericObjectPool instance, although
		// any ObjectPool implementation will suffice.
		//
		ObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory);

		// Set the factory's pool property to the owning pool
		poolableConnectionFactory.setPool(connectionPool);

		//
		// Finally, we create the PoolingDriver itself,
		// passing in the object pool we created.
		//
		PoolingDataSource<PoolableConnection> dataSource = new PoolingDataSource<>(connectionPool);

		return dataSource;
	}
	
	public abstract TransactionSearchResult getSubsequentTransactionIfAny(BigDecimal tranId);
	
	public static class TransactionSearchResult {
		public BigDecimal tranId;
		
		public boolean wasTransactionFound() {
			boolean result = true;
			
			if (tranId == null
			|| this.tranId.compareTo(LogActionDao.NO_TRANSACTION_FOUND) == 0) {
				result = false;
			}
			
			return result;
		}
	}
}
