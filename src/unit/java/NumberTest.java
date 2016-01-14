import static org.junit.Assert.*;

import org.junit.Test;

public class NumberTest {

	@Test
	public void test() {

		Sequence sequence = new Sequence();

		sequence.test1();
	}

	public static class Sequence {

		public void test1() {

			float i = 0 / 0f;

			float goat = 45.0f;

			mysterious(i);
		}

		public void mysterious(double x) {

			if (x != x) {
				System.out.println("Mind BLOWN.");
			} else {
				System.out.println("Ho hum. Nothing to see here.");
			}

		}

	}

}
