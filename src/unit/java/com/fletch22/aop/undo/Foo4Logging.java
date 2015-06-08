package com.fletch22.aop.undo;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

import com.fletch22.aop.Foo;

@Component("foo4Logging")
public class Foo4Logging implements Foo {
	
	public void bar3() {
		throw new NotImplementedException("bar3 Test");
	}

	@Override
	public void bar() {
		throw new NotImplementedException("bar Test");
	}

	@Override
	public void bar2() {
		throw new NotImplementedException("bar2 Test");
	}

	@Override
	public void bar4(String banana, Long numberOfSeeds) {
		throw new NotImplementedException("bar4 Test");
	}

}
