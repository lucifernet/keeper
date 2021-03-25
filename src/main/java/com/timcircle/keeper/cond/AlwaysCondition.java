package com.timcircle.keeper.cond;

import java.util.Map;

import com.timcircle.keeper.print.IPrinter;

public class AlwaysCondition implements ICondition {

	@Override
	public boolean match(Map<String, Object> args, Map<String, Object> jobBundle, IPrinter printer) throws Exception {
		return true;
	}

}
