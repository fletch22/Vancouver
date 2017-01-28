package com.fletch22.app.designer.layout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.dao.AppDesignerDao;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbType;

@Component
public class LayoutDao extends AppDesignerDao<Layout, LayoutTransformer> {

	Logger logger = LoggerFactory.getLogger(LayoutDao.class);

	@Autowired
	LayoutTransformer layoutTransformer;

	@Override
	protected void create(Layout layout) {
		OrbType orbType = this.orbTypeManager.getOrbType(Layout.TYPE_LABEL);

		if (orbType == null) throw new RuntimeException("Type does not exist in database yet.");
		
		create(layout, orbType);
	}

	@Override
	protected LayoutTransformer getTransformer() {
		return layoutTransformer;
	}
	
	@Override
	protected void setNonChildrenAttributes(Layout page, Orb orb) {
		// NOTE: 01-26-2017: Nothing to do
	}
}

