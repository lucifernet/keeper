package com.timcircle.keeper.print;

public interface IPrinter {
	void d(String message);
	void i(String message);
	void i(String message, Throwable t);
	void w(String message);
	void w(String message, Throwable t);
	void e(String message, Throwable t);
	void f(String message, Throwable t);
}
