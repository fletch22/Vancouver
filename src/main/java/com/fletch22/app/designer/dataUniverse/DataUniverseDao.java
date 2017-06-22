package com.fletch22.app.designer.dataUniverse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.dao.AppDesignerDao;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;

@Component
public class DataUniverseDao extends AppDesignerDao<DataUniverse, DataUniverseTransformer> {

	Logger logger = LoggerFactory.getLogger(DataUniverseDao.class);

	@Autowired
	DataUniverseTransformer pageTransformer;

	@Override
	protected void create(DataUniverse page) {
		OrbType orbType = this.orbTypeManager.getOrbType(DataUniverse.TYPE_LABEL);

		if (orbType == null) throw new RuntimeException("Type does not exist in database yet.");
		
		create(page, orbType);
	}

	@Override
	protected DataUniverseTransformer getTransformer() {
		return pageTransformer;
	}
	
	@Override
	protected void setNonChildrenAttributes(DataUniverse page, Orb orb) {
		orb.getUserDefinedProperties().put(DataUniverse.ATTR_LABEL, page.label);
	}
}

