package com.fletch22.app.designer.datastore;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainService;
import com.fletch22.app.designer.dataModel.DataModel;

@Component
public class DatastoreService extends DomainService<Datastore, DataModel> {
	
	@Autowired
	DatastoreDao datastoreDao;

	public Datastore createInstance(String label) {
		Datastore datastore = new Datastore();
		datastore.label = label;
		save(datastore);
		return datastore;
	}
	
	public void save(Datastore datastore) {
		datastoreDao.save(datastore);
	}

	public Datastore get(long orbInternalId) {
		return datastoreDao.read(orbInternalId);
	}
	
	@Override
	public Datastore createInstance(Map<String, String> properties) {
		validatePropertiesSimple(properties, Datastore.ATTRIBUTE_LIST);
		
		return createInstance(properties.get(Datastore.ATTR_LABEL));
	}
	
	@Override
	public Datastore update(long id, Map<String, String> properties) {
		validatePropertiesSimple(properties, Datastore.ATTRIBUTE_LIST);
		
		Datastore datastore = get(id);
		if (properties.containsKey(Datastore.ATTR_LABEL)) datastore.label = properties.get(Datastore.ATTR_LABEL);
		
		this.save(datastore);
		
		return datastore;
	}
}
