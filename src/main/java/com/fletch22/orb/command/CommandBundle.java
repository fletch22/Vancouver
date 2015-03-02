package com.fletch22.orb.command;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.orb.CommandExpressor;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CommandBundle {
	
	static Logger logger = LoggerFactory.getLogger(CommandBundle.class);
	
	private List<StringBuilder> actionList = new ArrayList<StringBuilder>();
	
	public void addCommand(StringBuilder action) {
		this.actionList.add(action);
	}

	public StringBuilder toJson() {
		StringBuilder translation = new StringBuilder();

		translation.append("{\"");
		translation.append(CommandExpressor.ROOT_LABEL);
		translation.append("\":{\"");
		translation.append(CommandExpressor.COMMAND_BUNDLE);
		translation.append("\":[");
		
		boolean isFirst = true;
		for (StringBuilder sb : this.actionList) {
			if (!isFirst) {
				translation.append(",");
			}
			isFirst = false;
			translation.append(sb);
		}
		
		translation.append("]}}");

		return translation;
	}
	
	public static CommandBundle fromJson(StringBuilder action) {
		
		Gson gson = new Gson();
		
		CommandBundle commandBundle = new CommandBundle();
		
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = (JsonObject) parser.parse(action.toString());
		
		JsonObject root = jsonObject.getAsJsonObject(CommandExpressor.ROOT_LABEL);
		JsonArray commandArray = root.getAsJsonArray(CommandExpressor.COMMAND_BUNDLE);
		
		for (int i = 0; i < commandArray.size(); i++) {
			jsonObject = commandArray.get(i).getAsJsonObject();
			commandBundle.addCommand(new StringBuilder(gson.toJson(jsonObject)));
		}
		
		return commandBundle;
	}
	
	public List<StringBuilder> getActionList() {
		return this.actionList;
	}
}
