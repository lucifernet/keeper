package com.timcircle.keeper.react;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.timcircle.keeper.util.ContentUtil;
import com.timcircle.keeper.util.JsonUtil;
import com.timcircle.keeper.util.StringUtil;

public class LogReaction implements IReaction {

	private static Logger logger = Logger.getLogger(LogReaction.class);

	@Override
	public void react(Map<String, Object> args, Map<String, Object> jobBundle) throws Exception {
		logger.debug("---------------- Start to Log ----------------");
		JsonUtil json = new JsonUtil(args);
		List<JsonUtil> logs = json.getJsonArray("logs");
		for (JsonUtil log : logs) {
			String message = log.getString("message");
			logger.debug("Original message " + message);
						
			List<Map<String, Object>> replaceList = log.getMapList("replace");
			logger.debug("There are " + replaceList.size() + " replacments.");
			
			if (replaceList.size() > 0) {				
				message = ContentUtil.handleContent(message, replaceList, jobBundle);
			}

			String level = log.getString("level");
			if (StringUtil.equalsIgnoreCase("DEBUG", level)) {
				logger.debug(message);
			} else if (StringUtil.equalsIgnoreCase("INFO", level)) {
				logger.info(message);
			} else if (StringUtil.equalsIgnoreCase("WARN", level) || StringUtil.equalsIgnoreCase("WARNING", level)) {
				logger.warn(message);
			} else if (StringUtil.equalsIgnoreCase("ERROR", level) || StringUtil.equalsIgnoreCase("ERR", level)) {
				logger.error(message);
			} else if (StringUtil.equalsIgnoreCase("FATAL", level)) {
				logger.fatal(message);
			} else {
				logger.debug(message);
			}
		}
		logger.debug("---------------- End of Log ----------------");
	}

}
