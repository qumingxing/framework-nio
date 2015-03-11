/*
 * Copyright [2014-2015] [qumx of copyright owner]
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qmx.framework.nio;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 同步阻塞式消息发送处理线程池
 * 
 * @author qmx 2015-2-27 下午2:09:49
 * 
 */
public class SynchronizedThreadPool implements ThreadPool
{

	/**
	 * 线程池
	 */
	private ExecutorService executorService = null;
	/**
	 * 唯一的线程池对象
	 */
	private final static SynchronizedThreadPool SYNCHRONIZED_THREAD_POOL = new SynchronizedThreadPool();
	/**
	 * 线程池大小
	 */
	private int size;
	/**
	 * 存储同步响应数据 </br> 以弱键 实现的基于哈希表的 Map。在 WeakHashMap
	 * 中，当某个键不再正常使用时，将自动移除其条目。更精确地说，对于一个给定的键
	 * ，其映射的存在并不阻止垃圾回收器对该键的丢弃，这就使该键成为可终止的，被终止
	 * ，然后被回收。丢弃某个键时，其条目从映射中有效地移除，因此，该类的行为与其他的 Map 实现有所不同。
	 */
	private volatile Map<String, Object> responsePool = Collections
			.synchronizedMap(new WeakHashMap<String, Object>());
	/**
	 * 默认的同步请求超时时间
	 */
	private long defaultSynchronizedTimeout = 3000L;
	/**
	 * 每轮等待步长
	 */
	private long waitThreadStop = defaultSynchronizedTimeout / 10;
	private final static Logger log = LoggerFactory
			.getLogger(SynchronizedThreadPool.class);

	/**
	 * 设置同步响应数据
	 * 
	 * @param messageNumber
	 *            消息编号
	 * @param message
	 *            响应内容
	 */
	protected void setResponse(String messageNumber, Object message)
	{
		responsePool.put(messageNumber, message);
	}

	private SynchronizedThreadPool()
	{

	}

	public static SynchronizedThreadPool getInstance()
	{
		return SYNCHRONIZED_THREAD_POOL;
	}

	@Override
	public void setPoolSize(int size)
	{
		// TODO Auto-generated method stub
		this.size = size;
	}

	@Override
	public int getPoolSize()
	{
		// TODO Auto-generated method stub
		return this.size;
	}

	@Override
	public void createThreadPool()
	{
		// TODO Auto-generated method stub
		executorService = Executors.newFixedThreadPool(getPoolSize());
	}

	@Override
	public void multiExecute(final Worker worker)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void singleExecute(final Worker worker)
	{
		// TODO Auto-generated method stub

	}

	protected Object getResult(final String messageNumber)
	{
		Future<?> future = executorService.submit(new Callable<Object>()
		{

			@Override
			public Object call() throws Exception
			{
				// TODO Auto-generated method stub
				Object result = null;
				int i = 0;
				while ((null == (result = responsePool.remove(messageNumber))))
				{
					i++;
					waitThread();
					if (i % 11 == 0)
						break;
				}
				return result;
			}

		});
		Object result = null;
		try
		{
			result = future.get(defaultSynchronizedTimeout,
					TimeUnit.MILLISECONDS);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			StringPrintWriter printWriter = new StringPrintWriter();
			e.printStackTrace(printWriter);
			log.error(printWriter.getString());
		} catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			StringPrintWriter printWriter = new StringPrintWriter();
			e.printStackTrace(printWriter);
			log.error(printWriter.getString());
		} catch (TimeoutException e)
		{
			// TODO Auto-generated catch block
			log.warn("Synch request timeout->{}", messageNumber);
		}
		return result;
	}

	private void waitThread()
	{
		try
		{
			Thread.sleep(waitThreadStop);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 设置默认的同步请求超时时间
	 * 
	 * @param timeout
	 *            毫秒
	 */
	public void setDefaultSynchronizedTimeout(long timeout)
	{
		this.defaultSynchronizedTimeout = timeout;
		this.waitThreadStop = this.defaultSynchronizedTimeout / 10;
	}

	/**
	 * 清空存储同步响应数据
	 */
	public void clearAllSyncResponsePool()
	{
		responsePool.clear();
	}

	@Override
	public void shutdownThreadPool()
	{
		// TODO Auto-generated method stub
		if (null != executorService && !executorService.isShutdown())
		{
			executorService.shutdown();
		}
	}
}
