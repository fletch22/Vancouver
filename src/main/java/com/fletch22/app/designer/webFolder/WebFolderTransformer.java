package com.fletch22.app.designer.webFolder;

import org.springframework.stereotype.Component;

import com.fletch22.app.designer.DomainTransformer;
import com.fletch22.orb.Orb;

@Component
public class WebFolderTransformer extends DomainTransformer {
	
	public WebFolder transform(Orb orb) {
		
		WebFolder webFolder = new WebFolder();
		
		this.setBaseAttributes(orb, webFolder);
		webFolder.label = orb.getUserDefinedProperties().get(WebFolder.ATTR_LABEL);
		
		return webFolder;
	}
}
