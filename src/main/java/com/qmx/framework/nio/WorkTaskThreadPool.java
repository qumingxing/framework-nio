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

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 该类主要负责向{@link HandleListener}分发业务请求的线程池实现
 * 
 * @author qmx 2014-11-28 下午3:49:08
 * 
 */
public class WorkTaskThreadPool implements ThreadPool
{
	/**
	 * 线程池
	 */
	private ExecutorService executorService = null;
	/**
	 * 唯一的线程池对象
	 */
	private final static WorkTaskThreadPool READ_THREAD_POOL = new WorkTaskThreadPool();
	/**
	 * 线程池大小
	 */
	private int size;

	private WorkTaskThreadPool()
	{

	}

	public static WorkTaskThreadPool getInstance()
	{
		return READ_THREAD_POOL;
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
		executorService.execute(new Runnable()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				executeTask(worker);
			}
		});

	}

	@Override
	public void singleExecute(final Worker worker)
	{
		// TODO Auto-generated method stub
		Future<?> future = executorService.submit(new Runnable()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				executeTask(worker);
			}
		});
		try
		{
			future.get();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void executeTask(Worker worker)
	{
		MethodWorker methodWorker = (MethodWorker) worker;
		MessageEvent event = methodWorker.newMessageEvent();
		ChannelBuffer channelBuffer = methodWorker.getChannelBuffer();
		HandleEnum name = methodWorker.getMethodName();
		SocketChannel socketChannel = channelBuffer.getSocketChannel();
		Selector selector = channelBuffer.getSelector();
		HandleListener handleListener = channelBuffer.getHandleListener();
		try
		{
			if (name == HandleEnum.accept)
			{
				handleListener.accept(event);
				selector.wakeup();
				socketChannel.register(selector, SelectionKey.OP_READ);

			} else if (name == HandleEnum.connect)
			{
				handleListener.conneced(event);
				channelBuffer.setWriteReady(true);
				selector.wakeup();
				socketChannel.register(selector, SelectionKey.OP_WRITE);
			} else if (name == HandleEnum.write)
			{
				if (channelBuffer.isWriteReady())
				{
					channelBuffer.setWriteReady(false);
					handleListener.write(event);
					selector.wakeup();
					socketChannel.register(selector, SelectionKey.OP_READ);
				}
			} else if (name == HandleEnum.read)
			{
				// 只有connect和accept可以write方法
				// event.setEncodeByt(methodWorker.getByt());
				// event.getEncoderAndDecoder().decode();
				handleListener.read(event);
				channelBuffer.setWriteReady(true);
				// selector.wakeup();
				// socketChannel.register(selector,
				// SelectionKey.OP_WRITE);
			} else if (name == HandleEnum.close)
			{
				handleListener.close(event);
			} else if (name == HandleEnum.exception)
			{
				handleListener.exception(event, methodWorker.getException());
			}
		} catch (Exception e)
		{
			handleListener.exception(event, e);
		}
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
