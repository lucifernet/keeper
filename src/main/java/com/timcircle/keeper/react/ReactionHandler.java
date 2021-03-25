package com.timcircle.keeper.react;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.timcircle.keeper.util.ArgumentUtil;
import com.timcircle.keeper.util.JsonUtil;
import com.timcircle.keeper.AppContext;

public class ReactionHandler {

	private static Logger logger = Logger.getLogger(ReactionHandler.class);

	private List<JsonUtil> reactions;
	private Map<String, Object> globalArgs;
	private AppContext context;

	public ReactionHandler(AppContext context, List<JsonUtil> reactions, Map<String, Object> globalArgs) {
		this.reactions = reactions;
		this.globalArgs = globalArgs;
		this.context = context;
	}

	public void doAll(Map<String, Object> jobBundle) {
		for (JsonUtil cond : reactions) {
			String type = cond.getString("type");
			String name = cond.getString("name");
			Map<String, Object> privateArgs = cond.getMap("arguments");
			Map<String, Object> newArgs = ArgumentUtil.generateArgs(globalArgs, privateArgs);

			IReaction reaction;
			try {
				reaction = IReaction.newInstance(context, type, name);
				logger.debug("Reaction : " + reaction.getClass().getName());
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e1) {
				logger.error("Load IReaction fail : " + name, e1);
				return;
			}

			try {
				reaction.react(newArgs, jobBundle);
				logger.debug("Reaction " + reaction.getClass().getName() + " is done.");
			} catch (Exception e) {
				logger.error("Reaction execution error : " + name, e);
			}
		}
	}
}
