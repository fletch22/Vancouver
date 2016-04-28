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

		ArrayList<Diff> diffList = new ArrayList<Diff>();
		Gson gson = gsonFactory.getInstance();

		JsonArray jsonArray = gson.fromJson(jsonDiff, JsonArray.class);

		logger.info("size of jsonArray: {}", jsonArray.size());

		for (int i = 0; i < jsonArray.size(); i++) {
			JsonElement jsonElement = jsonArray.get(i);
			JsonObject jsonObject = jsonElement.getAsJsonObject();

			String letter = jsonObject.get("kind").getAsJsonPrimitive()
					.getAsString();

			JsonArray pathInformation = jsonObject.get("path").getAsJsonArray();

			JsonObject joStateNew = gson.fromJson(state, JsonObject.class);

			Diff diff = new Diff();
			diff.diffKind = getKind(letter);

			switch (diff.diffKind) {
			case DELETED_PROPERTY:
				
				JsonElement deletedChild = jsonObject.get("lhs");
				
				processDelete(joStateNew, pathInformation, deletedChild);
				break;
			case EDITED_ELEMENT:
			case ARRAY_CHANGE:
			case NEWLY_ADDED_PROPERTY:
				break;
			default:
				throw new RuntimeException(
						"Encountered problem while trying to process diff state - did not recognize diff kind.");
			}

			diffList.add(diff);
		}

		return diffList;
	}

	protected void processDelete(JsonObject state, JsonArray pathInformation, JsonElement deletedChild) {
		ParentAndChild parentAndChild = getParentAndChild(state, pathInformation, deletedChild);
	}

	private ParentAndChild getParentAndChild(JsonObject state, JsonArray pathInformation, JsonElement deletedChild) {
		JsonElement jsonElement = (JsonElement) state;
		for (int i = 0; i < pathInformation.size() - 1; i++) {
			JsonElement pathElement = pathInformation.get(i);

			if (pathElement.isJsonPrimitive()) {
				JsonPrimitive jsonPrimitive = pathElement.getAsJsonPrimitive();
				if (jsonPrimitive.isNumber()) {
					throw new RuntimeException("Not yet implemented.");
				} else {
					String memberName = jsonPrimitive.getAsString();
					jsonElement = jsonElement.getAsJsonObject().get(memberName);
				}
			} else {
				throw new RuntimeException("Encountered problem reading diff array. Encountered an array item that was not a primitive -- inconceivable!");
			}
		}
		
		long parentId;
		long childId;
		try {
			parentId = getId(jsonElement);
			childId = deletedChild.getAsJsonObject().get("id").getAsLong();
		} catch (Exception e) {
			throw new RuntimeException("Encountered problem trying to get parent ID or child ID during delete operation. Check JSON validity.");
		}
		return new ParentAndChild(parentId, childId);
	}

	private long getId(JsonElement jsonElement) {
		logger.info(jsonElement.toString());
		logger.info("Id: {}", jsonElement.getAsJsonObject().get("id").getAsLong());
		
		return jsonElement.getAsJsonObject().get("id").getAsLong();
	}

	public class ParentAndChild {
		public long parentId;
		public long childId;
		
		public ParentAndChild(long parentId, long childId) {
			this.parentId = parentId;
			this.childId = childId;
		}
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
			diffKind = DiffKind.EDITED_ELEMENT;
			break;
		case "A":
			diffKind = DiffKind.ARRAY_CHANGE;
			break;
		default:
			String message = String.format(
					"Encountered problem. Did not recognize diff code: %s",
					letter);
			throw new RuntimeException(message);
		}
		return diffKind;
	}

}
