package com.fletch22.aop;

import com.fletch22.orb.serialization.GsonSerializable;

public class QueryThing implements GsonSerializable {
	private String thing;
	private Integer that;

	public Integer getThat() {
		return that;
	}

	public void setThat(Integer that) {
		this.that = that;
	}

	public String getThing() {
		return thing;
	}

	public void setThing(String thing) {
		this.thing = thing;
	}
}