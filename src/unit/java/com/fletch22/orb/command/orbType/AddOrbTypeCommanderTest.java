package com.fletch22.orb.command.orbType;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fletch22.orb.OrbTypeConstants;
import com.fletch22.orb.command.orbType.dto.AddOrbTypeDto;
import com.fletch22.util.json.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
public class AddOrbTypeCommanderTest {
	
	Logger logger = LoggerFactory.getLogger(AddOrbTypeCommanderTest.class);
	
	@Autowired
	AddOrbTypeCommand addOrbTypeCommand;
	
	@Autowired
	GsonFactory gsonFactory;
	
	@Test
	public void testSpring() {
		assertNotNull(addOrbTypeCommand);
		assertNotNull(addOrbTypeCommand.jsonUtil);
	}

	@Test
	public void testSpeed() {
		
		logger.debug("Start");
		
		String json = addOrbTypeCommand.toJson("foo").toString();
		for (int i = 0; i < 100000; i++) {
			@SuppressWarnings("unused")
			AddOrbTypeDto dto = addOrbTypeCommand.fromJson(json);
		}
		logger.debug("End");  
	}
	
	@SuppressWarnings({ "unused" })
	@Test
	public void testComplexJsonConversionSpeed() {
		
		AddOrbTypePackage addOrbTypePackage = new AddOrbTypePackage();
		addOrbTypePackage.label = "foo";
		addOrbTypePackage.orbInternalId = OrbTypeConstants.ORBTYPE_INTERNAL_ID_UNSET;
		
		Gson gson = gsonFactory.getInstance();
		JsonParser jsonParser = new JsonParser();
		
		CommandWrapper commandWrapper = new CommandWrapper();
		commandWrapper.actionId = AddOrbTypePackage.ACTION_ID;
		commandWrapper.action = addOrbTypePackage;
		
		String actionb = gson.toJson(commandWrapper);
		
		logger.debug("Start Complex");
		
		for (int i = 0; i < 100000; i++) {
			JsonElement jsonElement = jsonParser.parse(actionb);
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			String actionId = jsonObject.getAsJsonPrimitive("actionId").getAsString();
			JsonElement jsonCommand = jsonObject.getAsJsonObject("action");
			
			TransformActionToClassName transformActionToClassName = new TransformActionToClassName();
			Class<?> clazz = transformActionToClassName.transformAction(actionId);
			
			try {
				AddOrbTypePackage addTypePackageRedyra = (AddOrbTypePackage) gson.fromJson(jsonCommand, clazz);
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		
		}
		logger.debug("End");
		
	}
	
	public class Command<T> {
		
		@SuppressWarnings("unchecked")
		public T getObject(JsonElement jsonCommand, Class<?> type) {
			Gson gson = gsonFactory.getInstance();
			
			Object object;
			try {
				object = gson.fromJson(jsonCommand, type.getClass());
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(), e);
			}
			
			return (T) object;
		}
	}
}
