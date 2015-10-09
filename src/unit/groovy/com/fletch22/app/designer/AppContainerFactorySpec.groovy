package com.fletch22.app.designer;

import static org.junit.Assert.*

import org.junit.Test

import spock.lang.Specification

import com.fletch22.orb.Orb
import com.fletch22.orb.OrbType
import com.fletch22.orb.OrbTypeManager
import com.fletch22.orb.query.OrbResultSet
import com.fletch22.orb.query.QueryManager

class AppContainerFactorySpec extends Specification {

	@Test
	public void testSuccess() {
		
		given:
		AppContainerDao appContainerFactory = new AppContainerDao()
		appContainerFactory.orbTypeManager = Mock(OrbTypeManager)
		appContainerFactory.queryManager = Mock(QueryManager)
		
		OrbType orbType = Mock(OrbType)
		orbType.id = 123
		appContainerFactory.orbTypeManager.getOrbType(AppContainer.TYPE_LABEL) >> orbType
		
		OrbResultSet orbResultSet = Mock(OrbResultSet)
		
		List<Orb> orbList = new ArrayList<Orb>()
		orbResultSet.orbList = orbList
		orbList.add(new Orb())
		orbResultSet.getOrbList() >> orbList
		
		appContainerFactory.queryManager.findByAttribute(_, _, _) >> orbResultSet
			
		when:
		orbResultSet
		AppContainer appContainer = appContainerFactory.read("foo")
		
		then:
		appContainer		
	}

}
