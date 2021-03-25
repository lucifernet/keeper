package com.timcircle.keeper.cond;

import java.util.Map;

import com.timcircle.keeper.print.IPrinter;

public class BaseCondition implements ICondition {

	@Override
	public boolean match(Map<String, Object> args, Map<String, Object> jobBundle, IPrinter printer) {
		return false;
	}

}
