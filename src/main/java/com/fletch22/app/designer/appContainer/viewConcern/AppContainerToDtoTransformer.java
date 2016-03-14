package com.fletch22.app.designer.appContainer.viewConcern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.appContainer.AppContainer;
import com.fletch22.app.designer.viewConcern.ChildrenDtoTransformer;

@Component
public class AppContainerToDtoTransformer {
	
	@Autowired
	ChildrenDtoTransformer childrenDtoTransformer;

	public AppContainerDto transform(AppContainer appContainer) {
		AppContainerDto appContainerDto = new AppContainerDto();
		
		appContainerDto.label= appContainer.label;
		appContainerDto.id = appContainer.getId();
		appContainerDto.parentId = appContainer.getParentId();
		appContainerDto.childrenDto = childrenDtoTransformer.transform(appContainer.getChildren());		
		
		return appContainerDto;
	}
}
