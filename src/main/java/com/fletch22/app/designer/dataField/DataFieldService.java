package com.fletch22.app.designer.dataField;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainServiceBase;
import com.fletch22.app.designer.userData.ModelToUserDataTranslator;
import com.fletch22.orb.Orb;

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
		Optional<NameChangeInfo> nameChangeInfoOpt = this.getNameChangeInfo(dataField);
		dataFieldDao.save(dataField);
		
		if (nameChangeInfoOpt.isPresent()) {
			modelToUserDataTranslator.renameField(nameChangeInfoOpt.get());
		}
	}
	
	public Optional<NameChangeInfo> getNameChangeInfo(DataField dataField) {
		NameChangeInfo nameChangeInfo = null;
		if (dataField.hasParent()) {
			Orb orbOriginal = dataField.getOrbOriginal();
			if (orbOriginal != null) {
				String oldName = orbOriginal.getUserDefinedProperties().get(DataField.ATTR_LABEL);
				nameChangeInfo = new NameChangeInfo(oldName, dataField);
			}
		}
		return Optional.ofNullable(nameChangeInfo);
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

		if (properties.containsKey(DataField.ATTR_LABEL))
			dataField.label = properties.get(DataField.ATTR_LABEL);

		this.save(dataField);

		return get(id);
	}

	public void delete(long id) {
		// TODO: Create logic to delete UserDataType's attribute
		throw new NotImplementedException("Not implemented yet.");
	}
}
