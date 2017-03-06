package com.fletch22.app.designer.DataModel;

import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainTransformer;
import com.fletch22.orb.Orb;

@Component
public class DataModelTransformer extends DomainTransformer<DataModel> {
	
	public DataModel transform(Orb orb) {
		
		DataModel dataModel = new DataModel();
		
		this.setBaseAttributes(orb, dataModel);
		dataModel.label = orb.getUserDefinedProperties().get(DataModel.ATTR_LABEL);
		
		return dataModel;
	}
}
