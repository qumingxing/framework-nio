package com.qmx.framework.nio;

import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 *
 */
public class App
{
	private static ScheduledExecutorService SCHEDULED_EXECUTOR_HEART_SERVICE = Executors
			.newSingleThreadScheduledExecutor();
	public static void main(String[] args)
	{
		SCHEDULED_EXECUTOR_HEART_SERVICE.scheduleWithFixedDelay(
				new Runnable()
				{

					@Override
					public void run()
					{
						// TODO Auto-generated method stub
						System.out.println(new Date().toLocaleString());
					}
				}, 3000, 3000,
				TimeUnit.MILLISECONDS);
	}
}
