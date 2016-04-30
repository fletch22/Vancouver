package com.fletch22.app.state.diff;

import static org.junit.Assert.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Shared
import spock.lang.Specification

import com.fletch22.app.state.diff.JsonDiffTransformer.DeletedChild
import com.fletch22.app.state.diff.JsonDiffTransformer.EditPropertyInfo
import com.fletch22.app.state.diff.JsonDiffTransformer.ParentAndChild
import com.fletch22.util.json.GsonFactory
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

@ContextConfiguration(locations = ['classpath:/springContext-test.xml'])
class JsonDiffTransformerSpec extends Specification {
	
	@Autowired
	JsonDiffTransformer jsonDiffTransformer;
	
	@Autowired
	GsonFactory gsonFactory;
	
	@Shared
	String stateNew = '{"id":57171736,"index":0,"guid":"73c0904d-e88a-44f9-b871-39491de284da","isActive":true,"balance":"$3,541.62","picture":"http://placehold.it/32x32","age":22,"eyeColor":"blue","children":[],"jewelry":[{"hand":{"id":867765}}],"spa":{"id":525,"virtue":{"id":777,"honesty":{"id":7476,"truth":"yes"},"faithfulness":"maybe"}},"company":"QUONATA","email":"hayden.yates@quonata.me","phone":"+1 (850) 577-3564","address":"448 Ellery Street, Kanauga, Maryland, 6098","about":"Reprehenderit dolor veniam eu Lorem nisi ullamco tempor excepteur. Sint ea nostrud occaecat est aute. Ullamco sit pariatur eiusmod consectetur nulla enim irure velit laboris deserunt ut ut minim eu. Ipsum nulla minim sit aute irure consectetur mollit officia deserunt ullamco.","registered":"Friday, July 25, 2014 12:45 PM","latitude":"44.662608","longitude":"-37.689721","tags":[7,"qui","banana"],"range":[0,1,2,3,4,5,6,7,8,9],"friends":[3,{"id":4,"name":"Earnestine Reese"}],"greeting":"Hello, Hayden! You have 7 unread messages.","favoriteFruit":"apple","brickleberry":{"id":525,"weather":"sunshine","temp":70,"arm":{"id":534,"hand":{"id":435,"finger":[{"id":63,"scissors":"fiskers"}]}}}}'
	
	@Shared
	String deletePropertyBadDiff = '{"kind":"D","path":["name"],"lhs":{"id":1234,"first":"Hayden","last":"Yates"}}'
		
	@Shared
	String deletePropertyGoodDiff = '{"kind":"D","path":["jewelry",0,"hand","ring"],"lhs":{"id":6677,"setting":{"id":52525,"type":"princess","material":"platinum"}}}'
	
	@Shared 
	String editedPropertyDiff = '{"kind":"E","path":["spa","virtue","faithfulness"],"lhs":"yeah","rhs":"maybe"}'
	
	@Shared 
	String changeToArrayDiff = '{"kind":"A","path":["children"],"index":0,"item":{"kind":"D","lhs":{"id":56679,"foo":"bar"}}}'
	
	@Shared
	String jsonGood = '[' + changeToArrayDiff + ',' + deletePropertyGoodDiff + ',' + editedPropertyDiff + ',{"kind":"A","path":["tags"],"index":2,"item":{"kind":"N","rhs":"banana"}},{"kind":"E","path":["friends",1,"id"],"lhs":1,"rhs":4},{"kind":"N","path":["brickleberry"],"rhs":{"id":525,"weather":"sunshine","temp":70,"arm":{"id":534,"hand":{"id":435,"finger":[{"id":63,"scissors":"fiskers"}]}}}}]'
		
	def 'test all diffs processing'() {
		
		given:
		Gson gson = gsonFactory.getInstance();
		
		JsonObject joStateNew = gson.fromJson(stateNew, JsonObject.class);
				
		when:
		ArrayList<String> diffs = jsonDiffTransformer.transform(stateNew, jsonGood);
		
		then:
		jsonDiffTransformer != null
		diffs.size() == 6
	}
	
	def 'test delete prop diff processing bad delete'() {
		
		given:
		Gson gson = gsonFactory.getInstance();
		JsonObject joStateNew = gson.fromJson(stateNew, JsonObject.class);
		
		JsonObject deleteDiff = gson.fromJson(deletePropertyBadDiff, JsonObject.class);
		JsonArray jsonArray = deleteDiff.getAsJsonArray("path");
				
		when:
		ArrayList<String> diffs = jsonDiffTransformer.processDeleteProperty(joStateNew, jsonArray);
		
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
		ParentAndChild parentAndChild = jsonDiffTransformer.getDeletePropertyInfo(joStateNew, jsonArray, deletedChild)
		
		then:
		parentAndChild != null
		parentAndChild.parentId == 867765
		parentAndChild.childId == 6677
	}
	
	def 'test delete array element diff processing'() {
		
		given:
		Gson gson = gsonFactory.getInstance()
		JsonObject joStateNew = gson.fromJson(stateNew, JsonObject.class)
		
		JsonObject diff = gson.fromJson(changeToArrayDiff, JsonObject.class)
		JsonArray jsonArray = diff.getAsJsonArray("path")
		
		JsonElement index = diff.get("index")
		JsonElement item = diff.get("item")
				
		when:
		jsonDiffTransformer.processChangedArray(joStateNew, jsonArray, index, item)
		
		then:
		noExceptionThrown()
	}
	
	def 'test get parent child info'() {
		
		given:
		Gson gson = gsonFactory.getInstance()
		JsonObject state = gson.fromJson(stateNew, JsonObject.class)
		
		JsonObject diff = gson.fromJson(changeToArrayDiff, JsonObject.class)
		JsonArray pathInformation = diff.getAsJsonArray("path")
		
		long index = diff.get("index").getAsLong()
		JsonElement item = diff.get("item")
		JsonElement deletedChildElement = item.getAsJsonObject().get("lhs");
				
		when:
		DeletedChild deletedChild = jsonDiffTransformer.getDeletedChildInfo(state, pathInformation, index, deletedChildElement)
		
		then:
		noExceptionThrown()
		deletedChild.index == index
		deletedChild.parentAndChild.childId == 56679
		deletedChild.parentAndChild.parentId == 57171736
	}
	
	def 'test get edited info'() {
		
		given:
		Gson gson = gsonFactory.getInstance()
		JsonObject state = gson.fromJson(stateNew, JsonObject.class)
		
		JsonObject diff = gson.fromJson(editedPropertyDiff, JsonObject.class)
		JsonArray pathInformation = diff.getAsJsonArray("path")
		
		JsonElement newValue = diff.get("rhs");
				
		when:
		EditPropertyInfo editPropertyInfo = jsonDiffTransformer.getEditedPropertyInfo(state, pathInformation, newValue)
		
		then:
		noExceptionThrown()
		editPropertyInfo.newValue == 'maybe'
		editPropertyInfo.property == 'faithfulness'
		editPropertyInfo.id == 777
	}
}
