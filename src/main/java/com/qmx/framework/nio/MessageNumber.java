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

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 生成消息编号
 * 
 * @author qmx 2015-2-26 上午10:58:03
 * 
 */
public class MessageNumber
{
	private final static long MAX_VALUE = 999999999;
	private final static long INIT_VALUE = 100000000;
	private final static AtomicLong MESSAGE_NUMBER = new AtomicLong(INIT_VALUE);
	private final static ReentrantLock lock = new ReentrantLock();
	/**
	 * 将生成的消息编号存储在本地线程中，同步等待获取返回值的时候需要通过从本地线程中取出消息号去匹配
	 */
	private final static ThreadLocal<String> messageNumberStore = new ThreadLocal<String>();

	/**
	 * 获取一个唯一的消息编号
	 * 
	 * @return 9位数消息编号
	 */
	public static String getMessageNumber()
	{
		long nextValue = 0l;
		String number = null;
		try
		{
			lock.lock();
			nextValue = MESSAGE_NUMBER.getAndIncrement();
			if (nextValue == MAX_VALUE)
			{
				MESSAGE_NUMBER.lazySet(INIT_VALUE);
				nextValue = MESSAGE_NUMBER.getAndIncrement();
			}
		} finally
		{
			lock.unlock();
		}
		number = String.valueOf(nextValue);
		messageNumberStore.set(number);
		return number;
	}

	/**
	 * 获取在本地存储的当前同步消息的消息编号
	 * 
	 * @return 消息编号
	 */
	public static String getStoreMessageNumber()
	{
		return messageNumberStore.get();
	}
}
