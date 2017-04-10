package com.fletch22.app.designer.dataField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.dao.AppDesignerDao;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;

@Component
public class DataFieldDao extends AppDesignerDao<DataField, DataFieldTransformer> {

	Logger logger = LoggerFactory.getLogger(DataFieldDao.class);

	@Autowired
	DataFieldTransformer dataFieldTransformer;

	@Override
	protected void create(DataField dataField) {
		OrbType orbType = this.orbTypeManager.getOrbType(DataField.TYPE_LABEL);
		if (orbType == null) throw new RuntimeException("Type does not exist in database yet.");
		
		create(dataField, orbType);
	}

	@Override
	protected DataFieldTransformer getTransformer() {
		return dataFieldTransformer;
	}
	
	@Override
	protected void setNonChildrenAttributes(DataField dataField, Orb orb) {
		orb.getUserDefinedProperties().put(DataField.ATTR_LABEL, dataField.label);
	}
}


