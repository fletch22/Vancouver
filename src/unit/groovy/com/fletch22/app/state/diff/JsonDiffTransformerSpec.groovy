package com.fletch22.app.state.diff;

import static org.junit.Assert.*

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Shared
import spock.lang.Specification

import com.fletch22.app.state.diff.service.AddChildService
import com.fletch22.app.state.diff.service.JsonDiffProcessorService
import com.fletch22.util.json.GsonFactory
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class JsonDiffTransformerSpec extends Specification {
	
	Logger logger = LoggerFactory.getLogger(JsonDiffTransformerSpec)
	
	@Autowired
	JsonDiffProcessorService jsonDiffProcessorService;
	
	@Autowired
	GsonFactory gsonFactory;
	
	@Shared
	String stateNew = '{"id":43543,"type":"mofo","index":0,"guid":"73c0904d-e88a-44f9-b871-39491de284da","isActive":true,"balance":"$3,541.62","picture":"http://placehold.it/32x32","age":22,"eyeColor":"blue","children":[],"jewelry":[{"hand":{"id":867765,"type":"manipulator"}}],"spa":{"id":525,"type":"recreation","virtue":{"id":777,"type":"beliefOfBeneficialConductAndOrBehaviour","honesty":{"type":"transparencyVirtue","id":7476,"truth":"yes"},"faithfulness":"maybe"}},"company":"QUONATA","email":"hayden.yates@quonata.me","phone":"+1 (850) 577-3564","address":"448 Ellery Street, Kanauga, Maryland, 6098","about":"Reprehenderit dolor veniam eu Lorem nisi ullamco tempor excepteur. Sint ea nostrud occaecat est aute. Ullamco sit pariatur eiusmod consectetur nulla enim irure velit laboris deserunt ut ut minim eu. Ipsum nulla minim sit aute irure consectetur mollit officia deserunt ullamco.","registered":"Friday, July 25, 2014 12:45 PM","latitude":"44.662608","longitude":"-37.689721","tags":[7,"qui","banana"],"range":[0,1,2,3,4,5,6,7,8,9],"friends":[3,{"id":4,"type":"closeFriend","name":"Earnestine Reese"}],"greeting":"Hello, Hayden! You have 7 unread messages.","favoriteFruit":"apple","brickleberry":{"id":525,"type":"berry","weather":"sunshine","temp":70,"arm":{"id":534,"type":"appendage","hand":{"id":435,"type":"manipulator","finger":[{"id":63,"type":"gripper","scissors":"fiskers"}]}}}}'
	
	@Shared
	String deletePropertyBadDiff = '{"kind":"D","path":["name"],"lhs":{"id":1234,"type":"somethingNamed","first":"Hayden","last":"Yates"}}'
		
	@Shared
	String deletePropertyGoodDiff = '{"kind":"D","path":["jewelry",0,"hand","ring"],"lhs":{"id":6677,"type":"jewelry","setting":{"id":52525,"type":"appearance","prodType":"princess","material":"platinum"}}}'
	
	@Shared 
	String editedPropertyGoodDiff1 = '{"kind":"E","path":["spa","virtue","faithfulness"],"lhs":"yeah","rhs":"maybe"}'
	
	@Shared 
	String deleteFromArrayGoodDiff = '{"kind":"A","path":["children"],"index":0,"item":{"kind":"D","lhs":{"id":56679,"type":"aKindOfChild","foo":"bar"}}}'
	
	@Shared
	String addedObjectToArrayBadDiff = '{"kind":"A","path":["tags"],"index":2,"item":{"kind":"N","rhs":"banana"}}'
	
	@Shared
	String addedObjectToArrayGoodDiff = '{"kind":"A","path":["tags"],"index":2,"item":{"kind":"N","rhs":{"type":"floober","hardness":"reallyHard","foo":"fum"}}}'
	
	@Shared 
	String editPropertyGoodDiff2 = '{"kind":"E","path":["friends",1,"id"],"lhs":1,"rhs":4}'
	
	@Shared
	String editedPropertyBadDiff = '{"kind":"X","path":["friends",1,"id"],"lhs":1,"rhs":4}'
	
	@Shared 
	String addedObjectToNonArrayBadDiff = '{"kind":"N","path":["brickleberry"],"rhs":{"id":525,"type":"berry","weather":"sunshine","temp":70,"arm":{"id":534,"type":"appendage","hand":{"id":435,"type":"manipulator","finger":[{"id":63,"type":"gripper","scissors":"fiskers"}]}}}}'
	
	@Shared
	String jsonGood = '[' + deleteFromArrayGoodDiff + ',' + deletePropertyGoodDiff + ',' + editedPropertyGoodDiff1 + ',' + editPropertyGoodDiff2 + ',' + addedObjectToArrayGoodDiff + ']'
	
	@Shared
	AddChildService addedChildServiceOriginal	
	
	def setup() {
		this.addedChildServiceOriginal = this.jsonDiffProcessorService.addChildService
		this.jsonDiffProcessorService.addChildService = Mock(AddChildService)
	}
	
	def cleanup() {
		this.jsonDiffProcessorService.addChildService = this.addedChildServiceOriginal 
	}
		
	def 'test all diffs processing'() {
		
		given:
		Gson gson = gsonFactory.getInstance();
		
		JsonObject joStateNew = gson.fromJson(stateNew, JsonObject.class);
				
		when:
		jsonDiffProcessorService.process(stateNew, jsonGood);
		
		then:
		noExceptionThrown()
	}
	
	def 'test delete prop diff processing bad delete'() {
		
		given:
		Gson gson = gsonFactory.getInstance();
		JsonObject joStateNew = gson.fromJson(stateNew, JsonObject.class);
		
		JsonObject deleteDiff = gson.fromJson(deletePropertyBadDiff, JsonObject.class);
		JsonArray jsonArray = deleteDiff.getAsJsonArray("path");
				
		when:
		ArrayList<String> diffs = JsonDiffProcessorService jsonDiffProcessorService.processDeleteProperty(joStateNew, jsonArray);
		
		then:
		thrown RuntimeException
	}
	
	def 'test delete prop diff processing'() {
		
		given:
		Gson gson = gsonFactory.getInstance()
		JsonObject joStateNew = gson.fromJson(stateNew, JsonObject.class)
		
		JsonObject deleteDiff = gson.fromJson(deletePropertyGoodDiff, JsonObject.class)
		JsonArray jsonArray = deleteDiff.getAsJsonArray("path")
		
		JsonElement deletedChild = deleteDiff.get("lhs")
				
		when:
		ParentAndChild parentAndChild = JsonDiffProcessorService jsonDiffProcessorService.getDeletePropertyInfo(joStateNew, jsonArray, deletedChild)
		
		then:
		parentAndChild != null
		parentAndChild.parentId == 867765
		parentAndChild.childId == 6677
	}
	
	def 'test delete array element diff processing'() {
		
		given:
		Gson gson = gsonFactory.getInstance()
		JsonObject joStateNew = gson.fromJson(stateNew, JsonObject.class)
		
		JsonObject diff = gson.fromJson(deleteFromArrayGoodDiff, JsonObject.class)
		JsonArray jsonArray = diff.getAsJsonArray("path")
		
		JsonElement index = diff.get("index")
		JsonElement item = diff.get("item")
				
		when:
		JsonDiffProcessorService jsonDiffProcessorService.processChangedArray(joStateNew, jsonArray, index, item)
		
		then:
		noExceptionThrown()
	}
	
	def 'test get parent child info'() {
		
		given:
		Gson gson = gsonFactory.getInstance()
		JsonObject state = gson.fromJson(stateNew, JsonObject.class)
		
		JsonObject diff = gson.fromJson(deleteFromArrayGoodDiff, JsonObject.class)
		JsonArray pathInformation = diff.getAsJsonArray("path")
		
		long index = diff.get("index").getAsLong()
		JsonElement item = diff.get("item")
		JsonElement deletedChildElement = item.getAsJsonObject().get("lhs");
				
		when:
		DeletedChild deletedChild = JsonDiffProcessorService jsonDiffProcessorService.getDeletedChildInfo(state, pathInformation, index, deletedChildElement)
		
		then:
		noExceptionThrown()
		deletedChild.index == index
		deletedChild.parentAndChild.childId == 56679
		deletedChild.parentAndChild.parentId == 43543
	}
	
	def 'test get edited info'() {
		
		given:
		Gson gson = gsonFactory.getInstance()
		JsonObject state = gson.fromJson(stateNew, JsonObject.class)
		
		JsonObject diff = gson.fromJson(editedPropertyGoodDiff1, JsonObject.class)
		JsonArray pathInformation = diff.getAsJsonArray("path")
		
		JsonElement newValue = diff.get("rhs");
				
		when:
		EditedProperty editPropertyInfo = JsonDiffProcessorService jsonDiffProcessorService.getEditedPropertyInfo(state, pathInformation, newValue)
		
		then:
		noExceptionThrown()
		editPropertyInfo.newValue == 'maybe'
		editPropertyInfo.property == 'faithfulness'
		editPropertyInfo.id == 777
	}
	
	def 'test edit property did not recognize diff kind'() {
		
		given:
		Gson gson = gsonFactory.getInstance();
		JsonObject diff = gson.fromJson(editedPropertyBadDiff, JsonObject.class);
		JsonObject state = gson.fromJson(stateNew, JsonObject.class);
		
		when:
		JsonDiffProcessorService jsonDiffProcessorService.processDiff(state, diff);
		
		then:
		def exception = thrown RuntimeException
		exception.message.contains("Did not recognize diff code");
	}
	
	def 'test add bad addition to non array'() {
		
		given:
		Gson gson = gsonFactory.getInstance();
		JsonObject diff = gson.fromJson(addedObjectToNonArrayBadDiff, JsonObject.class);
		JsonObject state = gson.fromJson(stateNew, JsonObject.class);
		
		when:
		JsonDiffProcessorService jsonDiffProcessorService.processDiff(state, diff);
		
		then:
		def exception = thrown RuntimeException
		exception.message.contains(JsonDiffProcessorService.EX_MSG_UNSUPPORTED_NEW_ADD);
	}
	
	def 'test add bad addition to array'() {
		
		given:
		Gson gson = gsonFactory.getInstance();
		JsonObject diff = gson.fromJson(addedObjectToArrayBadDiff, JsonObject.class);
		
		JsonElement jsonElementChild = diff.get("item").getAsJsonObject().get("rhs");
				
		when:
		JsonDiffProcessorService jsonDiffProcessorService.getChild(jsonElementChild);
		
		then:
		thrown RuntimeException
	}
	
	def 'test add good addition to to array'() {
		
		given:
		Gson gson = gsonFactory.getInstance();
		JsonObject diff = gson.fromJson(addedObjectToArrayGoodDiff, JsonObject.class);
		JsonElement jsonElementChild = diff.get("item").getAsJsonObject().get("rhs");
				
		when:
		Child child = JsonDiffProcessorService jsonDiffProcessorService.getChild(jsonElementChild);
		
		then:
		noExceptionThrown()
		child.typeLabel == 'floober'
		child.props.size() == 2
		child.props.get("hardness") == "reallyHard"
		child.props.get("foo") == "fum"
	}
}
