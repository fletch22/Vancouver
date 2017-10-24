package com.fletch22.app.designer.div;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.dao.AppDesignerDao;
import com.fletch22.app.designer.submit.ButtonSubmit;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;

@Component
public class DivDao extends AppDesignerDao<Div, DivTransformer> {

	Logger logger = LoggerFactory.getLogger(DivDao.class);

	@Autowired
	DivTransformer divTransformer;

	@Override
	protected void create(Div div) {
		OrbType orbType = this.orbTypeManager.getOrbType(Div.TYPE_LABEL);
		if (orbType == null) throw new RuntimeException("Type does not exist in database yet.");
		
		create(div, orbType);
	}

	@Override
	protected DivTransformer getTransformer() {
		return divTransformer;
	}
	
	@Override
	protected void setNonChildrenAttributes(Div div, Orb orb) {
		orb.getUserDefinedProperties().put(Div.ATTR_STYLE, div.style);
		orb.getUserDefinedProperties().put(Div.ATTR_ORDINAL, div.ordinal);
	}
}


