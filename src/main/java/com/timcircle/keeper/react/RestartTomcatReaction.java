package com.timcircle.keeper.react;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.timcircle.keeper.util.JsonUtil;

public class RestartTomcatReaction implements IReaction {

	private static Logger logger = Logger.getLogger(RestartTomcatReaction.class);

	public void react(Map<String, Object> args, Map<String, Object> jobBundle) throws Exception {
		String command = new JsonUtil(args).getString("command");
		logger.info("Try to restart tomcat");

		Map<String, Object> bundle = new HashMap<>();
		jobBundle.put(RestartTomcatReaction.class.getSimpleName(), bundle);
		bundle.put("command", command);
		
		try {
			String[] cmds = command.split(" ");
			Process proc = Runtime.getRuntime().exec(cmds);
			proc.waitFor();
			logger.info("command executed successfully.");
			bundle.put("succeed", true);
		} catch (Exception ex) {
			logger.error("Restart tomcat fail.", ex);
			bundle.put("succeed", false);
			bundle.put("error", ex.getMessage());
		}
	}
}
