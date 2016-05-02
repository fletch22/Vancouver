package com.fletch22.app.state.diff.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.aop.Transactional;
import com.fletch22.app.state.diff.AddedChild;
import com.fletch22.app.state.diff.Child;
import com.fletch22.app.state.diff.DeletedChild;
import com.fletch22.app.state.diff.Diff;
import com.fletch22.app.state.diff.DiffKind;
import com.fletch22.app.state.diff.EditedProperty;
import com.fletch22.app.state.diff.ParentAndChild;
import com.fletch22.util.json.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@Component
public class JsonDiffProcessorService {
	
	Logger logger = LoggerFactory.getLogger(JsonDiffProcessorService.class);
	
	private static final String EX_MSG_NO_RECOGNIZE_DIFF_CODE = "Encountered problem. Did not recognize diff code: %s";
	private static final String TYPE_LABEL = "typeLabel";
	private static final String EX_MSG_UNSUPPORTED_NEW_ADD = "Encountered problem while trying to process diff state - cannot add item outside of array object addition. Not yet implemented.";
	private static final String EX_MSG_DIFF_KIND_UNSUPPORTED = "Encountered problem while trying to process diff state - diff kind is unsupported";
	
	public enum FamilyMember {
		Parent,
		Child
	}

	@Autowired
	GsonFactory gsonFactory;
	
	@Autowired
	AddChildService addChildService;

	@Transactional
	public void process(String state, String jsonArrayDiff) {
		
		logger.debug(jsonArrayDiff.toString());

		Gson gson = gsonFactory.getInstance();
		JsonObject jsonObjectState = gson.fromJson(state, JsonObject.class);
		
		JsonObject jsonObjectModelState = jsonObjectState.get("model").getAsJsonObject();
		logger.debug(jsonObjectModelState.toString());
		
		JsonArray jsonArray = gson.fromJson(jsonArrayDiff, JsonArray.class);

		logger.debug("size of jsonArray: {}", jsonArray.size());

		for (int i = 0; i < jsonArray.size(); i++) {
			JsonElement jsonElement = jsonArray.get(i);
			processDiff(jsonObjectModelState, jsonElement);
		}
	}

	private void processDiff(JsonObject jsonObjectState, JsonElement jsonElement) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		String letter = jsonObject.get("kind").getAsJsonPrimitive().getAsString();

		JsonArray pathInformation = jsonObject.get("path").getAsJsonArray();

		DiffKind diffKind = getKind(letter);

		processDiffKind(jsonObjectState, jsonObject, pathInformation, diffKind);
	}

	private void processDiffKind(JsonObject jsonObjectState, JsonObject jsonObject, JsonArray pathInformation, DiffKind diffKind) {
		switch (diffKind) {
			case DELETED_THING:
				JsonElement deletedChild = jsonObject.get("lhs");
				processDeleteProperty(jsonObjectState, pathInformation, deletedChild);
				break;
			case EDITED_PROPERTY:
				logger.debug("Got edited property.");
				JsonElement newValue = jsonObject.get("rhs");
				processEditedProperty(jsonObjectState, pathInformation, newValue);
				break;
			case ARRAY_CHANGE:
				JsonElement item = jsonObject.get("item");
				JsonElement index = jsonObject.get("index");
				processChangedArray(jsonObjectState, pathInformation, index, item);
				break;
			case NEWLY_ADDED_PROPERTY:
				throw new RuntimeException(EX_MSG_UNSUPPORTED_NEW_ADD);
			default:
				throw new RuntimeException(EX_MSG_DIFF_KIND_UNSUPPORTED);
		}
	}

	private void processEditedProperty(JsonObject state, JsonArray pathInformation, JsonElement newValue) {
		EditedProperty editPropertyInfo = getEditedPropertyInfo(state, pathInformation, newValue);
	}
	
	protected void processDeleteProperty(JsonObject state, JsonArray pathInformation, JsonElement deletedChild) {
		ParentAndChild parentAndChild = getDeletePropertyInfo(state, pathInformation, deletedChild);
	}

	private void processAddedChild(JsonObject state, JsonArray pathInformation, long index, JsonElement jsonElementChild) {
		
		logger.debug(state.toString());
		
		JsonElement parentElement = getParentDescribedByPath(pathInformation, state);
		
		Child child = getChild(jsonElementChild);
		long parentId = getId(parentElement);
		
		AddedChild addedChild = new AddedChild(parentId, child);
		
		addChildService.process(addedChild);
	}
	
	private void processDeletedChild(JsonObject state, JsonArray pathInformation, long index, JsonElement deletedChildElement) {
		DeletedChild deletedChild = getDeletedChildInfo(state, pathInformation, index, deletedChildElement);
	}
	
	protected void processChangedArray(JsonObject state, JsonArray pathInformation, JsonElement jsonElementIndex, JsonElement item) {
		JsonElement jsonElement = (JsonElement) state;
		jsonElement = getParentDescribedByPath(pathInformation, jsonElement);

		JsonObject jsonObject = (JsonObject) item;

		long index = jsonElementIndex.getAsLong();

		String letter = jsonObject.get("kind").getAsJsonPrimitive().getAsString();

		Diff diff = new Diff();
		diff.diffKind = getKind(letter);

		switch (diff.diffKind) {
		case DELETED_THING:
			JsonElement deletedChild = jsonObject.get("lhs");
			processDeletedChild(state, pathInformation, index, deletedChild);
			break;
		case EDITED_PROPERTY:
			throw new RuntimeException("Encountered problem while trying to process changed array. The changed array had an edited element. Handling this type of operation is not yet implemented.");
		case ARRAY_CHANGE:
			throw new RuntimeException("Encountered problem while trying to process changed array. The changed array had an edited element. Handling this type of operation is not yet implemented.");
		case NEWLY_ADDED_PROPERTY:
			JsonElement addedChild = jsonObject.get("rhs");
			processAddedChild(state, pathInformation, index, addedChild);
			break;
		default:
			throw new RuntimeException(EX_MSG_DIFF_KIND_UNSUPPORTED);
		}
	}
	
	private EditedProperty getEditedPropertyInfo(JsonObject state, JsonArray pathInformation, JsonElement jsonElementNewValue) {
		JsonElement parentElement = (JsonElement) state;
		
		JsonElement childElement = getParentDescribedByPath(pathInformation, parentElement);
		
		long id = getId(childElement);
		String property = getLastPathItemValue(pathInformation);
		String newValue = jsonElementNewValue.getAsJsonPrimitive().getAsString();
		
		return new EditedProperty(id, property, newValue);
	}

	private ParentAndChild getDeletePropertyInfo(JsonObject state, JsonArray pathInformation, JsonElement deletedChild) {
		JsonElement parentElement = (JsonElement) state;
		parentElement = getParentDescribedByPath(pathInformation, parentElement);

		return getParentAndChild(parentElement, deletedChild);
	}

	private Child getChild(JsonElement jsonElementChild) {
		
		logger.debug(jsonElementChild.toString());
		
		String key = null;
		Map<String, String> properties = new HashMap<String, String>();
		
		if (!jsonElementChild.isJsonObject()) {
			throw new RuntimeException("Encountered problem while trying to get the properties from a newly added object. Was expecting an object. Found something else. This is not allowed.");
		}
		
		for (Entry<String, JsonElement> entry : jsonElementChild.getAsJsonObject().entrySet()) {
			key = entry.getKey();
			JsonElement jsonElement = entry.getValue();
			if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isString()) {
					properties.put(key, jsonPrimitive.getAsString());
				} else {
					throw new RuntimeException("Encountered problem while trying to get the properties from a newly added object. Encountered an object property whose value was not a string. This is not allowed. Added objects must only have string valued properties.");
				}
			} else {
				throw new RuntimeException("Encountered problem while trying to get the properties from a newly added object. Encountered an object property whose value was not a primitive. This is not allowed. Added objects must only have string valued properties.");
			}
		}
		String type = properties.remove(TYPE_LABEL);
		
		return new Child(type, properties);
	}
	
	private DeletedChild getDeletedChildInfo(JsonObject state, JsonArray pathInformation, long index, JsonElement deletedChildElement) {
		
		JsonElement parentElement = getParentDescribedByPath(pathInformation, state);
		
		DeletedChild deletedChild = new DeletedChild();
		deletedChild.parentAndChild = getParentAndChild(parentElement, deletedChildElement);
		deletedChild.index = index;
		
		return deletedChild;
	}
	
	private JsonElement getParentDescribedByPath(JsonArray pathInformation, JsonElement element) {
		return getNodeDescribedByPath(pathInformation, element, FamilyMember.Parent);
	}
	
	private JsonElement getChildDescribedByPath(JsonArray pathInformation, JsonElement element) {
		return getNodeDescribedByPath(pathInformation, element, FamilyMember.Child);
	}

	private JsonElement getNodeDescribedByPath(JsonArray pathInformation, JsonElement jsonElement, FamilyMember familyMember) {
		
		logger.debug("jsonElement: " + jsonElement.toString());
		
		int depthOfTraversal = getDepthOfPathTraversal(pathInformation, familyMember);
		
		for (int i = 0; i < depthOfTraversal; i++) {
			JsonElement pathElement = pathInformation.get(i);

			if (pathElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = pathElement.getAsJsonPrimitive();
				if (jsonPrimitive.isNumber()) {
					jsonElement = jsonElement.getAsJsonArray().get(jsonPrimitive.getAsInt());
				} else {
					String memberName = jsonPrimitive.getAsString();
					jsonElement = jsonElement.getAsJsonObject().get(memberName);
				}
			} else {
				throw new RuntimeException("Encountered problem reading diff array. Encountered an array item that was not a primitive -- inconceivable!");
			}
		}
		
		return jsonElement;
	}

	private int getDepthOfPathTraversal(JsonArray pathInformation, FamilyMember familyMember) {
		int depthOfTraversal = 0;
		switch (familyMember) { 
			case Parent:
				depthOfTraversal = pathInformation.size() - 1;
				break;
			case Child:
				depthOfTraversal = pathInformation.size();
				break;
			default:
				throw new RuntimeException("Encoutered problem processing type of family member to search through path information.");
		}
		return depthOfTraversal;
	}

	private ParentAndChild getParentAndChild(JsonElement parentElement, JsonElement childElement) {
		long parentId;
		long childId;
		try {
			parentId = getId(parentElement);
			childId = childElement.getAsJsonObject().get("id").getAsLong();
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem trying to get parent ID or child ID. Check JSON validity.");
		}
		ParentAndChild parentAndChild = new ParentAndChild(parentId, childId);
		return parentAndChild;
	}

	private long getId(JsonElement jsonElement) {
		return jsonElement.getAsJsonObject().get("id").getAsLong();
	}

	public DiffKind getKind(String letter) {

		DiffKind diffKind = null;
		switch (letter) {
		case "N":
			diffKind = DiffKind.NEWLY_ADDED_PROPERTY;
			break;
		case "D":
			diffKind = DiffKind.DELETED_THING;
			break;
		case "E":
			diffKind = DiffKind.EDITED_PROPERTY;
			break;
		case "A":
			diffKind = DiffKind.ARRAY_CHANGE;
			break;
		default:
			throw new RuntimeException(String.format(EX_MSG_NO_RECOGNIZE_DIFF_CODE, letter));
		}
		return diffKind;
	}
	
	public String getLastPathItemValue(JsonArray pathInformation) {
		
		JsonPrimitive jsonPrimitive = pathInformation.get(pathInformation.size() - 1).getAsJsonPrimitive();
		if (jsonPrimitive.isNumber()) {
			throw new RuntimeException("Encountered problem while trying to get the last item in the path. Unable to process element that is not a string. Feature not yet implemented.");
		}
		
		return jsonPrimitive.getAsString();
	}
}
