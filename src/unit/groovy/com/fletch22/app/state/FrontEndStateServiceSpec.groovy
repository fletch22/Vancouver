package com.fletch22.app.state;

import static org.junit.Assert.*
import spock.lang.Specification

import com.fletch22.web.controllers.ComponentController.ClientIdsPackage

class FrontEndStateServiceSpec extends Specification {
	
	FrontEndStateService frontEndStateService
	
	def setup() {
		this.frontEndStateService = new FrontEndStateService();
	}

//	def testDetermineLastGoodState() {
//		
//		given:
//		ClientIdsPackage clientIdsPackage = new ClientIdsPackage()
//				
//		List<List<String>> idPackages = new ArrayList<ArrayList<String>>()
//		
//		List<String> firstPackage = new ArrayList<String>()
//		firstPackage.add("uuid-foo1")
//		firstPackage.add("uuid-foo2")
//		firstPackage.add("uuid-foo3")
//		firstPackage.add("uuid-foo4")
//		idPackages.add(firstPackage)
//		
//		clientIdsPackage.idPackages = idPackages 
//		
//		when:
//		String state = this.frontEndStateService.determineLastGoodState(clientIdsPackage)
//			
//		then:
//		state != null
//	}
}
