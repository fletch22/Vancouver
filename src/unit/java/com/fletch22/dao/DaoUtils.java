package com.fletch22.dao;

import java.sql.Connection;

public class DaoUtils {

	public Connection getConnection(Dao dao) {
		return dao.getConnection();
	}
}
