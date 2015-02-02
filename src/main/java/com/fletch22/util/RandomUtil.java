package com.fletch22.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class RandomUtil {
	
	private SecureRandom random = new SecureRandom();

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
	
	public String getRandomString() {
		return new BigInteger(130, random).toString(32);
	}
}
