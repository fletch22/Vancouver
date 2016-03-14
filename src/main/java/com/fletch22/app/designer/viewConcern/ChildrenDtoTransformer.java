package com.fletch22.app.designer.viewConcern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.Child;
import com.fletch22.app.designer.ComponentChildren;

@Component
public class ChildrenDtoTransformer {
	
	@Autowired
	TransformerFactory transformerFactory;

	public ChildrenDto transform(ComponentChildren componentChildren) {
		
		ChildrenDto childrenDto = new ChildrenDto();
		
		if (componentChildren.isHaveChildrenBeenResolved()) {
			childrenDto.isHaveChildrenBeenResolved = true;
			for (Child child: componentChildren.getList()) {
				DtoTransformer dtoTransformer = transformerFactory.getInstance(child);
				ChildDto childDto = dtoTransformer.transform(child);
				childrenDto.children.add(childDto);
			}
		} else {
			childrenDto.isHaveChildrenBeenResolved = false;
		}
		
		return childrenDto;
	}
}
