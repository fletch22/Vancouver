package com.fletch22.aop.undo;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.stereotype.Component;

import com.fletch22.aop.Foo;

// This class acts like a dummy class. Mainly it's a stand in for 
// The real implementation class who's interface is FOO. 
// In this way we can use AOP to intercept the call to a method.
// Then in the intercepting Aspect class (Log4EventRedoAspect.java) we carefully
// log the call to method. This is a roundabout technique for logging. We get something 
// strong in return for something difficult to explain. We get design and compile time
// checking of the method call -- the redo method call.

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
