package com.timcircle.keeper.cond;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.timcircle.keeper.print.IPrinter;
import com.timcircle.keeper.print.Log4jAdapter;
import com.timcircle.keeper.util.ArgumentUtil;
import com.timcircle.keeper.util.JsonUtil;
import com.timcircle.keeper.AppContext;

public class ConditionHandler {

	private List<JsonUtil> conditions;
	private Map<String, Object> globalArgs;
	private static Logger logger = Logger.getLogger(ConditionHandler.class);

	private AppContext context;

	public ConditionHandler(AppContext context, List<JsonUtil> conditions, Map<String, Object> globalArgs) {
		this.context = context;
		this.conditions = conditions;
		this.globalArgs = globalArgs;
	}

	public boolean matchOne(Map<String, Object> jobBundle) {
		for (JsonUtil cond : conditions) {
			String type = cond.getString("type");
			String name = cond.getString("name");
			Map<String, Object> privateArgs = cond.getMap("arguments");
			Map<String, Object> newArgs = ArgumentUtil.generateArgs(globalArgs, privateArgs);

			ICondition condition;
			try {
				condition = ICondition.newInstance(context, type, name);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e) {
				logger.error("Load ICondition fail : " + name, e);
				continue;
			}
			try {
				IPrinter printer = new Log4jAdapter(logger, condition.getClass());
				if (condition.match(newArgs, jobBundle, printer)) {
					logger.debug("Condition is matched : " + name);
					return true;
				} else {
					logger.debug("Condition is not matched : " + name);
				}
			} catch (Exception ex) {
				logger.error("Execute ICondition with error : " + condition.getClass().getName(), ex);
				return false;
			}
		}
		return false;
	}
}
