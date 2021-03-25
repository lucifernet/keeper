package com.timcircle.keeper.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class ContentUtil {

	private static final Logger logger = Logger.getLogger(ContentUtil.class);

	public static String handleContent(String content, List<Map<String, Object>> replaceList,
			Map<String, Object> jobBundle) {
		for (Map<String, Object> r : replaceList) {

			JsonUtil replace = new JsonUtil(r);
			for (String key : r.keySet()) {
				String param = "$" + key;

				if (!content.contains(param)) {
					logger.debug("The content does not contain key :" + param);
					continue;
				}

				JsonUtil item = replace.getJson(key);
				String bundle = item.getString("bundle");
				String[] bundlePaths = bundle.split("/");
				if (bundlePaths.length > 0) {
					String bundleName = bundlePaths[0];
					JsonUtil jb = new JsonUtil(jobBundle);
					if (jobBundle.containsKey(bundleName)) {
						List<String> keys = new ArrayList<>();
						for (int i = 1; i < bundlePaths.length; i++) {
							keys.add(bundlePaths[i]);
						}

						Map<String, Object> b = jb.getMap(bundleName);
						String value = findBundleValue(b, keys);
						logger.debug("key : " + key + " value : " + value);

						List<JsonUtil> converters = item.getJsonArray("converters");
						for (JsonUtil converter : converters) {
							String iif = converter.getString("if");
							if (StringUtil.equalsStr(iif, value)) {
								String converted = converter.getString("then");
								value = converted;
								logger.debug("value converted to :" + converted);
								break;
							}
						}

						content = content.replace(param, value);
					} else {
						logger.debug("The job bundle does not contain bundle :" + bundleName);
					}
				}
			}
		}
		return content;
	}

	@SuppressWarnings("rawtypes")
	private static String findBundleValue(Map bundle, List<String> keys) {
		logger.debug("bundle content is :" + bundle);

		String key = keys.get(0);
		keys.remove(0);

		try {
			logger.debug("findBundleValue is handling : " + key);
			if (keys.size() == 0) {
				if (bundle.containsKey(key)) {
					logger.debug("bundle contains key :" + key);
					Object value = bundle.get(key);
					if (value instanceof String)
						return (String) value;
					return String.valueOf(value);
				} else {
					logger.debug("bundle does not contain key :" + key);
				}
			} else {
				if (bundle.containsKey(key)) {
					Object value = bundle.get(key);
					if (value instanceof Map) {
						return findBundleValue((Map) value, keys);
					}
				}
			}
			logger.debug("BundleValue is not found : " + key);
			return StringUtil.EMPTY;
		} finally {
			logger.debug("BundleValue is not found : " + key);
		}
	}
}
