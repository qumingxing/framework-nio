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

import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;

/**
 * 接收消息时会通过该类生成一个适当大小的缓冲区，以存放接收到的数据。数据处理过后需要调用<code>realse<code>释放回缓冲区池中。
 * 
 * @author qmx 2014-12-5 上午11:33:47
 * 
 */
public class ChannelBuffersPool
{
	/**
	 * 默认的缓冲池数量大小
	 */
	private int poolSize = 8;
	/**
	 * 缓冲区池{@link SoftReference}当堆即将达到溢出时JVM会释放废弃的引用
	 */
	@SuppressWarnings("unchecked")
	private SoftReference<ByteBuffer>[] buffers = new SoftReference[poolSize];

	/**
	 * 创建一个新的或返回一个已有的适当大小的字节缓冲区，缓冲区大小最小不会小于1024
	 * 
	 * @param capacity
	 *            缓冲区大小
	 * @return 返回一个缓冲区
	 */
	public ByteBuffer getByteBuffer(int capacity)
	{
		int bufferCapacity = normalizeCapacity(capacity);
		for (int i = 0; i < buffers.length; i++)
		{
			SoftReference<ByteBuffer> reference = buffers[i];
			if (null == reference || null == reference.get())
			{
				continue;
			} else
			{
				ByteBuffer byteBuffer = reference.get();
				if (byteBuffer.capacity() >= capacity)
				{
					buffers[i] = null;
					byteBuffer.clear();
					return byteBuffer;
				}
			}
		}
		return ByteBuffer.allocateDirect(bufferCapacity);
	}

	/**
	 * 释放一个缓冲区
	 * 
	 * @param byteBuffer
	 *            缓冲区对象
	 */
	public void realse(ByteBuffer byteBuffer)
	{
		for (int i = 0; i < buffers.length; i++)
		{
			SoftReference<ByteBuffer> reference = buffers[i];
			if (null == reference || null == reference.get())
			{
				buffers[i] = new SoftReference<ByteBuffer>(byteBuffer);
				return;
			}
		}
	}

	/**
	 * 根据设置创建一个不小于1024大小的容量
	 * 
	 * @param capacity
	 *            缓冲区大小
	 * @return 计算后的缓冲区大小
	 */
	private static final int normalizeCapacity(int capacity)
	{
		int q = capacity >>> 10;
		int r = capacity & 1023;
		if (r != 0)
		{
			q++;
		}
		return q << 10;
	}
}
