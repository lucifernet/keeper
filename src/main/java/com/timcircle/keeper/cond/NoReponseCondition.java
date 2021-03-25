package com.timcircle.keeper.cond;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.timcircle.keeper.print.IPrinter;
import com.timcircle.keeper.util.HttpUtil;
import com.timcircle.keeper.util.JsonUtil;
import com.timcircle.keeper.util.StringUtil;

public class NoReponseCondition implements ICondition {

	private static final String URL = "http://127.0.0.1/index.jsp";

	//private static Logger logger = Logger.getLogger(NoReponseCondition.class);

	public boolean match(Map<String, Object> args, Map<String, Object> jobBundle, IPrinter printer) {
		JsonUtil json = new JsonUtil(args);
		String url = json.getString("watch_url");
		if (StringUtil.isEmpty(url)) {
			printer.w("watch_url was empty. use default url : " + URL);
			url = URL;
		}

		CallURL callUrl = new CallURL(url);
		Thread t = new Thread(callUrl);
		t.start();

		long timeout = json.getLong("timeout", 30000L);

		ExecutorService poolService = Executors.newSingleThreadExecutor();
		try {
			Future<String> future = poolService.submit(new CallURLTask(url));
			try {
				long st = System.currentTimeMillis();
				String result = future.get(timeout, TimeUnit.MILLISECONDS);
				long spend = System.currentTimeMillis() - st;
				printer.i("Call url succeed. content length is :" + result.length() + " spend " + spend + " ms.");
				return false;
			} catch (InterruptedException e) {
				printer.i("Call url was interrupted.", e);
				return true;
			} catch (ExecutionException e) {
				printer.i("Call url occured execution exception.", e);
				return true;
			} catch (TimeoutException e) {
				printer.i("Call url timeout.");
				return true;
			}
		} finally {
			poolService.shutdown();
		}
	}

	class CallURLTask implements Callable<String> {
		private String url;

		public CallURLTask(String url) {
			this.url = url;
		}

		@Override
		public String call() throws Exception {
			try {
				return HttpUtil.sendGet(this.url);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}

	}

	public static CompletableFuture<String> callURL(String url, ExecutorService service) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return HttpUtil.sendGet(url);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}, service);
	}

	class CallURL implements Runnable {

		private String url;
		private String result;
		private Exception exception;

		public CallURL(String url) {
			this.url = url;
		}

		@Override
		public void run() {
			try {
				this.result = HttpUtil.sendGet(url);
			} catch (Exception e) {
				this.exception = e;
			}
		}

		public String getResult() {
			return this.result;
		}

		public Exception getException() {
			return this.exception;
		}
	}
}
