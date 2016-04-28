package com.fletch22.app.state.diff;

import static org.junit.Assert.*

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration

import spock.lang.Shared
import spock.lang.Specification

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
	String stateNew = '{"id":"571aa717e0d3bc6d3c78e3ea","index":0,"guid":"73c0904d-e88a-44f9-b871-39491de284da","isActive":true,"balance":"$3,541.62","picture":"http://placehold.it/32x32","age":22,"eyeColor":"blue","children":[],"spa":{"id":525,"virtue":{"id":777,"faithfulness":"maybe"}},"company":"QUONATA","email":"hayden.yates@quonata.me","phone":"+1 (850) 577-3564","address":"448 Ellery Street, Kanauga, Maryland, 6098","about":"Reprehenderit dolor veniam eu Lorem nisi ullamco tempor excepteur. Sint ea nostrud occaecat est aute. Ullamco sit pariatur eiusmod consectetur nulla enim irure velit laboris deserunt ut ut minim eu. Ipsum nulla minim sit aute irure consectetur mollit officia deserunt ullamco.","registered":"Friday, July 25, 2014 12:45 PM","latitude":"44.662608","longitude":"-37.689721","tags":[7,"qui","banana"],"range":[0,1,2,3,4,5,6,7,8,9],"friends":[3,{"id":4,"name":"Earnestine Reese"}],"greeting":"Hello, Hayden! You have 7 unread messages.","favoriteFruit":"apple","brickleberry":{"id":525,"weather":"sunshine","temp":70,"arm":{"id":534,"hand":{"id":435,"finger":[{"id":63,"scissors":"fiskers"}]}}}}'
	
	@Shared
	String deleteBadDiff = '{"kind":"D","path":["name"],"lhs":{"id":1234,"first":"Hayden","last":"Yates"}}'
		
	@Shared
	String deleteGoodDiff = '{"kind":"D","path":["spa","virtue","honesty"],"lhs":{"id":7476,"truth":"yes"}}'
	
	@Shared 
	String editedDiff = '{"kind":"E","path":["spa","virtue","faithfulness"],"lhs":"yeah","rhs":"maybe"}'
	
	@Shared 
	String deletedFromArrayDiff = '{"kind":"A","path":["children"],"index":0,"item":{"kind":"D","lhs":{"id":56679,"foo":"bar"}}}'
		
	@Shared
	String jsonGood = '[' + deletedFromArrayDiff + ',' + deleteGoodDiff + ',' + editedDiff + ',{"kind":"E","path":["spa","virtue","faithfulness"],"lhs":"yeah","rhs":"maybe"},{"kind":"A","path":["tags"],"index":2,"item":{"kind":"N","rhs":"banana"}},{"kind":"E","path":["friends",1,"id"],"lhs":1,"rhs":4},{"kind":"N","path":["brickleberry"],"rhs":{"id":525,"weather":"sunshine","temp":70,"arm":{"id":534,"hand":{"id":435,"finger":[{"id":63,"scissors":"fiskers"}]}}}}]';
		
	def 'test all diffs processing'() {
		
		given:
		Gson gson = gsonFactory.getInstance();
		
		JsonObject joStateNew = gson.fromJson(stateNew, JsonObject.class);
				
		when:
		ArrayList<String> diffs = jsonDiffTransformer.transform(stateNew, jsonGood);
		
		then:
		jsonDiffTransformer != null
		diffs.size() == 7
	}
	
	def 'test delete diff processing bad delete'() {
		
		given:
		Gson gson = gsonFactory.getInstance();
		JsonObject joStateNew = gson.fromJson(stateNew, JsonObject.class);
		
		JsonObject deleteDiff = gson.fromJson(deleteBadDiff, JsonObject.class);
		JsonArray jsonArray = deleteDiff.getAsJsonArray("path");
				
		when:
		ArrayList<String> diffs = jsonDiffTransformer.processDelete(joStateNew, jsonArray);
		
		then:
		thrown RuntimeException
	}
	
	def 'test delete diff processing'() {
		
		given:
		Gson gson = gsonFactory.getInstance()
		JsonObject joStateNew = gson.fromJson(stateNew, JsonObject.class)
		
		JsonObject deleteDiff = gson.fromJson(deleteGoodDiff, JsonObject.class)
		JsonArray jsonArray = deleteDiff.getAsJsonArray("path")
		
		JsonElement deletedChild = deleteDiff.get("lhs")
				
		when:
		ParentAndChild parentAndChild = jsonDiffTransformer.getParentAndChild(joStateNew, jsonArray, deletedChild)
		
		then:
		parentAndChild != null
		parentAndChild.parentId == 777
		parentAndChild.childId == 7476
	}
}
