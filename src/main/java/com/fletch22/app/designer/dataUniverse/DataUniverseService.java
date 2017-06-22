package com.fletch22.app.designer.dataUniverse;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainService;
import com.fletch22.app.designer.datastore.Datastore;

@Component
public class DataUniverseService extends DomainService<DataUniverse, Datastore> {
	
	@Autowired
	DataUniverseDao datastoreDao;

	public DataUniverse createInstance(String label) {
		DataUniverse dataUniverse = new DataUniverse();
		dataUniverse.label = label;
		save(dataUniverse);
		return dataUniverse;
	}
	
	public void save(DataUniverse datastore) {
		datastoreDao.save(datastore);
	}

	public DataUniverse get(long orbInternalId) {
		return datastoreDao.read(orbInternalId);
	}
	
	@Override
	public DataUniverse createInstance(Map<String, String> properties) {
		validatePropertiesSimple(properties, DataUniverse.ATTRIBUTE_LIST);
		
		return createInstance(properties.get(DataUniverse.ATTR_LABEL));
	}
	
	@Override
	public DataUniverse update(long id, Map<String, String> properties) {
		validatePropertiesSimple(properties, DataUniverse.ATTRIBUTE_LIST);
		
		DataUniverse datastore = get(id);
		if (properties.containsKey(DataUniverse.ATTR_LABEL)) datastore.label = properties.get(DataUniverse.ATTR_LABEL);
		
		this.save(datastore);
		
		return datastore;
	}
}
