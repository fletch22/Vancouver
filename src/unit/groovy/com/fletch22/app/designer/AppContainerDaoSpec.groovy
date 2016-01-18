package com.fletch22.app.designer;

import static org.junit.Assert.*

import org.junit.Test
import org.mockito.Mockito

import spock.lang.Specification

import com.fletch22.app.designer.appContainer.AppContainer
import com.fletch22.app.designer.appContainer.AppContainerDao
import com.fletch22.app.designer.appContainer.AppContainerTransformer
import com.fletch22.app.designer.website.WebsiteTransformer
import com.fletch22.orb.Orb
import com.fletch22.orb.OrbType
import com.fletch22.orb.OrbTypeManager
import com.fletch22.orb.query.OrbResultSet
import com.fletch22.orb.query.QueryManager

class AppContainerDaoSpec extends Specification {

	@Test
	public void testSuccess() {
		
		given:
		AppContainerDao appContainerDao = new AppContainerDao()
		
		def model = Mockito.mock(AppContainerTransformer.class)
		
		def foo = new AppContainerTransformer<AppContainer>()
		
		appContainerDao.appContainerTransformer = Mock(AppContainerTransformer)
		appContainerDao.orbTypeManager = Mock(OrbTypeManager)
		appContainerDao.queryManager = Mock(QueryManager)
		
		OrbType orbType = Mock(OrbType)
		orbType.id = 123
		appContainerDao.orbTypeManager.getOrbType(AppContainer.TYPE_LABEL) >> orbType

		OrbResultSet orbResultSet = Mock(OrbResultSet)
		
		Orb orb = new Orb()
		orb.setOrbInternalId(234)
		orb.getUserDefinedProperties().put(AppContainer.ATTR_LABEL, "foo")
		orbResultSet.uniqueResult() >> orb
		
		appContainerDao.queryManager.findByAttribute(_, _, _) >> orbResultSet
			
		when:
		orbResultSet
		AppContainer appContainer = appContainerDao.findByLabel("foo")
		
		then:
		notThrown Exception
	}

}
