package com.fletch22.app.designer.dataField;

import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainTransformer;
import com.fletch22.orb.Orb;

@Component
public class DataFieldTransformer extends DomainTransformer<DataField> {
	
	public DataField transform(Orb orb) {
		
		DataField dataField = new DataField();
		
		this.setBaseAttributes(orb, dataField);
		dataField.label = orb.getUserDefinedProperties().get(DataField.ATTR_LABEL);
		
		return dataField;
	}
}
