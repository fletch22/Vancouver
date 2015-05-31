import static com.googlecode.cqengine.query.QueryFactory.endsWith;
import static com.googlecode.cqengine.query.QueryFactory.equal;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.SimpleAttribute;
import com.googlecode.cqengine.attribute.SimpleNullableAttribute;
import com.googlecode.cqengine.codegen.AttributeBytecodeGenerator;
import com.googlecode.cqengine.index.navigable.NavigableIndex;
import com.googlecode.cqengine.index.standingquery.StandingQueryIndex;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.query.option.QueryOptions;
import com.googlecode.cqengine.resultset.ResultSet;

public class CqEngineTest {

	Logger logger = LoggerFactory.getLogger(CqEngineTest.class);

	@Test
	public void test() {
		// logger.info(AttributeSourceGenerator.generateAttributesForPastingIntoTargetClass(Car.class));
		IndexedCollection<Car> cars = new ConcurrentIndexedCollection<Car>();
		Class clazz = AttributeBytecodeGenerator.generateSimpleNullableAttributeForParameterizedGetter(Car.class, String.class, "getListValue", "2", "LIST_VALUE");
		
		SimpleNullableAttribute<Car, String> thirdValue = null;
		try {
			thirdValue = (SimpleNullableAttribute<Car, String>) clazz.newInstance();
			logger.info("Type: {}", thirdValue.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		cars.addIndex(NavigableIndex.onAttribute(thirdValue));
		//cars.addIndex(NavigableIndex.onAttribute(thirdValue));
		
		// 20-25% slower
		cars.addIndex(StandingQueryIndex.onQuery(equal(thirdValue, "Banana")));

		List<String> list = new ArrayList<String>();
		list.add("Pear");
		list.add("Apple");
		list.add("Banana");
		Car car1 = new Car(0, "red", list);
		cars.add(car1);
		
		int numberObjects = 1000000;
		for (int i = 1; i < numberObjects; i++) {
			cars.add(new Car(i, "red", list));
		}
		

		Query query1 = equal(thirdValue, "Banana");
		ResultSet<Car> resultSet = cars.retrieve(query1);
		logger.info("Found: {} cars", resultSet.size());
		for (Car car : resultSet) {
			//logger.info("Card ID found: {}", car.id);
		}
		
//		doQuery();
		
	}

	private void doQuery() {
		// Arrange
		IndexedCollection<Car> cars = new ConcurrentIndexedCollection<Car>();
		cars.addIndex(NavigableIndex.onAttribute(Car.CAR_ID));

		List<String> list = new ArrayList<String>();
		list.add("Banana");

		int numberObjects = 1000000;
		for (int i = 0; i < numberObjects; i++) {
			Car car1 = new Car(i, "red", list);
			cars.add(car1);
		}

		// Act
		Query query1 = endsWith(Car.FIRST_CUSTOM_ATTRIBUTE, "Banana");
		ResultSet<Car> resultSet = cars.retrieve(query1);
		logger.info("Found: {} cars", resultSet.size());
		for (Car car : resultSet) {
			logger.info("Card ID found: {}", car.id);
		}
	}

	public static class Car {

		Integer id;
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
}
