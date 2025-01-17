package com.fletch22.app.designer;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fletch22.app.designer.reference.ReferenceResolverService;

public abstract class DomainService<T extends Parent, U extends Child> extends DomainServiceBase<T> {

	Logger logger = LoggerFactory.getLogger(DomainService.class);

	@Autowired
	ReferenceResolverService referenceResolver;

	@Override
	public abstract void save(T t);

	@Override
	public abstract T get(long id);

	public void addToParent(T parent, U child, long index) {
		connectParentAndChild(parent, child, index);
		save(parent);
	}

	public Child removeChildFromParent(long parentId, long childId) {
		T t = (T) this.get(parentId);
		this.clearAndResolveNextGeneration(t);
		ComponentChildren children = t.getChildren();
		
		logger.info("Parent ID: {}", parentId);

		Child child = children.findChildById(childId);
		children.removeChild(child);
		save(t);

		return child;
	}
	
	public void clearAndResolveAllDescendents(Parent orbBasedComponent) {
		referenceResolver.clearAndResolveAllDescendents(orbBasedComponent);
	}

	public void clearAndResolveNextGeneration(Parent orbBasedComponent) {
		referenceResolver.clearAndResolveNextGeneration(orbBasedComponent);
	}

	protected void connectParentAndChild(T parent, U child, long index) {
		clearAndResolveNextGeneration(parent);
		logger.debug("cpac: {}", parent.getChildren().isHaveChildrenBeenResolved());
		child.setParentId(parent.getId());
		parent.getChildren().addChildAtOrdinal(child, index);
	}

	public abstract T createInstance(Map<String, String> props);

	public abstract T update(long id, Map<String, String> properties);
}
