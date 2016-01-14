package com.fletch22.util;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class RandomUtil {

	private static final char[] symbols;

	static {
		StringBuilder tmp = new StringBuilder();
		for (char ch = '0'; ch <= '9'; ++ch)
			tmp.append(ch);
		for (char ch = 'a'; ch <= 'z'; ++ch)
			tmp.append(ch);
		symbols = tmp.toString().toCharArray();
	}

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

	public String getRandomString(int length) {

		char[] buf;

		if (length < 1) {
			throw new IllegalArgumentException("length < 1: " + length);
		}

		buf = new char[length];

		for (int idx = 0; idx < buf.length; ++idx) {
			buf[idx] = symbols[random.nextInt(symbols.length)];
		}
		
		return new String(buf);
	}
	
	public String getRandomUuidString() {
		return UUID.randomUUID().toString();
	}
}
