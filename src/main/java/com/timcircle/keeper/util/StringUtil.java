package com.timcircle.keeper.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class StringUtil {

	public static final String EMPTY = "";

	public static boolean isEmpty(String value) {
		if (value == null)
			return true;
		return value.trim().isEmpty();
	}

	public static boolean isNotEmpty(String value) {
		return !isEmpty(value);
	}

	public static boolean equalsStr(String a, String... in_b) {
		a = notNullString(a);
		for (String bItem : in_b) {
			bItem = notNullString(bItem);
			if (a.equals(bItem))
				return true;
		}
		return false;
	}

	public static boolean equalsIgnoreCase(String a, String... in_b) {
		a = notNullString(a);
		for (String bItem : in_b) {
			bItem = notNullString(bItem);
			if (a.equalsIgnoreCase(bItem))
				return true;
		}
		return false;
	}

	public static String notNullString(String value) {
		if (isEmpty(value))
			return EMPTY;
		return value;
	}

	public static String enc(String string) {		
		try {
			return URLEncoder.encode(string, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return string;
		}
	}

	public static boolean containsIgnorecase(String env, String string) {
		if(isEmpty(env) || isEmpty(string)) return false;
		return env.toLowerCase().contains(string.toLowerCase());
	}
}
