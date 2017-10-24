package com.fletch22.app.state.diff.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.OrbBasedComponent;
import com.fletch22.app.designer.ServiceFactory;
import com.fletch22.app.designer.ServiceJunction;
import com.fletch22.app.designer.dao.BaseDao;
import com.fletch22.app.designer.dataField.DataField;
import com.fletch22.app.designer.dataModel.DataModel;
import com.fletch22.app.designer.util.DomainUtilDao;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;

@Component
public class DeleteService {
	
	Logger logger = LoggerFactory.getLogger(DeleteService.class);
	
	@Autowired
	ServiceFactory serviceFactory;
	
	@Autowired 
	ServiceJunction serviceJunction;
	
	@Autowired
	DomainUtilDao domainUtilDao;
	
	@Autowired 
	BaseDao baseDao;
	
	@Autowired
	OrbManager orbManager;
	
	@Autowired
	OrbTypeManager orbTypeManager;

	public void delete(long id) {
		
		logger.debug("Attempting to delete the item.");
		
		Orb orb = orbManager.getOrb(id);
		OrbType orbType = orbTypeManager.getOrbType(orb.getOrbTypeInternalId());
		
		OrbBasedComponent orbBaseComponentChild = null;
		switch (orbType.label) {
			case DataModel.TYPE_LABEL:
				serviceJunction.dataModelService.delete(id);
				break;
			case DataField.TYPE_LABEL:
				serviceJunction.dataFieldService.delete(id);
				break;
//			case Div.TYPE_LABEL:
//				serviceJunction.divService.delete(id);
			default:
				throw new RuntimeException(String.format("Encountered error trying to delete component. Cannot yet delete component type '%s'", orbType.label));
		}
		
		baseDao.delete(id);
	}
}
