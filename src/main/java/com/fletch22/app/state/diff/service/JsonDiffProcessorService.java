package com.fletch22.app.state.diff.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.NotImplementedException;
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

	private static final String MODEL_NODE_NAME = "model";

	private static final String PROPERTY_PARENT_ID = "parentId";

	private static final String PROPERTY_ID = "id";

	Logger logger = LoggerFactory.getLogger(JsonDiffProcessorService.class);

	private static final String EX_MSG_NO_RECOGNIZE_DIFF_CODE = "Encountered problem. Did not recognize diff code: %s";
	private static final String PROPERTY_TYPE_LABEL = "typeLabel";
	private static final String EX_MSG_UNSUPPORTED_NEW_ADD = "Encountered problem while trying to process diff state - cannot add item outside of array object addition. Not yet implemented.";
	private static final String EX_MSG_DIFF_KIND_UNSUPPORTED = "Encountered problem while trying to process diff state - diff kind is unsupported";

	private static final long UNSET_PARENT_ID = -1;

	public enum FamilyMember {
		Parent, Child
	}

	@Autowired
	GsonFactory gsonFactory;

	@Autowired
	AddChildService addChildService;

	@Autowired
	DeleteService deleteService;
	
	@Autowired
	EditObjectService editObjectService;

	private Gson gson = new Gson();

	@Transactional
	public ArrayList<StuntDoubleAndNewId> process(String state, String jsonArrayDiff) {

		JsonArray jsonArray = gson.fromJson(jsonArrayDiff, JsonArray.class);

		ArrayList<StuntDoubleAndNewId> stuntDoubleAndNewIdList = new ArrayList<StuntDoubleAndNewId>();
		for (int i = 0; i < jsonArray.size(); i++) {
			JsonElement jsonElement = jsonArray.get(i);
			ResultDiffProcessing resultDiffProcessing = processDiff(state, jsonElement);
			if (resultDiffProcessing.hasStuntDoubleAndNewId()) {
				stuntDoubleAndNewIdList.add(resultDiffProcessing.stuntDoubleAndNewId);
			}
		}
		return stuntDoubleAndNewIdList;
	}

	private ResultDiffProcessing processDiff(String state, JsonElement jsonElement) {
		JsonObject jsonObject = jsonElement.getAsJsonObject();

		String letter = jsonObject.get("kind").getAsJsonPrimitive().getAsString();

		JsonArray pathInformation = jsonObject.get("path").getAsJsonArray();

		DiffKind diffKind = getKind(letter);

		return processDiffKind(state, jsonObject, pathInformation, diffKind);
	}

	private ResultDiffProcessing processDiffKind(String state, JsonObject jsonObject, JsonArray pathInformation, DiffKind diffKind) {
		ResultDiffProcessing resultDiffProcessing = new ResultDiffProcessing();

		switch (diffKind) {
		case DELETED_PROPERTY:
			JsonElement deletedChild = jsonObject.get("lhs");
			processDeleteProperty(state, pathInformation, deletedChild);
			break;
		case EDITED_PROPERTY:
			JsonElement newValue = jsonObject.get("rhs");
			processEditedProperty(state, pathInformation, newValue);
			break;
		case ARRAY_CHANGE:
			JsonElement item = jsonObject.get("item");
			JsonElement index = jsonObject.get("index");
			resultDiffProcessing = processChangedArray(state, pathInformation, index, item);
			break;
		case NEWLY_ADDED_PROPERTY:
			throw new RuntimeException(EX_MSG_UNSUPPORTED_NEW_ADD);
		default:
			throw new RuntimeException(EX_MSG_DIFF_KIND_UNSUPPORTED);
		}
		return resultDiffProcessing;
	}

	private void processEditedProperty(String state, JsonArray pathInformation, JsonElement newValue) {
		// throw new NotImplementedException("processDeleteProperty not implemented yet. (Set to null?)");
		EditedProperty editPropertyInfo = getEditedPropertyInfo(state, pathInformation, newValue);
		
		editObjectService.process(editPropertyInfo);
	}

	protected void processDeleteProperty(String state, JsonArray pathInformation, JsonElement deletedChild) {
		throw new NotImplementedException("processDeleteProperty not implemented yet. (Set to null?)");
		// ParentAndChild parentAndChild = getDeletePropertyInfo(state,
		// pathInformation, deletedChild);
	}

	private StuntDoubleAndNewId processAddedChild(JsonObject state, JsonArray pathInformation, long index, JsonElement jsonElementChild) {

		JsonElement parentElement = getParentDescribedByPath(pathInformation, state);

		Child child = getChild(jsonElementChild);
		String temporaryId = child.props.remove(PROPERTY_ID);
		long parentId = getId(parentElement);

		AddedChild addedChild = new AddedChild(parentId, child, temporaryId);

		long childNewId = addChildService.process(addedChild);

		return new StuntDoubleAndNewId(temporaryId, childNewId);
	}

	private StuntDoubleAndNewId processAddedChildNew(JsonArray pathInformation, long index, JsonElement jsonElementChild) {

		Child child = getChild(jsonElementChild);
		String temporaryId = child.props.remove(PROPERTY_ID);
		long parentId = child.parentId;

		AddedChild addedChild = new AddedChild(parentId, child, temporaryId);

		long childNewId = addChildService.process(addedChild);

		return new StuntDoubleAndNewId(temporaryId, childNewId);
	}

	private void processDeletedChild(JsonObject state, JsonArray pathInformation, long index, JsonElement deletedChildElement) {
		DeletedChild deletedChild = getDeletedChildInfo(state, pathInformation, index, deletedChildElement);

		deleteService.process(deletedChild.parentAndChild.childId);
	}

	protected ResultDiffProcessing processChangedArray(String state, JsonArray pathInformation, JsonElement jsonElementIndex, JsonElement item) {
		ResultDiffProcessing resultProcessChangedArray = new ResultDiffProcessing();
		long index;

		JsonObject jsonObject = (JsonObject) item;
		String letter = jsonObject.get("kind").getAsJsonPrimitive().getAsString();

		Diff diff = new Diff();
		diff.diffKind = getKind(letter);

		switch (diff.diffKind) {
		case DELETED_PROPERTY:
			index = jsonElementIndex.getAsLong();
			JsonElement deletedChild = jsonObject.get("lhs");

			Gson gson = new Gson();
			JsonObject joState = gson.fromJson(state, JsonObject.class);
			processDeletedChild(joState, pathInformation, index, deletedChild);
			break;
		case EDITED_PROPERTY:
			throw new RuntimeException(
					"Encountered problem while trying to process changed array. The changed array had an edited element. Handling this type of operation is not yet implemented.");
		case ARRAY_CHANGE:
			throw new RuntimeException(
					"Encountered problem while trying to process changed array. The changed array had an edited element. Handling this type of operation is not yet implemented.");
		case NEWLY_ADDED_PROPERTY:
			index = jsonElementIndex.getAsLong();
			JsonElement addedChild = jsonObject.get("rhs");
			resultProcessChangedArray.stuntDoubleAndNewId = processAddedChildNew(pathInformation, index, addedChild);
			break;
		default:
			throw new RuntimeException(EX_MSG_DIFF_KIND_UNSUPPORTED);
		}

		return resultProcessChangedArray;
	}

	public class ResultDiffProcessing {
		public StuntDoubleAndNewId stuntDoubleAndNewId;

		public boolean hasStuntDoubleAndNewId() {
			return this.stuntDoubleAndNewId != null;
		}
	}

	private EditedProperty getEditedPropertyInfo(String state, JsonArray pathInformation, JsonElement jsonElementNewValue) {
		Gson gson = new Gson();
		JsonElement parentElement = getModelNodeInState(state, gson);

		JsonElement childElement = getParentDescribedByPath(pathInformation, parentElement);

		long id = getId(childElement);
		String property = getLastPathItemValue(pathInformation);
		String newValue = jsonElementNewValue.getAsJsonPrimitive().getAsString();

		return new EditedProperty(id, property, newValue);
	}

	private JsonElement getModelNodeInState(String state, Gson gson) {
		JsonElement parentElement = gson.fromJson(state, JsonElement.class);
		parentElement = parentElement.getAsJsonObject().get(MODEL_NODE_NAME);
		return parentElement;
	}

	private ParentAndChild getDeletePropertyInfo(String state, JsonArray pathInformation, JsonElement deletedChild) {
		Gson gson = new Gson();
		JsonElement parentElement = gson.fromJson(state, JsonElement.class);
		parentElement = getParentDescribedByPath(pathInformation, parentElement);

		return getParentAndChild(parentElement, deletedChild);
	}

	private Child getChild(JsonElement jsonElementChild) {

		String key = null;
		Map<String, String> properties = new HashMap<String, String>();

		if (!jsonElementChild.isJsonObject()) {
			throw new RuntimeException(
					"Encountered problem while trying to get the properties from a newly added object. Was expecting an object. Found something else. This is not allowed.");
		}

		long parentId = UNSET_PARENT_ID;

		for (Entry<String, JsonElement> entry : jsonElementChild.getAsJsonObject().entrySet()) {
			key = entry.getKey();
			JsonElement jsonElement = entry.getValue();
			
			if (jsonElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = jsonElement.getAsJsonPrimitive();
				if (jsonPrimitive.isString()) {
					properties.put(key, jsonPrimitive.getAsString());
				} else {
					if (key.equals(PROPERTY_PARENT_ID)) {
						parentId = jsonPrimitive.getAsLong();
					} else {
						throw new RuntimeException(
								String.format(
										"Encountered problem while trying to get the propery '%s' from a newly added object. Encountered an object property whose value was not a string. This is not allowed. Added objects must only have string valued properties.",
										key));
					}
				}
			} else if (jsonElement.isJsonArray()) {
				JsonArray jsonArray = jsonElement.getAsJsonArray();
				if (jsonArray.size() > 0) {
					throw new RuntimeException(String.format("Encountered problem while trying to get propert '%s' from newly added object. Property was detected as array type but was not empty. This is not allowed on this type of operation."));
				}
			} else {
				throw new RuntimeException(
						"Encountered problem while trying to get the properties from a newly added object. Encountered an object property whose value was not a primitive. This is not allowed. Added objects must only have string valued properties or empty arrays..");
			}
		}
		String type = properties.remove(PROPERTY_TYPE_LABEL);

		if (parentId == UNSET_PARENT_ID) {
			throw new RuntimeException("Encountered problem with diff node. Changed section did not have a parent ID.");
		}

		return new Child(type, properties, parentId);
	}

	private DeletedChild getDeletedChildInfo(JsonObject state, JsonArray pathInformation, long index, JsonElement deletedChildElement) {

		DeletedChild deletedChild = new DeletedChild();

		long parentId = deletedChildElement.getAsJsonObject().get(PROPERTY_PARENT_ID).getAsLong();
		long childId = deletedChildElement.getAsJsonObject().get(PROPERTY_ID).getAsLong();

		deletedChild.parentAndChild = new ParentAndChild(parentId, childId);
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
			childId = childElement.getAsJsonObject().get(PROPERTY_ID).getAsLong();
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem trying to get parent ID or child ID. Check JSON validity.");
		}
		ParentAndChild parentAndChild = new ParentAndChild(parentId, childId);
		return parentAndChild;
	}

	private long getId(JsonElement jsonElement) {
		return jsonElement.getAsJsonObject().get(PROPERTY_ID).getAsLong();
	}

	public DiffKind getKind(String letter) {

		DiffKind diffKind = null;
		switch (letter) {
		case "N":
			diffKind = DiffKind.NEWLY_ADDED_PROPERTY;
			break;
		case "D":
			diffKind = DiffKind.DELETED_PROPERTY;
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
			throw new RuntimeException(
					"Encountered problem while trying to get the last item in the path. Unable to process element that is not a string. Feature not yet implemented.");
		}

		return jsonPrimitive.getAsString();
	}
}
