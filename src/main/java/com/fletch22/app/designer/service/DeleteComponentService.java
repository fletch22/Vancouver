package com.fletch22.app.designer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.dao.BaseDao;

@Component
public class DeleteComponentService {

	@Autowired
	BaseDao baseDao;
	
	public void delete(long id) {
		baseDao.delete(id);
	}
}
