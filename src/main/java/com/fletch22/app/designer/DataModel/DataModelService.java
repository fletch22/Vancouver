package com.fletch22.app.designer.DataModel;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainService;
import com.fletch22.app.designer.DataField.DataField;

@Component
public class DataModelService extends DomainService<DataModel, DataField> {
	
	@Autowired
	DataModelDao dataModelDao;

	public DataModel createInstance(String label) {
		DataModel dataModel = new DataModel();
		dataModel.label = label;
		
		save(dataModel);
		return dataModel;
	}
	
	public void save(DataModel layout) {
		dataModelDao.save(layout);
	}

	public DataModel get(long orbInternalId) {
		return dataModelDao.read(orbInternalId);
	}
	
	@Override
	public DataModel createInstance(Map<String, String> properties) {
		validatePropertiesSimple(properties, DataModel.ATTRIBUTE_LIST);
		
		return createInstance(properties.get(DataModel.ATTR_LABEL));
	}
	
	@Override
	public DataModel update(long id, Map<String, String> properties) {
		validatePropertiesSimple(properties, DataModel.ATTRIBUTE_LIST);
		
		DataModel dataModel = get(id);
		if (properties.containsKey(DataModel.ATTR_LABEL)) dataModel.label = properties.get(DataModel.ATTR_LABEL);
		
		return get(id);
	}
}
