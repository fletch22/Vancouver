package com.fletch22.util;

import java.util.Random;

public class RandomUtil {

	public int getRandomInteger() {
		Random random = new Random();
		
		return random.nextInt();
	}
}
