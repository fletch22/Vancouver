package com.fletch22.app.designer.userData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.app.designer.Child;
import com.fletch22.app.designer.ComponentChildren;
import com.fletch22.app.designer.dataField.DataField;
import com.fletch22.app.designer.dataModel.DataModel;
import com.fletch22.app.designer.dataModel.DataModelService;
import com.fletch22.orb.OrbManager;
import com.fletch22.orb.OrbType;
import com.fletch22.orb.OrbTypeManager;



@Component
public class ModelToUserDataTranslator {
	
	private static final Logger logger = LoggerFactory.getLogger(ModelToUserDataTranslator.class);
	
	private static final String ORB_TYPE_PREFIX = "userDataType_";
	
	@Autowired
	OrbManager orbManager;
	
	@Autowired
	OrbTypeManager orbTypeManager;
	
	@Autowired
	DataModelService dataModelService;
	
	public void createUserData(DataModel dataModel) {
		
		LinkedHashSet<String> customFields = new LinkedHashSet<>();
		
		ComponentChildren componentChildren = dataModel.getChildren();
		ArrayList<Child> children = componentChildren.getList();
		logger.info("Children length: {}", children.size());
		
		if (children != null && children.size() > 0) {
			children.forEach((child)->{
				if (child.getClass() == DataField.class) {
					DataField dataField = (DataField) child;
					customFields.add(dataField.label);
				} else {
					String message = String.format("Encountered error while trying to create user data for dataModel %s. Handling '%s' is not yet implemented.", dataModel.label, child.getClass().getName());
					throw new RuntimeException(message);
				}
			});
		}
		
		orbTypeManager.createOrbType(composeUserDataTypeLabel(dataModel), customFields);
	}
	
	public void updateUserData(DataModel dataModel) {
		OrbType orbTypeUserData = orbTypeManager.getOrbType(this.composeUserDataTypeLabel(dataModel));
		
		deleteMissingAttributes(dataModel, orbTypeUserData);
		
		appendMissingAttributes(dataModel, orbTypeUserData);
	}

	private void appendMissingAttributes(DataModel dataModel, OrbType orbTypeUserData) {
		ComponentChildren componentChildren = dataModel.getChildren();
		ArrayList<Child> children = componentChildren.getList();
		logger.info("Children length: {}", children.size());
		
		if (children != null && children.size() > 0) {
			LinkedHashSet<String> customFields = orbTypeUserData.customFields;
			appendMissingAttr(dataModel, orbTypeUserData, children, customFields);
		}
	}

	private void appendMissingAttr(DataModel dataModel, OrbType orbTypeUserData, ArrayList<Child> children, LinkedHashSet<String> customFields) {
		children.forEach((child)->{
			if (child.getClass() == DataField.class) {
				DataField dataField = (DataField) child;
				if (!customFields.contains(dataField.label)) {
					orbTypeManager.addAttribute(orbTypeUserData.id, dataField.label);
				}
			} else {
				String message = String.format("Encountered error while trying to update user data for dataModel %s. Handling '%s' is not yet implemented.", dataModel.label, child.getClass().getName());
				throw new RuntimeException(message);
			}
		});
	}

	private void deleteMissingAttributes(DataModel dataModel, OrbType orbTypeUserData) {
		LinkedHashSet<String> customFields = orbTypeUserData.customFields;
		ComponentChildren componentChildren = dataModel.getChildren();
		ArrayList<Child> children = componentChildren.getList();
		List<String> deleteList = new ArrayList<>();
		if (children != null && children.size() > 0) {
			deleteList = getDeleteList(dataModel, customFields, children);
		}
		
		for (String attributeName : deleteList) {
			orbTypeManager.deleteAttribute(orbTypeUserData.id, attributeName, true);
		};
	}

	private List<String> getDeleteList(DataModel dataModel, LinkedHashSet<String> customFields, ArrayList<Child> children) {
		List<String> deleteList = new ArrayList<>();
		
		Map<String, Child> attributeMap = new HashMap<>();
		for (Child child : children) {
			if (child.getClass() == DataField.class) {
				DataField dataField = (DataField) child;
				attributeMap.put(dataField.label, child);
			} else {
				String message = String.format("Encountered error while trying to update user data for dataModel %s. Handling '%s' is not yet implemented.", dataModel.label, child.getClass().getName());
				throw new RuntimeException(message);
			}
		}
		
		for (String field: customFields) { 
			if (!attributeMap.containsKey(field)) {
				deleteList.add(field);
			}
		}
		return deleteList;
	}
	
	private String composeUserDataTypeLabel(DataModel dataModel) {
		return ORB_TYPE_PREFIX + String.valueOf(dataModel.getId());
	}
	
	private OrbType getUserDataType(DataModel dataModel) {
		String userDataLabel = this.composeUserDataTypeLabel(dataModel);
		return orbTypeManager.getOrbType(userDataLabel); 
	}
	
	private DataModel getDataFieldParent(DataField dataField) {
		return dataModelService.get(dataField.getParentId());
	}

	public void ensureNameSyncedWithUserDataType(DataField dataField) {
		String originalLabel = dataField.getOrbOriginal().getUserDefinedProperties().get(DataField.ATTR_LABEL);
		if (!dataField.label.equals(originalLabel)) {
			DataModel dataModel = this.getDataFieldParent(dataField);
			OrbType orbTypeUserData = this.getUserDataType(dataModel);
			orbTypeManager.renameAttribute(orbTypeUserData.id, originalLabel, dataField.label);
		}
	}
}
