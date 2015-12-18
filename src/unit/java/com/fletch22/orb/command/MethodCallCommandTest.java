package com.fletch22.orb.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fletch22.orb.command.orbType.dto.MethodCallDto;
import com.fletch22.util.json.GsonFactory;

public class MethodCallCommandTest {

	Logger logger = LoggerFactory.getLogger(MethodCallCommandTest.class);

	@Test
	public void test() {

		// Arrange
		Object[] args = new Object[2];
		args[0] = "This is a sample parameter value.";
		args[1] = 1L;
		
		String[] paramArray = new String[2];
		paramArray[0] = "java.lang.String";
		paramArray[1] = Long.TYPE.toString();

		MethodCallDto methodCallDto = new MethodCallDto("com.fletch22.Foo", "bar", paramArray, args);

		MethodCallCommand methodCallCommand = new MethodCallCommand();
		GsonFactory gsonFactory = new GsonFactory();
		methodCallCommand.gsonFactory = gsonFactory;
		
		StringBuilder json = methodCallCommand.toJson(methodCallDto);

		String action = json.toString();

		logger.debug(action);

		// Act
		MethodCallDto methodCallDtoActual = methodCallCommand.fromJson(new StringBuilder(action));

		// Assert
		assertEquals(methodCallDto.className, methodCallDtoActual.className);
		assertEquals(methodCallDto.args.length, methodCallDtoActual.args.length, 0);
		assertEquals(methodCallDto.parameterTypes.length, methodCallDtoActual.parameterTypes.length);
	}
	
	@Test
	public void testExistingCall() {
		
		// Arrange
		StringBuilder sb = new StringBuilder();
		
		String json = "{\"command\":{\"methodCall\":{\"className\":\"com.fletch22.orb.cache.local.OrbTypeManagerLocalCache\"},\"methodName\":\"addAttribute\",\"methodParameters\":[{\"parameterTypeName\":\"long\", \"argument\":{\"clazzName\":\"java.lang.Long\",\"objectValueAsJson\":\"1\"}},{\"parameterTypeName\":\"class java.lang.String\", \"argument\":{\"clazzName\":\"java.lang.String\",\"objectValueAsJson\":\"\\\"foo\\\"\"}}]}}";
		
		sb.append(json);
		
		MethodCallCommand methodCallCommand = new MethodCallCommand();
		
		methodCallCommand.gsonFactory = new GsonFactory();
		
		// Act
		MethodCallDto methodCallDtoActual = methodCallCommand.fromJson(sb);
		
		// Assert
		assertNotNull(methodCallDtoActual);
		
		Object[] args = methodCallDtoActual.args;
		assertEquals(Long.class.getName(), args[0].getClass().getName());
	}

}
