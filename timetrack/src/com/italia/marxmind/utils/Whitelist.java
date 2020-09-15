package com.italia.marxmind.utils;

public class Whitelist {

	public static String remove(String val){
		if(val==null) return null;
		val = val.replace("--", "");
		return val;
	}
	
}
