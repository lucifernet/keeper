package com.timcircle.keeper.react;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.timcircle.keeper.util.JarUtil;
import com.timcircle.keeper.util.StringUtil;
import com.timcircle.keeper.AppContext;

public interface IReaction {
	void react(Map<String, Object> args, Map<String, Object> jobBundle) throws Exception;

	public static final String TYPE_DEFAULT = "default";
	public static final String TYPE_CLASS = "class";

	public static final String REACT_EXEC_SCRIPT = "ExecScript";
	public static final String REACT_RESTART_TOMCAT = "RestartTomcat";
	public static final String REACT_SEND_EMAIL = "SendEmail";
	public static final String REACT_DO_NOTHING = "DoNothing";
	public static final String REACT_LOG = "Log";

	static IReaction newInstance(AppContext context, String rtype, String rname)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		if (StringUtil.equalsIgnoreCase(rtype, TYPE_DEFAULT)) {
			if (StringUtil.equalsIgnoreCase(rname, REACT_EXEC_SCRIPT))
				return new ExecScriptReaction();
			if (StringUtil.equalsIgnoreCase(rname, REACT_RESTART_TOMCAT))
				return new RestartTomcatReaction();
			if (StringUtil.equalsIgnoreCase(rname, REACT_SEND_EMAIL))
				return new SendEmailReaction();
			if (StringUtil.equalsIgnoreCase(rname, REACT_LOG))
				return new LogReaction();			
		} else if (StringUtil.equalsIgnoreCase(rtype, TYPE_CLASS)) {			
			File extDir = context.getExtsDirectory();
			IReaction reaction = JarUtil.initFromJar(extDir.getAbsolutePath(), rname, IReaction.class);
			return reaction;
		}
		return new DoNothingReaction();
	}
}
