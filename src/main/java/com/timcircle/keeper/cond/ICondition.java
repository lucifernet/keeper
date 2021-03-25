package com.timcircle.keeper.cond;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.timcircle.keeper.print.IPrinter;
import com.timcircle.keeper.util.JarUtil;
import com.timcircle.keeper.util.StringUtil;
import com.timcircle.keeper.AppContext;

public interface ICondition {
	boolean match(Map<String, Object> args, Map<String, Object> jobBundle, IPrinter printer) throws Exception;

	public static final String TYPE_DEFAULT = "default";
	public static final String TYPE_CLASS = "class";

	public static final String COND_NORESPOSNE = "NoResponse";
	public static final String COND_ALWAYS = "Always";

	static ICondition newInstance(AppContext context, String ctype, String cname)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		if (StringUtil.equalsIgnoreCase(ctype, TYPE_DEFAULT)) {
			if (StringUtil.equalsIgnoreCase(cname, COND_NORESPOSNE))
				return new NoReponseCondition();
			if (StringUtil.equalsIgnoreCase(cname, COND_ALWAYS))
				return new AlwaysCondition();
		} else if (StringUtil.equalsIgnoreCase(ctype, TYPE_CLASS)) {
			File extDir = context.getExtsDirectory();
			ICondition condition = JarUtil.initFromJar(extDir.getAbsolutePath(), cname, ICondition.class);
			return condition;
		}
		return new BaseCondition();
	}
}
