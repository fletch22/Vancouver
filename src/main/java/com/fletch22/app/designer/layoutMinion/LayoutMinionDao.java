package com.fletch22.app.designer.layoutMinion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.dao.AppDesignerDao;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;

@Component
public class LayoutMinionDao extends AppDesignerDao<LayoutMinion, LayoutMinionTransformer> {

	Logger logger = LoggerFactory.getLogger(LayoutMinionDao.class);

	@Autowired
	LayoutMinionTransformer layoutTransformer;

	@Override
	protected void create(LayoutMinion layout) {
		OrbType orbType = this.orbTypeManager.getOrbType(LayoutMinion.TYPE_LABEL);

		if (orbType == null) throw new RuntimeException("Type does not exist in database yet.");
		
		create(layout, orbType);
	}

	@Override
	protected LayoutMinionTransformer getTransformer() {
		return layoutTransformer;
	}
	
	@Override
	protected void setNonChildrenAttributes(LayoutMinion layoutMinion, Orb orb) {
		orb.getUserDefinedProperties().put(LayoutMinion.ATTR_HEIGHT, layoutMinion.height);
		orb.getUserDefinedProperties().put(LayoutMinion.ATTR_WIDTH, layoutMinion.width);
		orb.getUserDefinedProperties().put(LayoutMinion.ATTR_X, layoutMinion.x);
		orb.getUserDefinedProperties().put(LayoutMinion.ATTR_Y, layoutMinion.y);
		orb.getUserDefinedProperties().put(LayoutMinion.ATTR_KEY, layoutMinion.key);
		orb.getUserDefinedProperties().put(LayoutMinion.ATTR_STYLE, layoutMinion.style);
	}
}


