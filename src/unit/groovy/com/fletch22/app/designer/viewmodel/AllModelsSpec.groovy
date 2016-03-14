package com.fletch22.app.designer.viewmodel;

import static org.junit.Assert.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Specification

import com.fletch22.util.json.GsonFactory
import com.google.gson.Gson

@ContextConfiguration(locations = 'classpath:/springContext-test.xml')
class AllModelsSpec extends Specification {
	
	Logger logger = LoggerFactory.getLogger(AllModelsSpec);

	@Autowired
	GsonFactory gsonFactory;
	
	def 'test all Models serialization'() {
		
		given:
		AllModels allModels = new AllModels();
		
		Gson gson = gsonFactory.getInstance();
		
		logger.info(gson.toJson(allModels));
	}
	
//	def 'test allModels deserialize'() {
//		
//		given: 
//		//{"nameValuePairs":{}}
//		AllModels allModels = new AllModels();
//		
//		Gson gson = gsonFactory.getInstance();
//		
//		when:
//		ExtParameters extParameters = gson.fromJson("{nameValuePairs: {id: 234, typeLabel: 'AppContainer'}}", ExtParameters.class);
//		
//		then: 
//		extParameters != null
//		extParameters.nameValuePairs.size() == 2;
//	}

}
