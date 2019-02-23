package com.ovl.utils;

import java.util.Random;

public class OverloadRandom {
	private static Random random;
	
	static {
		random = new Random();
	}
	
	public static int next(int i){
		return random.nextInt(i);
	}
}
