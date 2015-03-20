package com.qmx.framework.nio;

import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Hello world!
 * 
 */
public class App
{
	
	public static void main(String[] args) throws InterruptedException
	{
		int i = 0;
		do
		{
			System.out.println(i);
			i++;
			//Thread.sleep(3000);
		} while (i < 50);
	}

}
