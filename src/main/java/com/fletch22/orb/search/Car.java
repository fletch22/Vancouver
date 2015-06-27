package com.fletch22.orb.search;

import java.util.List;

import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.attribute.SimpleNullableAttribute;
import com.googlecode.cqengine.query.option.QueryOptions;

public class Car {

	public Integer id;
	String color;
	List<String> list;
	int sizeOfList;

	public Car(Integer id, String color, List<String> list) {
		this.id = id;
		this.color = color;
		this.list = list;
		this.sizeOfList = list.size();
	}

	public String getListValue(String index) {
		int i = Integer.parseInt(index);

		return (sizeOfList > i) ? list.get(i) : null;
	}

	/**
	 * CQEngine attribute for accessing field {@code Car.color}.
	 */
	// Note: For best performance:
	// - if this field cannot be null, replace this SimpleNullableAttribute
	// with
	// a SimpleAttribute
	public static final Attribute<Car, String> COLOR = new SimpleNullableAttribute<Car, String>("COLOR") {
		public String getValue(Car car, QueryOptions queryOptions) {
			return car.color;
		}
	};

	/**
	 * CQEngine attribute for accessing field {@code Car.first value}.
	 */
	// Note: For best performance:
	// - if this field cannot be null, replace this SimpleNullableAttribute
	// with
	// a SimpleAttribute
	public static final Attribute<Car, String> FIRST_CUSTOM_ATTRIBUTE = new SimpleNullableAttribute<Car, String>("FIRST_CUSTOM_ATTRIBUTE") {
		public String getValue(Car car, QueryOptions queryOptions) {
			return (car.list.size() > 0) ? car.list.get(0) : null;
		}
	};

	/**
	 * CQEngine attribute for accessing field {@code Car.id}.
	 */
	// Note: For best performance:
	// - if this field cannot be null, replace this SimpleNullableAttribute
	// with
	// a SimpleAttribute
	public static final Attribute<Car, Integer> CAR_ID = new SimpleAttribute<Car, Integer>("CAR_ID") {
		public Integer getValue(Car car, QueryOptions queryOptions) {
			return car.id;
		}
	};
}