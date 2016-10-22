package com.fletch22.app.state.diff.service;

import static org.junit.Assert.*

import org.junit.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import spock.lang.Specification

import com.fletch22.app.state.diff.service.JsonDiffProcessorService.FamilyMember
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement

class JsonDiffProcessorServiceSpec extends Specification {
	
	Logger logger = LoggerFactory.getLogger(JsonDiffProcessorServiceSpec);

	@Test
	public void test() {
		
		given:
		JsonDiffProcessorService jsonDiffProcessorService = new JsonDiffProcessorService()
		
		Gson gson = new Gson();
		JsonArray pathInformation = gson.fromJson('["appContainer","children",0,"label"]', JsonArray.class)
		
		JsonElement jsonElement = gson.fromJson('{"appContainer": {"children": [{"label": "foo","typeLabel": "App","id": 1041,"parentId": 1040}],"id": 1040,"typeLabel": "AppContainer"}}', JsonElement.class)
		
		logger.info(pathInformation.toString())
		
		when:
		jsonDiffProcessorService.getNodeDescribedByPath(pathInformation, jsonElement, FamilyMember.Parent)
		int i = 0
		
		then:
		i == 0
	}
}
