package com.fletch22.app.designer.DataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.dao.AppDesignerDao;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;

@Component
public class DataModelDao extends AppDesignerDao<DataModel, DataModelTransformer> {

	Logger logger = LoggerFactory.getLogger(DataModelDao.class);

	@Autowired
	DataModelTransformer dataModelTransformer;

	@Override
	protected void create(DataModel dataModel) {
		OrbType orbType = this.orbTypeManager.getOrbType(DataModel.TYPE_LABEL);
		if (orbType == null) throw new RuntimeException("Type does not exist in database yet.");
		
		create(dataModel, orbType);
	}

	@Override
	protected DataModelTransformer getTransformer() {
		return dataModelTransformer;
	}
	
	@Override
	protected void setNonChildrenAttributes(DataModel dataModel, Orb orb) {
		orb.getUserDefinedProperties().put(DataModel.ATTR_LABEL, dataModel.label);
	}
}


