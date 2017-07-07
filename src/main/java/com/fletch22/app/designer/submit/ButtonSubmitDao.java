package com.fletch22.app.designer.submit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.dao.AppDesignerDao;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;

@Component
public class ButtonSubmitDao extends AppDesignerDao<ButtonSubmit, ButtonSubmitTransformer> {

	Logger logger = LoggerFactory.getLogger(ButtonSubmitDao.class);

	@Autowired
	ButtonSubmitTransformer dropDownListboxTransformer;

	@Override
	protected void create(ButtonSubmit div) {
		OrbType orbType = this.orbTypeManager.getOrbType(ButtonSubmit.TYPE_LABEL);
		if (orbType == null) throw new RuntimeException("Type does not exist in database yet.");
		
		create(div, orbType);
	}

	@Override
	protected ButtonSubmitTransformer getTransformer() {
		return dropDownListboxTransformer;
	}
	
	@Override
	protected void setNonChildrenAttributes(ButtonSubmit ddl, Orb orb) {
		orb.getUserDefinedProperties().put(ButtonSubmit.ATTR_STYLE, ddl.style);
		orb.getUserDefinedProperties().put(ButtonSubmit.ATTR_NAME, ddl.name);
		orb.getUserDefinedProperties().put(ButtonSubmit.ATTR_LABEL, ddl.label);
	}
}


