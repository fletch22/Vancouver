package com.fletch22.util;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class RandomUtil {

	public int getRandomInteger() {
		Random random = new Random();

		return random.nextInt();
	}

	public int getRandom(int start, int end) {

		int high;
		int low;
		if (start > end) {
			high = start;
			low = end;
		} else {
			high = end;
			low = start;
		}

		Random r = new Random();
		int randomNumber = r.nextInt(high - low) + low;

		return randomNumber;
	}
}
