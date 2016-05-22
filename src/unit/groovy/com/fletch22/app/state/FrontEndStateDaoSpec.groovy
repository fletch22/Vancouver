package com.fletch22.app.state;

import static org.junit.Assert.*
import spock.lang.Specification

import com.fletch22.app.state.FrontEndStateDao.StateSearchResult
import com.fletch22.orb.Orb
import com.fletch22.orb.query.OrbResultSet

class FrontEndStateDaoSpec extends Specification {
	
	FrontEndStateDao frontEndStateDao
	
	def setup() {
		this.frontEndStateDao = new FrontEndStateDao()
	}

	def "test determineLastGoodStateFromList - first one"() {
		
		given:
		List<String> clientIds = new ArrayList<String>()
		clientIds.add("uuid-foo1")
		clientIds.add("uuid-foo2")
		clientIds.add("uuid-foo3")
		clientIds.add("uuid-foo4")
		
		OrbResultSet orbResultSet = new OrbResultSet()
		orbResultSet.orbList = new ArrayList<Orb>()
		
		Orb orb = createOrb("1234", "uuid-foo1")
		orbResultSet.orbList.add(orb);
		
		when:
		StateSearchResult stateSearchResult = frontEndStateDao.determineLastGoodStateFromList(clientIds, orbResultSet)
			
		then:
		stateSearchResult.isStateFound()
		stateSearchResult.state == "1234"
	}
	
	def "test determineLastGoodStateFromList - last one"() {
		
		given:
		List<String> clientIds = new ArrayList<String>()
		clientIds.add("uuid-foo1")
		clientIds.add("uuid-foo2")
		clientIds.add("uuid-foo3")
		clientIds.add("uuid-foo4")
		
		OrbResultSet orbResultSet = new OrbResultSet()
		orbResultSet.orbList = new ArrayList<Orb>()
		
		Orb orb = createOrb("1234", "uuid-foo1")
		orbResultSet.orbList.add(orb);
		
		orb = createOrb("asdf", "uuid-foo2")
		orbResultSet.orbList.add(orb);
		
		orb = createOrb("qwer", "uuid-foo3")
		orbResultSet.orbList.add(orb);
		
		orb = createOrb("qwasdfer", "uuid-foo4")
		orbResultSet.orbList.add(orb);
		
		when:
		StateSearchResult stateSearchResult = frontEndStateDao.determineLastGoodStateFromList(clientIds, orbResultSet)
			
		then:
		stateSearchResult.isStateFound()
		stateSearchResult.state == "qwasdfer"
	}
	
	def "test determineLastGoodStateFromList - fouth one missing"() {
		
		given:
		List<String> clientIds = new ArrayList<String>()
		clientIds.add("uuid-foo1")
		clientIds.add("uuid-foo2")
		clientIds.add("uuid-foo3")
		clientIds.add("uuid-foo4")
		
		OrbResultSet orbResultSet = new OrbResultSet()
		orbResultSet.orbList = new ArrayList<Orb>()
		
		Orb orb = createOrb("1234", "uuid-foo1")
		orbResultSet.orbList.add(orb);
		
		orb = createOrb("asdf", "uuid-foo2")
		orbResultSet.orbList.add(orb);
		
		orb = createOrb("qwer", "uuid-foo3")
		orbResultSet.orbList.add(orb);
		
		when:
		StateSearchResult stateSearchResult = frontEndStateDao.determineLastGoodStateFromList(clientIds, orbResultSet)
			
		then:
		stateSearchResult.isStateFound()
		stateSearchResult.state == "qwer"
	}
	
	def "test determineLastGoodStateFromList - third one missing"() {
		
		given:
		List<String> clientIds = new ArrayList<String>()
		clientIds.add("uuid-foo1")
		clientIds.add("uuid-foo2")
		clientIds.add("uuid-foo3")
		clientIds.add("uuid-foo4")
		
		OrbResultSet orbResultSet = new OrbResultSet()
		orbResultSet.orbList = new ArrayList<Orb>()
		
		Orb orb = createOrb("1234", "uuid-foo1")
		orbResultSet.orbList.add(orb);
		
		orb = createOrb("asdf", "uuid-foo2")
		orbResultSet.orbList.add(orb);
		
		orb = createOrb("qwasdfer", "uuid-foo4")
		orbResultSet.orbList.add(orb);
		
		when:
		StateSearchResult stateSearchResult = frontEndStateDao.determineLastGoodStateFromList(clientIds, orbResultSet)
			
		then:
		stateSearchResult.isStateFound()
		stateSearchResult.state == "asdf"
	}
	
	def createOrb(String state, String clientId) {
		Orb orb = new Orb();
		Map<String, String> props = orb.getUserDefinedProperties()
		props.putAt(FrontEndState.ATTR_CLIENT_ID, clientId)
		props.putAt(FrontEndState.ATTR_STATE, state)
		return orb
	}
}
