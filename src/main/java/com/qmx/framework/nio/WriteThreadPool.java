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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 多线程发送
 * 
 * @author qmx 2014-11-28 下午3:49:08
 * 
 */
public class WriteThreadPool implements ThreadPool
{
	/**
	 * 写线程池
	 */
	private ExecutorService executorService = null;
	/**
	 * 唯一的写线程池对象
	 */
	private final static WriteThreadPool WRITE_THREAD_POOL = new WriteThreadPool();
	/**
	 * 发送缓冲区池
	 */
	private SendChannelBuffersPool sendChannelBuffersPool = new SendChannelBuffersPool();
	/**
	 * 线程池大小
	 */
	private int size;

	private WriteThreadPool()
	{

	}

	public static WriteThreadPool getInstance()
	{
		return WRITE_THREAD_POOL;
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
				WriteWorker writeWorker = (WriteWorker) worker;
				byte[] byt = writeWorker.getData();
				ByteBuffer byteBuffer = sendChannelBuffersPool
						.getByteBuffer(byt.length);
				byteBuffer.put(byt);
				byteBuffer.flip();
				SocketChannel socketChannel = writeWorker.getChannel()
						.getChannel();
				try
				{
					while (byteBuffer.hasRemaining())
					{
						socketChannel.write(byteBuffer);
					}
				} catch (IOException e)
				{
					DestoryChannel.destory(writeWorker.getChannel(), e);
				} finally
				{
					sendChannelBuffersPool.realse(byteBuffer);
				}
			}
		});
	}

	@Override
	public void singleExecute(Worker worker)
	{
		// TODO Auto-generated method stub

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
