package com.fletch22.app.state;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.aop.Transactional;

@Component
public class FrontEndStateService {
	
	@Autowired
	FrontEndStateDao frontEndStateDao;

	@Transactional
	public void save(String state) {
		frontEndStateDao.save(state);
	}
}
