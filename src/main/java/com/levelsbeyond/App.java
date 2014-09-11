package com.levelsbeyond;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.script.ScriptException;

import org.jruby.embed.LocalContextScope;
import org.jruby.embed.LocalVariableBehavior;
import org.jruby.embed.ScriptingContainer;

/**
 * Hello world!
 */
public class App
{
	ScriptingContainer container = new ScriptingContainer(LocalContextScope.THREADSAFE, LocalVariableBehavior.PERSISTENT);
	static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

	public static void main(String[] args) throws ScriptException {
		List<Future> futureList = new ArrayList<Future>();
		for (Integer i = 0; i < 10; i++) {
			final Integer finalI = i;
			futureList.add(cachedThreadPool.submit(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					return new App(finalI.toString());
				}
			}));
		}
		for(Future future : futureList) {
			try {
				future.get();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		cachedThreadPool.shutdown();
	}

	private App(String name) {
		container.put("name", name);
		container.runScriptlet(this.getClass().getResourceAsStream("/test.rb"), "test");
		System.out.println(name + " " + container.get("name"));
	}
}
