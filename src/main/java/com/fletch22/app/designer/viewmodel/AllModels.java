package com.fletch22.app.designer.viewmodel;

import java.util.ArrayList;

import com.fletch22.app.designer.app.App;
import com.fletch22.app.designer.appContainer.AppContainer;
import com.google.gson.annotations.Expose;

// NOTE: 04-19-2016: Is this going to be useful? Figure out if I need it.
public class AllModels {

	public final static String ID = "id";
	public final static String TYPE_LABEL = "typeLabel";
	
	@Expose
	public ArrayList<String> appContainer = new ArrayList<String>();
	
	@Expose
	public ArrayList<String> app = new ArrayList<String>();
	
	public AllModels() {
		init(appContainer);
		appContainer.addAll(AppContainer.ATTRIBUTE_LIST);
		
		init(app);
		app.addAll(App.ATTRIBUTE_LIST);
	}
	
	public void init(ArrayList<String> arrayList) {
		arrayList.add(ID);
		arrayList.add(TYPE_LABEL);
	}
}
