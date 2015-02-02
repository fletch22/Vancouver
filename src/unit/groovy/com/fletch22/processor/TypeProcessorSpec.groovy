package com.fletch22.processor;

import static org.junit.Assert.*

import org.junit.Test
import org.junit.runner.RunWith
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

import spock.lang.Specification

import com.fletch22.orb.CommandExpressor
import com.fletch22.orb.command.orbType.AddOrbTypeCommand
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser

@RunWith(SpringJUnit4ClassRunner)
@ContextConfiguration( locations = "classpath:/springContext-test.xml")
class TypeProcessorSpec extends Specification {
	
	static Logger logger = LoggerFactory.getLogger(TypeProcessorSpec)
	
	@Autowired
	AddOrbTypeCommand orbTypeCommands

	@Test
	def 'test Gson'() {
		
		given:
		def command = orbTypeCommands.toJson("foo")
		
		when:
		
		JsonParser parser = new JsonParser()
		JsonObject jsonObject = (JsonObject) parser.parse(command.toString())
		
		JsonObject root = jsonObject.getAsJsonObject(CommandExpressor.ROOT_LABEL)
		JsonArray addOrbType = root.getAsJsonArray(CommandExpressor.ADD_ORB_TYPE)
		
		JsonElement typeLabel = addOrbType.get(0)
		
		def labelPrimitive = typeLabel.getAsJsonPrimitive(CommandExpressor.ORB_TYPE_LABEL)
		
		then:
		jsonObject
		root
		addOrbType
		typeLabel
		labelPrimitive.getAsString() == 'foo'
	}
}
