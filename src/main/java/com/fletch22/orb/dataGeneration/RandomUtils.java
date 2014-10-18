package com.fletch22.orb.dataGeneration;

import java.util.Random;

public class RandomUtils {

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
		int randomNumber = r.nextInt(high-low) + low;
		
		return randomNumber;
	}
}
