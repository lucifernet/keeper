package com.timcircle.keeper.util;

import java.util.HashMap;
import java.util.Map;

public class ArgumentUtil {
	@SuppressWarnings("unchecked")
	public static Map<String, Object> generateArgs(Map<String, Object> globalArgs, Map<String, Object> privateArgs) {
		Map<String, Object> newArgs = new HashMap<>();
		if (globalArgs != null) {
			for (String key : globalArgs.keySet()) {
				newArgs.put(key, globalArgs.get(key));
			}
		}
		if (privateArgs != null) {
			privateArgs = handleMapValues(globalArgs, privateArgs);
			for (String key : privateArgs.keySet()) {
				newArgs.put(key, privateArgs.get(key));
			}
		}
		return newArgs;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Map handleMapValues(Map<String, Object> globalArgs, Map map) {
		if (map != null) {
			for (Object key : map.keySet()) {
				Object value = map.get(key);
				if (value instanceof String) {
					value = handleStringValue(globalArgs, (String) value);
				} else if (value instanceof Map) {
					value = handleMapValues(globalArgs, (Map) value);
				}
				map.put(key, value);
			}
		}
		return map;
	}

	private static Object handleStringValue(Map<String, Object> globalArgs, String value) {
		if (StringUtil.isNotEmpty(value) && value.contains("$")) {
			if(value.startsWith("$")) {
				String key = value.substring(1);
				if (globalArgs != null && globalArgs.containsKey(key)) {
					return globalArgs.get(key);
				}
			}
			
			for(String key: globalArgs.keySet()) {
				Object globalValue = globalArgs.get(key);
				if(globalValue instanceof String) {
					String oldChar = "$" + key;
					value = value.replace(oldChar, (String) globalValue);
				}
			}
		}
		return value;
	}
}
