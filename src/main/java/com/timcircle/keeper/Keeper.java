package com.timcircle.keeper;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.timcircle.keeper.cond.ConditionHandler;
import com.timcircle.keeper.react.ReactionHandler;
import com.timcircle.keeper.util.JsonUtil;

public class Keeper {

	private static Logger logger = Logger.getLogger(Keeper.class);

	public static void main(String[] args) throws FileNotFoundException, Exception {

		if (args.length > 0) {
			AppContext context = new AppContext(args[0]);
			Keeper keeper = new Keeper();
			keeper.doJob(context);
		} else {
			throw new Exception("Missing root directory.");
		}
//		Map<String, Object> keepConfig;
//		if (args.length > 0) {
//			String keepConfigFile = args[0];
//			File file = new File(keepConfigFile);
//			if (!file.exists()) {
//				logger.error("configuration file does not exists : " + keepConfigFile);
//				return;
//			} else {
//				FileInputStream fis = new FileInputStream(file);
//				keepConfig = JsonUtil.toMap(fis);
//			}
//		} else {
//			// load file from the same folder.
//			String path = System.getProperty("user.dir");
//			logger.debug(path);
//			File file = new File(path);
//			file = new File(file, "config.json");
//			if (file.exists()) {
//				logger.debug("Load configuration from : " + file.getAbsolutePath());
//				keepConfig = JsonUtil.toMap(new FileInputStream(file));
//			} else {
//				logger.debug("Use default configuration.");
//				InputStream inputStream = Keeper.class.getClassLoader().getResourceAsStream("default.json");
//				keepConfig = JsonUtil.toMap(inputStream);
//			}
//		}
//
//		String logConfig;
//		if (args.length > 1) {
//			logConfig = args[1];
//
//			File file = new File(logConfig);
//			if (!file.exists()) {
//				logger.error("log4j file does not exists : " + logConfig);
//				return;
//			} else {
//				PropertyConfigurator.configure(logConfig);
//			}
//		} else {
//			// load file from the same folder.
//			String path = System.getProperty("user.dir");
//			logger.debug(path);
//			File file = new File(path);
//			file = new File(file, "log4j.properties");
//			if (file.exists()) {
//				logger.debug("Load log4j properties from : " + file.getAbsolutePath());
//				PropertyConfigurator.configure(file.getAbsolutePath());
//			} else {
//				logger.debug("Use default log4j properties.");
//			}
//		}
//		
//		if(args.length > 2) {
//			String extPath = args[2];
//			File file = new File(extPath);
//			if (!file.exists()) {
//				logger.error("ext folder does not exists : " + extPath);
//				return;
//			} else {
//				EXT_PATH = file.getAbsolutePath();
//				logger.debug("Load extenions from " + EXT_PATH);
//			}
//		} else {
//			EXT_PATH = new File(System.getProperty("user.dir"), "ext").getAbsolutePath();
//			logger.debug("Load extenions from default folder :" + EXT_PATH);
//		}
//
//		Keeper keeper = new Keeper();
//		keeper.doJob(keepConfig);
	}

//	public static String EXT_PATH = "";

	public void doJob(AppContext context) throws FileNotFoundException {
		JsonUtil config = context.getConfig();
//		JsonUtil config = new JsonUtil(configMap);
//		if (configMap.containsKey("ext_path"))
//			EXT_PATH = config.getString("ext_path");
//		else {
//			//File jarDir = new File(ClassLoader.getSystemClassLoader().getResource(".").getPath());
//			File jarDir = new File(".");
//			EXT_PATH = new File(jarDir, "ext").getAbsolutePath();
//		}

		List<Map<String, Object>> jobs = config.getMapList("jobs");

		// load plugin jobs
		List<Map<String, Object>> extJobs = context.getExtJobs();
		jobs.addAll(extJobs);

		Map<String, Object> globalArgs = config.getMap("global_arguments");
		Map<String, Object> jobBundle = new HashMap<>();

		for (Map<String, Object> job : jobs) {
			JsonUtil json = new JsonUtil(job);
			String jobName = json.getString("name");
			logger.info("A job start : " + jobName);

			List<JsonUtil> conditions = json.getJsonArray("conditions");

			if (conditions.size() == 0) {
				logger.info("Nothing to do.");
				continue;
			}

			ConditionHandler condHandler = new ConditionHandler(context, conditions, globalArgs);
			if (condHandler.matchOne(jobBundle)) {				
				List<JsonUtil> reactions = json.getJsonArray("reactions");
				ReactionHandler reactHandler = new ReactionHandler(context, reactions, globalArgs);
				reactHandler.doAll(jobBundle);
			}
			logger.info("A job is done : " + jobName);
		}
	}
}
