package com.fletch22.dao.inmem;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.h2.jdbcx.JdbcConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.fletch22.dao.LogActionDao;
import com.fletch22.orb.rollback.UndoActionBundle;

public class LogActionInMemDao extends LogActionDao {

	Logger logger = LoggerFactory.getLogger(LogActionInMemDao.class);

	@Autowired
	DataSource dataSource;

	@Value("${h2.database.url}")
	private String jdbcUrl;

	@Value("${h2.database.user}")
	private String login;

	@Value("${h2.database.password}")
	private String password;

	@Value("${h2.database.driver}")
	private String driver;

	@Autowired
	SqlStatementStore sqlStatementStore;

	JdbcConnectionPool connectionPool = null;

	@Override
	protected Connection getConnection() {

		if (connectionPool == null) {
			connectionPool = JdbcConnectionPool.create(jdbcUrl, login, password);

			initializeDatabase();
		}

		try {
			connection = connectionPool.getConnection();
		} catch (SQLException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

		return connection;
	}

	private void initializeDatabase() {
		executeSqlLines(sqlStatementStore.getInitializeDatabaseSql());
	}

	private void executeSqlLines(String[] lines) {

		Connection connection = null;
		try {

			connection = this.getConnection();
			connection.setAutoCommit(false);
			Statement st = connection.createStatement();

			for (int i = 0; i < lines.length; i++) {
				// Note: We ensure that there is no spaces before or after the
				// request string in order to not execute empty statements
				if (!lines[i].trim().equals(StringUtils.EMPTY)) {
					st.execute(lines[i]);
					logger.debug(" >> {}", lines[i]);
				}
			}
			connection.commit();

		} catch (Exception e) {
			logError(e);
			if (connection != null) {
				try {
					connection.rollback();
				} catch (Exception sqlex) {
					throw new RuntimeException("Encountered problem rolling back transaction.", sqlex);
				}
			}
		} finally {
			cleanUpConnectionAfterSqlOperation(connection);
		}
	}

	@Override
	public BigDecimal getCurrentTransactionIfAny() {
		BigDecimal currentTranId = NO_TRANSACTION_FOUND;

		String getAnyOrphanedTransactionsSql = sqlStatementStore.getAnyOrphanedTransactionsSql();

		Connection connection = null;
		try {

			connection = this.getConnection();
			connection.setAutoCommit(false);

			PreparedStatement preparedStatement = connection.prepareStatement(getAnyOrphanedTransactionsSql);

			ResultSet resultSet = preparedStatement.executeQuery();

			boolean hasRows = resultSet.first();

			if (hasRows) {
				currentTranId = resultSet.getBigDecimal(1);
			}
		} catch (Exception e) {
			throw new RuntimeException("Encountered error while trying to get Undos.", e);
		} finally {
			cleanUpConnectionAfterSqlOperation(connection);
		}
		return currentTranId;
	}

	@Override
	public void clearOutDatabase() {
		executeSqlLines(sqlStatementStore.getClearOutDatabaseSql());
	}

	@Override
	public void logAction(StringBuilder action, StringBuilder undoAction, BigDecimal tranId, BigDecimal tranDate) {

		String undoActionValue = (action == null) ? null : undoAction.toString();
		String actionValue = action.toString();

		String insertIntoUndoActionLogSql = sqlStatementStore.getInsertIntoUndoActionLog();
		String insertIntoActionLogSql = sqlStatementStore.getInsertIntoActionLog();

		Connection connection = null;
		try {

			connection = this.getConnection();
			connection.setAutoCommit(false);

			if (!StringUtils.isEmpty(undoActionValue)) {
				PreparedStatement preparedStatement = connection.prepareStatement(insertIntoUndoActionLogSql);
				preparedStatement.setBigDecimal(1, tranDate);
				preparedStatement.setString(2, undoActionValue);
				preparedStatement.setBigDecimal(3, tranId);

				preparedStatement.execute();
			}

			PreparedStatement preparedStatement = connection.prepareStatement(insertIntoActionLogSql);
			preparedStatement.setString(1, actionValue);
			preparedStatement.setBigDecimal(2, tranDate);

			preparedStatement.execute();

			connection.commit();

		} catch (Exception e) {
			processCaughtExceptionDuringTransactionOperation(connection, e);
		} finally {
			cleanUpConnectionAfterSqlOperation(connection);
		}
	}

	private void cleanUpConnectionAfterSqlOperation(Connection connection) {
		if (connection != null) {

			try {
				if (!connection.isClosed()) {
					connection.setAutoCommit(true);
					connection.close();
				}
			} catch (Exception sqlex) {
				throw new RuntimeException("Encountered problem rolling back transaction.", sqlex);
			}
		}
	}

	private void processCaughtExceptionDuringTransactionOperation(Connection connection, Exception e) {
		logError(e);

		if (connection != null) {
			try {
				connection.rollback();
			} catch (Exception sqlex) {
				throw new RuntimeException("Encountered problem rolling back transaction.", sqlex);
			}
		}

		throw new RuntimeException("Encountered problem executing sql.", e);
	}

	private void logError(Exception e) {
		logger.info("*** Error : " + e.toString());
		logger.info("*** ");
		logger.info("*** Error : ");
		e.printStackTrace();
		logger.info("################################################");
	}

	@Override
	public List<UndoActionBundle> getUndosForTransactionAndSubesequentTransactions(BigDecimal tranId) {

		String getSelectUndoSql = sqlStatementStore.getSelectUndoSql();

		Connection connection = null;
		List<UndoActionBundle> listUndoActionBundle = new ArrayList<UndoActionBundle>();
		try {

			connection = this.getConnection();
			connection.setAutoCommit(false);

			PreparedStatement preparedStatement = connection.prepareStatement(getSelectUndoSql);
			preparedStatement.setBigDecimal(1, tranId);

			ResultSet resultSet = preparedStatement.executeQuery();

			listUndoActionBundle = transformUndos(resultSet);

		} catch (Exception e) {
			throw new RuntimeException("Encountered error while trying to get Undos.", e);
		} finally {
			cleanUpConnectionAfterSqlOperation(connection);
		}
		return listUndoActionBundle;
	}

	@Override
	public int countCommands() {
		String getCountLogItemsSql = sqlStatementStore.getCountLogItemsSql();

		int count = 0;
		Connection connection = null;
		try {

			connection = this.getConnection();
			connection.setAutoCommit(false);

			PreparedStatement preparedStatement = connection.prepareStatement(getCountLogItemsSql);

			ResultSet resultSet = preparedStatement.executeQuery();

			boolean hasRows = resultSet.first();

			if (hasRows) {
				count = resultSet.getInt(1);
			}
		} catch (Exception e) {
			throw new RuntimeException("Encountered error while trying to get Undos.", e);
		} finally {
			cleanUpConnectionAfterSqlOperation(connection);
		}
		return count;
	}

	@Override
	public List<ActionInfo> getAllActions() {
		String getLogSql = sqlStatementStore.getLogSql();

		Connection connection = null;
		List<ActionInfo> actionInfoList = new ArrayList<ActionInfo>();
		try {

			connection = this.getConnection();
			connection.setAutoCommit(false);

			PreparedStatement preparedStatement = connection.prepareStatement(getLogSql);
			preparedStatement.setBigDecimal(1, new BigDecimal("0"));
			preparedStatement.setBigDecimal(2, new BigDecimal("9999999999999999"));

			ResultSet resultSet = preparedStatement.executeQuery();

			actionInfoList = transformActions(resultSet);

		} catch (Exception e) {
			throw new RuntimeException("Encountered error while trying to get Undos.", e);
		} finally {
			cleanUpConnectionAfterSqlOperation(connection);
		}
		return actionInfoList;
	}

	@Override
	public void rollbackToBeforeSpecificTransaction(BigDecimal tranId) {
		String deleteActionLogSql = sqlStatementStore.getDeleteActionLogSql();
		String deleteUndoLogSql = sqlStatementStore.getDeleteUndoLogSql();
		String getCurrentTransactionSql = sqlStatementStore.getGetCurrentTransactionSql();
		String deleteFromCurrentTransactionSql = sqlStatementStore.getResetCurrenTransaction();

		Connection connection = null;
		try {

			connection = this.getConnection();
			connection.setAutoCommit(false);

			PreparedStatement preparedStatement = connection.prepareStatement(deleteActionLogSql);
			preparedStatement.setBigDecimal(1, tranId);
			preparedStatement.execute();

			preparedStatement = connection.prepareStatement(deleteUndoLogSql);
			preparedStatement.setBigDecimal(1, tranId);
			preparedStatement.execute();

			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(getCurrentTransactionSql);

			boolean hasRows = resultSet.first();
			
			if (hasRows) {
				BigDecimal tranIdFound = resultSet.getBigDecimal(1);
				if (tranId.compareTo(tranIdFound) == 0) {
					statement = connection.createStatement();
					statement.execute(deleteFromCurrentTransactionSql);
				}
			}

			connection.commit();

		} catch (Exception e) {
			processCaughtExceptionDuringTransactionOperation(connection, e);
		} finally {
			cleanUpConnectionAfterSqlOperation(connection);
		}
	}

	@Override
	public void recordTransactionStart(BigDecimal tranId) {
		String getCurrentTransactionSql = sqlStatementStore.getGetCurrentTransactionSql();
		String recordTransactionStartSql = sqlStatementStore.getGetRecordTransactionStartSql();

		try {

			connection = this.getConnection();
			connection.setAutoCommit(false);

			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(getCurrentTransactionSql);

			boolean hasRows = resultSet.first();

			if (hasRows) {
				BigDecimal result = resultSet.getBigDecimal(1);
				throw new RuntimeException("Encountered problem while trying to begin transaction with id '" + tranId + "'. Transaction cannot be set because transaction '" + result + "' already exists.");
			} else {
				PreparedStatement preparedStatement = connection.prepareStatement(recordTransactionStartSql);
				preparedStatement.setBigDecimal(1, tranId);
				preparedStatement.execute();
			}

			connection.commit();

		} catch (Exception e) {
			processCaughtExceptionDuringTransactionOperation(connection, e);
		} finally {
			cleanUpConnectionAfterSqlOperation(connection);
		}
	}

	@Override
	public void clearCurrentTransaction() {
		String resetCurrentTransactionSql = sqlStatementStore.getResetCurrenTransaction();

		try {

			connection = this.getConnection();
			connection.setAutoCommit(false);

			Statement statement = connection.createStatement();
			statement.execute(resetCurrentTransactionSql);

			connection.commit();

		} catch (Exception e) {
			processCaughtExceptionDuringTransactionOperation(connection, e);
		} finally {
			cleanUpConnectionAfterSqlOperation(connection);
		}
	}

	@Override
	public String getConnectionString() {
		return this.jdbcUrl;
	}
}
