package com.fletch22.orb;

import static org.junit.Assert.*

import org.junit.Test

import spock.lang.Shared
import spock.lang.Specification

import com.fletch22.orb.command.orbType.DeleteOrbTypeCommand
import com.fletch22.orb.command.orbType.dto.AddOrbTypeDto
import com.fletch22.orb.rollback.UndoActionBundle
import com.fletch22.redis.ObjectInstanceCacheService
import com.fletch22.redis.ObjectTypeCacheService

class OrbTypeManagerSpec extends Specification {
	
	@Shared OrbTypeManager orbTypeManager
	
	@Shared ObjectTypeCacheService objectTypeCacheService
	@Shared ObjectInstanceCacheService objectInstanceCacheService
	@Shared InternalIdGenerator internalIdGenerator
	@Shared CommandExpressor commandExpressor
	@Shared DeleteOrbTypeCommand deleteOrbTypeCommand
	
	def setup() {
		this.objectTypeCacheService = Mock(ObjectTypeCacheService)
		this.objectInstanceCacheService = Mock(ObjectInstanceCacheService)
		this.internalIdGenerator = Mock(InternalIdGenerator)
		this.commandExpressor = Mock(CommandExpressor)
		this.deleteOrbTypeCommand = Mock(DeleteOrbTypeCommand)
		
		this.orbTypeManager = new OrbTypeManager()
		
		orbTypeManager.objectTypeCacheService = objectTypeCacheService;
		orbTypeManager.objectInstanceCacheService = objectInstanceCacheService
		orbTypeManager.internalIdGenerator = this.internalIdGenerator
		orbTypeManager.commandExpressor = this.commandExpressor
		orbTypeManager.deleteOrbTypeCommand = this.deleteOrbTypeCommand
	}	

	@Test
	def 'test creation of type'() {
		
		given:
		setup()
		
		def expectedLabel = 'foo'
		def expectedTranDate = new BigDecimal("123123213.12312213")
		def expectedInternalId = 123
		AddOrbTypeDto addOrbTypeDto = new AddOrbTypeDto(expectedLabel, expectedInternalId)
		BigDecimal tranDate = expectedTranDate
		UndoActionBundle rollbackAction = new UndoActionBundle()
		
		this.objectTypeCacheService.doesObjectTypeExist(*_) >> false
		
		when:
		orbTypeManager.createOrbType(addOrbTypeDto, tranDate, rollbackAction)
		
		then:
		rollbackAction.getActions().size() == 1
	}

}

