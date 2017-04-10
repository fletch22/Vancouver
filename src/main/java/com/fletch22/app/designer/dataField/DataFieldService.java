package com.fletch22.app.designer.dataField;

import java.util.Map;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainServiceBase;
import com.fletch22.app.designer.userData.ModelToUserDataTranslator;

@Component
public class DataFieldService extends DomainServiceBase<DataField> {
	
	@Autowired
	DataFieldDao dataFieldDao;
	
	@Autowired
	ModelToUserDataTranslator modelToUserDataTranslator;

	public DataField createInstance(String label) {
		DataField dataField = new DataField();
		dataField.label = label;
		
		save(dataField);
		return dataField;
	}
	
	public void save(DataField dataField) {
		modelToUserDataTranslator.ensureNameSyncedWithUserDataType(dataField);
		dataFieldDao.save(dataField);
	}

	public DataField get(long orbInternalId) {
		return dataFieldDao.read(orbInternalId);
	}
	
	@Override
	public DataField createInstance(Map<String, String> properties) {
		validatePropertiesSimple(properties, DataField.ATTRIBUTE_LIST);
		
		return createInstance(properties.get(DataField.ATTR_LABEL));
	}
	
	@Override
	public DataField update(long id, Map<String, String> properties) {
		validatePropertiesSimple(properties, DataField.ATTRIBUTE_LIST);
		
		DataField dataField = get(id);
		
		if (properties.containsKey(DataField.ATTR_LABEL)) dataField.label = properties.get(DataField.ATTR_LABEL);
		
		this.save(dataField);
		
		return get(id);
	}

	public void delete(long id) {
		// TODO: Create logic to delete UserDataType's attribute
		throw new NotImplementedException("Not implemented yet.");
	}
}
