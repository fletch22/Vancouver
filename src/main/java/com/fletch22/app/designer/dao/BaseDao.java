package com.fletch22.app.designer.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.orb.OrbManager;

@Component
public class BaseDao {
	
	@Autowired
	public OrbManager orbManager;
	
	public void delete(long id) {
		orbManager.deleteOrb(id, true);
	}
}
