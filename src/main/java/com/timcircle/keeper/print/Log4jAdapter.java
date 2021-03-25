package com.timcircle.keeper.print;

import org.apache.log4j.Logger;

public class Log4jAdapter implements IPrinter {

	private Logger logger;
	private Class<?> clazz;

	public Log4jAdapter(Logger logger, Class<?> clazz) {
		this.logger = logger;
		this.clazz = clazz;
	}

	@Override
	public void d(String message) {
		logger.debug(format(message));
	}

	@Override
	public void i(String message) {
		logger.info(format(message));
	}

	@Override
	public void i(String message, Throwable t) {
		logger.info(format(message), t);
	}
	
	@Override
	public void w(String message) {
		logger.warn(format(message));
	}

	@Override
	public void w(String message, Throwable t) {
		logger.warn(format(message), t);
	}

	@Override
	public void e(String message, Throwable t) {
		logger.error(format(message), t);
	}

	@Override
	public void f(String message, Throwable t) {
		logger.fatal(format(message), t);
	}

	private String format(String message) {
		String format = "[ %s ] - %s";
		return String.format(format, this.clazz.getSimpleName(), message);
	}
}
