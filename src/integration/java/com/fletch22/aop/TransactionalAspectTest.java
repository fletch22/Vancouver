package com.fletch22.aop;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/springContext-test.xml")
public class TransactionalAspectTest {
	
	Logger logger = LoggerFactory.getLogger(TransactionalAspectTest.class);

	@Test
	public void test() {


		Foo foo = new Foo();
		
		foo.manchu();
		
	}

	public static class Foo {
		
		Logger logger = LoggerFactory.getLogger(Foo.class);
		
		@Transactional
		public void manchu() {
			logger.info("This is inside manchu.");
			
			Bar bar = new Bar();
			
			bar.banana();
		}
	}
	
	public static class Bar {
		Logger logger = LoggerFactory.getLogger(Foo.class);
		
		@Transactional
		public void banana() {
			logger.info("This is inside banan method.");
		}
		
	}
}
