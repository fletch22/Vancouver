package com.fletch22.orb;

public interface JsonSerializeable<T> {

	public T fromJson();
	public String toJson();
}
