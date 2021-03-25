package com.timcircle.keeper.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class JsonUtil {

	private Map<String, Object> _source;

	public JsonUtil(Map<String, Object> source) {
		_source = source;
	}

	public String getString(String key) {
		if (_source.containsKey(key))
			return String.valueOf(_source.get(key));
		return StringUtil.EMPTY;
	}

	public Long getLong(String key, Long defaultValue) {
		if (_source.containsKey(key)) {
			try {
				return ((Double) _source.get(key)).longValue();
			} catch (Exception ex) {
				return defaultValue;
			}
		}
		return defaultValue;
	}

	public int getInt(String key, int defaultValue) {
		if (_source.containsKey(key)) {
			try {
				return ((Double) _source.get(key)).intValue();
			} catch (Exception ex) {
				return defaultValue;
			}
		}
		return defaultValue;
	}

	public Long getLong(String key) {
		return getLong(key, null);
	}

	public String toString() {
		return JsonUtil.toString(_source);
	}

	public static String toString(Object result) {
		Gson gson = new GsonBuilder().serializeNulls().create();
		String rspString = gson.toJson(result);
		return rspString;
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getMapList(String key) {
		if (_source.containsKey(key)) {
			Object obj = _source.get(key);
			return (List<Map<String, Object>>) obj;
		}
		return new ArrayList<>();
	}

	public static List<Map<String, Object>> toList(InputStream inputStream) {
		String content = readFileContent(inputStream);
		return toList(content);
	}

	public static Map<String, Object> toMap(InputStream inputStream) {
		String content = readFileContent(inputStream);
		return toMap(content);
	}

	public static List<Map<String, Object>> toList(String rspString) {
		Type type = new TypeToken<List<Map<String, Object>>>() {
		}.getType();
		return new Gson().fromJson(rspString, type);
	}

	public static Map<String, Object> toMap(String rspString) {
		Type type = new TypeToken<Map<String, Object>>() {
		}.getType();
		return new Gson().fromJson(rspString, type);
	}

	public static JsonUtil toJsonUtil(String rspString) {
		return new JsonUtil(toMap(rspString));
	}

	public boolean hasFields(String... fields) {
		for (String field : fields) {
			if (!_source.containsKey(field)) {
				return false;
			}
		}
		return true;

	}

	public void put(String key, Object value) {
		_source.put(key, value);
	}

	public void renameKey(String oldKey, String newKey) {
		Object value = null;
		if (_source.containsKey(oldKey)) {
			value = _source.get(oldKey);
			_source.remove(oldKey);
		}
		_source.put(newKey, value);
	}

	public Map<String, Object> getMap(String key) {
		if (_source.containsKey(key)) {
			@SuppressWarnings("unchecked")
			Map<String, Object> value = (Map<String, Object>) _source.get(key);
			return value;
		}
		return null;
	}

	public JsonUtil getJson(String key) {
		Map<String, Object> item = getMap(key);
		if (item != null) {
			return new JsonUtil(item);
		}
		return null;
	}

	private static String readFileContent(InputStream inputStream) {
		Scanner sc = new Scanner(inputStream);
		try {
			// Reading line by line from scanner to StringBuffer
			StringBuffer sb = new StringBuffer();
			while (sc.hasNext()) {
				sb.append(sc.nextLine());
			}
			return sb.toString();
		} finally {
			sc.close();
		}
	}

	public List<JsonUtil> getJsonArray(String key) {
		List<JsonUtil> array = new ArrayList<>();
		if (_source != null) {
			for (Map<String, Object> item : getMapList(key)) {
				array.add(new JsonUtil(item));
			}
		}
		return array;
	}

	@SuppressWarnings("rawtypes")
	public List<String> getStringList(String key) {
		List<String> list = new ArrayList<>();
		if (_source != null) {
			List values = (List) _source.get(key);
			for (Object value : values) {
				if (value instanceof String)
					list.add((String) value);
			}
		}
		return list;
	}

	public static JsonUtil toJsonUtil(File file) throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		Map<String, Object> map = toMap(fis);
		return new JsonUtil(map);
	}

	public static Map<String, Object> toMap(File file) throws FileNotFoundException {
		FileInputStream fis = new FileInputStream(file);
		return toMap(fis);
	}
}
