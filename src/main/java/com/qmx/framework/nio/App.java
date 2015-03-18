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
	/**
	 * 未连接
	 */
	protected static final int UNCONNECT = 0x00;
	/**
	 * 连接中
	 */
	protected static final int CONNECTING = 0x01;
	/**
	 * 已连接
	 */
	protected static final int CONNECTED = 0x02;
	/**
	 * 当前连接状态
	 */
	protected static volatile int CURRENT_CONNECT_STATE = CONNECTED;
	private static final Object waitForReconnect = new Object();
	private static ExecutorService executorService = Executors
			.newFixedThreadPool(3);
	private final static ExecutorService executorService2 = Executors
			.newSingleThreadExecutor();
	static int a = 0;
	private final static AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);
	private static void aa()
	{
		if(ATOMIC_INTEGER.get()!=0)
		{
			ATOMIC_INTEGER.decrementAndGet();
			return;
		}
		ATOMIC_INTEGER.incrementAndGet();
		synchronized (executorService2)
		{
			if (CURRENT_CONNECT_STATE == CONNECTED
					|| CURRENT_CONNECT_STATE == CONNECTING)
			{
				CURRENT_CONNECT_STATE = UNCONNECT;
				waitForReconnect.notifyAll();
			}
		}
	}

	private static void reconnect()
	{
		new Thread()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				while (true)
				{
					synchronized (waitForReconnect)
					{
						if (CURRENT_CONNECT_STATE == CONNECTED
								|| CURRENT_CONNECT_STATE == CONNECTING)
							try
							{
								waitForReconnect.wait();
							} catch (InterruptedException e1)
							{
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						// 连接中
						CURRENT_CONNECT_STATE = CONNECTING;
						a = 1;
						try
						{
							executorService2.awaitTermination(
									2000, TimeUnit.MILLISECONDS);
						} catch (InterruptedException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						App app = new App();
						if (null != app)
						{
							a=0;
							app.eee();
						}
					}
				}

			}
		}.start();
	}

	public static void main(String[] args) throws InterruptedException
	{
		/*App aa = new App();
		aa.eee();
		reconnect();
		Thread.sleep(3000);
		executorService.submit(new Runnable()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				aa();
			}
		});
		Thread.sleep(3000);
		executorService.submit(new Runnable()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				aa();
			}
		});*/
		System.out.println(ATOMIC_INTEGER.get());
		System.out.println(ATOMIC_INTEGER.incrementAndGet());
		System.out.println(ATOMIC_INTEGER.get());
		System.out.println(ATOMIC_INTEGER.decrementAndGet());
		System.out.println(ATOMIC_INTEGER.get());
	}

	private ExecutorService eee()
	{
		executorService2.submit(new Runnable()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				while (a==0)
				{
					try
					{
						Thread.sleep(5000);
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("-------------------");
				}
			}
		});
		return executorService2;
	}
}
