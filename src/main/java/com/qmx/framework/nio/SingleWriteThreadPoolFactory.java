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

import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * 创建写数据的单线程工厂，每个通道只会有一个，由于二进制在发送的过程中必须要保证发送的顺序正常，否则接收方将不法将二进制组装成文件。
 * 
 * @author qmx 2014-12-12 下午4:02:59
 * 
 */
public class SingleWriteThreadPoolFactory
{
	/**
	 * 存的所有单线程的集合，键使用的是
	 * <code>socketChannel.socket().getRemoteSocketAddress()</code>,值为
	 * {@link SingleWriteThreadPool}
	 */
	private Map<String, SingleWriteThreadPool> singlePools = new HashMap<String, SingleWriteThreadPool>();
	/**
	 * 单例的线程池工厂
	 */
	private final static SingleWriteThreadPoolFactory SINGLE_WRITE_THREAD_POOL_FACTORY = new SingleWriteThreadPoolFactory();

	private SingleWriteThreadPoolFactory()
	{

	}

	public static SingleWriteThreadPoolFactory getSingleWriteThreadPoolFactory()
	{
		return SINGLE_WRITE_THREAD_POOL_FACTORY;
	}

	public SingleWriteThreadPool getSingleWriteThread(
			SocketChannel socketChannel)
	{
		String clientSign = socketChannel.socket().getRemoteSocketAddress()
				.toString();
		SingleWriteThreadPool singleWriteThreadPool = singlePools
				.get(clientSign);
		if (null == singleWriteThreadPool)
		{
			SingleWriteThreadPool newSingleThread = SingleWriteThreadPool
					.getInstance();
			singlePools.put(clientSign, newSingleThread);
			return newSingleThread;
		}
		return singleWriteThreadPool;
	}
}
