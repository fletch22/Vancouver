package com.fletch22.app.designer.dataModel;

import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainService;
import com.fletch22.app.designer.dataField.DataField;
import com.fletch22.app.designer.userData.ModelToUserDataTranslator;

@Component
public class DataModelService extends DomainService<DataModel, DataField> {
	
	@Autowired
	DataModelDao dataModelDao;
	
	@Autowired
	ModelToUserDataTranslator modelToUserDataTranslator;

	public DataModel createInstance(String label) {
		DataModel dataModel = new DataModel();
		dataModel.label = label;
		
		save(dataModel);
		return dataModel;
	}
	
	public void save(DataModel dataModel) {
		dataModelDao.save(dataModel);
		modelToUserDataTranslator.syncWithUserData(dataModel);
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
		
		this.save(dataModel);
		
		return get(id);
	}

	public void delete(long id) {
		// TODO: Create logic to delete UserDataType and all instances.
		throw new NotImplementedException("Not implemented yet.");
	}
}
