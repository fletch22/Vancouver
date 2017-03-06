package com.fletch22.app.designer.datastore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.dao.AppDesignerDao;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;

@Component
public class DatastoreDao extends AppDesignerDao<Datastore, DatastoreTransformer> {

	Logger logger = LoggerFactory.getLogger(DatastoreDao.class);

	@Autowired
	DatastoreTransformer pageTransformer;

	@Override
	protected void create(Datastore page) {
		OrbType orbType = this.orbTypeManager.getOrbType(Datastore.TYPE_LABEL);

		if (orbType == null) throw new RuntimeException("Type does not exist in database yet.");
		
		create(page, orbType);
	}

	@Override
	protected DatastoreTransformer getTransformer() {
		return pageTransformer;
	}
	
	@Override
	protected void setNonChildrenAttributes(Datastore page, Orb orb) {
		orb.getUserDefinedProperties().put(Datastore.ATTR_LABEL, page.label);
	}
}

