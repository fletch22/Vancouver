//package com.fletch22.orb;
//
//import static org.junit.Assert.*
//
//import org.junit.Test
//
//import spock.lang.Shared
//import spock.lang.Specification
//
//import com.fletch22.orb.cache.external.OrbTypeManagerExternalCache;
//import com.fletch22.orb.command.orbType.DeleteOrbTypeCommand
//import com.fletch22.orb.command.orbType.dto.AddOrbTypeDto
//import com.fletch22.orb.rollback.UndoActionBundle
//import com.fletch22.redis.RedisObjectInstanceCacheService
//import com.fletch22.redis.ObjectTypeCacheService
//
//
//class OrbTypeManagerForExternalCacheSpec extends Specification {
//	
//	@Shared OrbTypeManagerExternalCache orbTypeManager
//	
//	@Shared ObjectTypeCacheService objectTypeCacheService
//	@Shared RedisObjectInstanceCacheService redisObjectInstanceCacheService
//	@Shared InternalIdGenerator internalIdGenerator
//	@Shared CommandExpressor commandExpressor
//	@Shared DeleteOrbTypeCommand deleteOrbTypeCommand
//	
//	def setup() {
//		this.objectTypeCacheService = Mock(ObjectTypeCacheService)
//		this.redisObjectInstanceCacheService = Mock(RedisObjectInstanceCacheService)
//		this.internalIdGenerator = Mock(InternalIdGenerator)
//		this.commandExpressor = Mock(CommandExpressor)
//		this.deleteOrbTypeCommand = Mock(DeleteOrbTypeCommand)
//		
//		this.orbTypeManager = new OrbTypeManagerExternalCache()
//		
//		orbTypeManager.objectTypeCacheService = objectTypeCacheService;
//		orbTypeManager.objectInstanceCacheService = redisObjectInstanceCacheService
//		orbTypeManager.internalIdGenerator = this.internalIdGenerator
//		orbTypeManager.commandExpressor = this.commandExpressor
//		orbTypeManager.deleteOrbTypeCommand = this.deleteOrbTypeCommand
//	}	
//
//	@Test
//	def 'test creation of type'() {
//		
//		given:
//		setup()
//		
//		def expectedLabel = 'foo'
//		def expectedTranDate = new BigDecimal("123123213.12312213")
//		def expectedInternalId = 123
//		AddOrbTypeDto addOrbTypeDto = new AddOrbTypeDto(expectedLabel, expectedInternalId)
//		BigDecimal tranDate = expectedTranDate
//		UndoActionBundle rollbackAction = new UndoActionBundle()
//		
//		this.objectTypeCacheService.doesObjectTypeExist(*_) >> false
//		
//		when:
//		orbTypeManager.createOrbType(addOrbTypeDto, tranDate, rollbackAction)
//		
//		then:
//		rollbackAction.getActions().size() == 1
//	}
//
//}
//
