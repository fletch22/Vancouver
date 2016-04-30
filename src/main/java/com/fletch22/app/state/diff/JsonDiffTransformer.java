package com.fletch22.app.state.diff;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fletch22.util.json.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

@Component
public class JsonDiffTransformer {

	Logger logger = LoggerFactory.getLogger(JsonDiffTransformer.class);

	@Autowired
	GsonFactory gsonFactory;

	public ArrayList<Diff> transform(String state, String jsonDiff) {

		Gson gson = gsonFactory.getInstance();
		JsonObject jsonObjectState = gson.fromJson(state, JsonObject.class);
		ArrayList<Diff> diffList = new ArrayList<Diff>();

		JsonArray jsonArray = gson.fromJson(jsonDiff, JsonArray.class);

		logger.debug("size of jsonArray: {}", jsonArray.size());

		for (int i = 0; i < jsonArray.size(); i++) {
			JsonElement jsonElement = jsonArray.get(i);
			JsonObject jsonObject = jsonElement.getAsJsonObject();

			String letter = jsonObject.get("kind").getAsJsonPrimitive().getAsString();

			JsonArray pathInformation = jsonObject.get("path").getAsJsonArray();

			Diff diff = new Diff();
			diff.diffKind = getKind(letter);

			switch (diff.diffKind) {
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
			case NEWLY_ADDED_THING:
				break;
			default:
				throw new RuntimeException("Encountered problem while trying to process diff state - did not recognize diff kind.");
			}

			diffList.add(diff);
		}

		return diffList;
	}

	private void processEditedProperty(JsonObject state, JsonArray pathInformation, JsonElement newValue) {
		EditPropertyInfo editPropertyInfo = getEditedPropertyInfo(state, pathInformation, newValue);
	}
	
	private EditPropertyInfo getEditedPropertyInfo(JsonObject state, JsonArray pathInformation, JsonElement jsonElementNewValue) {
		JsonElement parentElement = (JsonElement) state;
		
		JsonElement childElement = getParentDescribedByPath(pathInformation, parentElement);
		
		long id = getId(childElement);
		String property = getLastPathItemValue(pathInformation);
		String newValue = jsonElementNewValue.getAsJsonPrimitive().getAsString();
		
		return new EditPropertyInfo(id, property, newValue);
	}
	
	public String getLastPathItemValue(JsonArray pathInformation) {
		
		JsonPrimitive jsonPrimitive = pathInformation.get(pathInformation.size() - 1).getAsJsonPrimitive();
		if (jsonPrimitive.isNumber()) {
			throw new RuntimeException("Encountered problem while trying to get the last item in the path. Unable to process element that is not a string. Feature not yet implemented.");
		}
		
		return jsonPrimitive.getAsString();
	}

	protected void processDeleteProperty(JsonObject state, JsonArray pathInformation, JsonElement deletedChild) {
		ParentAndChild parentAndChild = getDeletePropertyInfo(state, pathInformation, deletedChild);
	}

	private ParentAndChild getDeletePropertyInfo(JsonObject state, JsonArray pathInformation, JsonElement deletedChild) {
		JsonElement parentElement = (JsonElement) state;
		parentElement = getParentDescribedByPath(pathInformation, parentElement);

		return getParentAndChild(parentElement, deletedChild);
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
		case NEWLY_ADDED_THING:
			break;
		default:
			throw new RuntimeException("Encountered problem while trying to process diff state - did not recognize diff kind.");
		}
	}

	private void processDeletedChild(JsonObject state, JsonArray pathInformation, long index, JsonElement deletedChildElement) {
		DeletedChild deletedChild = getDeletedChildInfo(state, pathInformation, index, deletedChildElement);
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
		
		int size = 0;
		switch (familyMember) { 
			case Parent:
				size = pathInformation.size() - 1;
				break;
			case Child:
				size = pathInformation.size();
				break;
			default:
				throw new RuntimeException("Encoutered problem processing type of family member to search through path information.");
		}
		
		for (int i = 0; i < size; i++) {
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
			diffKind = DiffKind.NEWLY_ADDED_THING;
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
			String message = String.format("Encountered problem. Did not recognize diff code: %s", letter);
			throw new RuntimeException(message);
		}
		return diffKind;
	}

	public class ParentAndChild {
		public long parentId;
		public long childId;

		public ParentAndChild(long parentId, long childId) {
			this.parentId = parentId;
			this.childId = childId;
		}
	}
	
	public class DeletedChild {
		public ParentAndChild parentAndChild;
		public long index;
	}
	
	public class EditPropertyInfo {
		
		public long id;
		public String property;
		public String newValue;
		
		public EditPropertyInfo(long id, String property, String newValue) {
			this.id = id;
			this.property = property;
			this.newValue = newValue;
		}
	}
	
	public enum FamilyMember {
		Parent,
		Child
	}
}
