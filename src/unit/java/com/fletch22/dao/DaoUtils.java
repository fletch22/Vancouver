package com.fletch22.dao;

import java.sql.Connection;

public class DaoUtils {

	public Connection getConnection(LogActionDao logActionDao) {
		return logActionDao.getConnection();
	}
}
