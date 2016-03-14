package com.fletch22.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.app.AppTransformer;
import com.fletch22.app.designer.appContainer.AppContainerTransformer;

@Component
public class TransformerDocks {

	@Autowired
	public AppContainerTransformer appContainerTransformer;
	
	@Autowired
	public AppTransformer appTransformer;
}
