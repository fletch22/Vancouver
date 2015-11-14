package com.fletch22.orb.query;

import static org.junit.Assert.*
import spock.lang.Specification

import com.fletch22.orb.Orb
import com.fletch22.orb.OrbManager
import com.fletch22.orb.OrbType
import com.fletch22.orb.OrbTypeManager
import com.fletch22.orb.cache.local.Cache
import com.fletch22.orb.cache.query.QueryCollection
import com.fletch22.orb.query.CriteriaFactory.Criteria
import com.fletch22.orb.systemType.SystemType

class QueryManagerImplUnitSpec extends Specification {

	def 'test find query'() {
		
		given:
		OrbTypeManager orbTypeManager = Mock(OrbTypeManager)
		OrbManager orbManager = Mock(OrbManager)

		QueryManagerImpl queryManagerImpl = new QueryManagerImpl()

		queryManagerImpl.orbTypeManager = orbTypeManager
		queryManagerImpl.orbManager = orbManager

		OrbType queryOrbType = Mock(OrbType)
		orbTypeManager.getOrbType(SystemType.CRITERIA.getLabel()) >> queryOrbType

		List<Orb> orbList = new ArrayList<Orb>()
		addQueryOrb(orbList, "bar")
		addQueryOrb(orbList, "cat")
		addQueryOrb(orbList, "foo")
		addQueryOrb(orbList, "dog")

		orbManager.getOrbsOfType(_) >> orbList

		Cache cache = Mock(Cache)
		QueryCollection queryCollection = Mock(QueryCollection)
		cache.queryCollection = queryCollection
		queryManagerImpl.cache = cache

		long idToFind = 123
		Criteria criteria = Mock(Criteria)
		criteria.getLabel() >> "foo"
		criteria.getOrbTypeInternalId() >> idToFind
		queryCollection.getByQueryId(_) >> criteria

		when:
		Criteria criteriaFound = queryManagerImpl.findQuery(idToFind, "foo")

		then:
		criteriaFound
		criteriaFound.is(criteria)
	}
	
	private addQueryOrb(List orbList, String label) {
		Orb orb = new Orb()
		orb.getUserDefinedProperties().put(SystemType.CRITERIA, label);
		orbList.add(orb)
	}
}
