package com.fletch22.app.state.diff.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.aop.Transactional;
import com.fletch22.app.designer.Child;
import com.fletch22.app.designer.DomainService;
import com.fletch22.app.designer.Parent;
import com.fletch22.app.designer.ServiceFactory;
import com.fletch22.app.designer.ServiceJunction;
import com.fletch22.app.designer.dao.BaseDao;
import com.fletch22.app.designer.reference.ReferenceResolverService;
import com.fletch22.app.designer.util.DomainUtilDao;
import com.fletch22.orb.Orb;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;
import com.fletch22.web.controllers.ComponentController.MoveCommand;

@Component
public class MoveService {

	Logger logger = LoggerFactory.getLogger(MoveService.class);

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

	@Autowired
	ReferenceResolverService referenceResolverService;

	@Transactional
	public void move(MoveCommand moveCommand) {
		logger.info("Attempting to move the item.");

		Orb movingChildParent = orbManager.getOrb(moveCommand.sourceParentId);
		OrbType movingChildParentOrbType = orbTypeManager.getOrbType(movingChildParent.getOrbTypeInternalId());

		DomainService domainService = serviceFactory.getServiceFromTypeLabel(movingChildParentOrbType.label);
		Child child = domainService.removeChildFromParent(moveCommand.sourceParentId, moveCommand.childId);

		logger.info("Source: {}; Child: {}", moveCommand.sourceParentId, moveCommand.childId);

		logger.info("Destination: {}", moveCommand.destinationParentId);

		Orb destinationChildParent = orbManager.getOrb(moveCommand.destinationParentId);
		OrbType destinationParentOrbType = orbTypeManager.getOrbType(destinationChildParent.getOrbTypeInternalId());

		domainService = serviceFactory.getServiceFromTypeLabel(destinationParentOrbType.label);
		Parent parent = domainService.get(moveCommand.destinationParentId);

		domainService.addToParent(parent, child, moveCommand.ordinalChildTarget);
	}
}
