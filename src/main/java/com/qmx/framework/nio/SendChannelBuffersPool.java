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
import java.util.concurrent.locks.ReentrantLock;

/**
 * 发送消息时会通过该类生成一个与发送消息大小等同的的缓冲区。数据处理过后需要调用<code>realse<code>释放回缓冲区池中。
 * 
 * @author qmx 2014-12-5 上午11:33:47
 * 
 */
public class SendChannelBuffersPool
{
	/**
	 * 默认的缓冲池数量大小
	 */
	private int poolSize = 8;
	/**
	 * 缓冲区池{@link SoftReference}当堆即将达到溢出时JVM会释放废弃的引用
	 */
	@SuppressWarnings("unchecked")
	private SoftReference<ByteBufferNode>[] buffersPool = new SoftReference[poolSize];
	private static final ReentrantLock lock = new ReentrantLock();

	/**
	 * 创建一个新的或返回一个已有的适当大小的字节缓冲区，缓冲区大小最小不会小于1024
	 * 
	 * @param capacity
	 *            缓冲区大小
	 * @return 返回一个缓冲区
	 */
	public ByteBuffer getByteBuffer(int capacity)
	{
		ByteBufferNode bufferNode = null;
		int bufferCapacity = normalizeCapacity(capacity);
		try
		{
			lock.lock();
			for (int i = 0; i < buffersPool.length; i++)
			{
				SoftReference<ByteBufferNode> reference = buffersPool[i];
				if (null != reference)
				{
					bufferNode = reference.get();
					if (bufferNode.isRelease())
					{
						if (bufferNode.getCapacity() >= bufferCapacity)
						{
							bufferNode.setRelease(false);
							bufferNode.getByteBuffer().clear();
							break;
						} else
						{
							buffersPool[i] = null;
							bufferNode = new ByteBufferNode(bufferCapacity);
							buffersPool[i] = new SoftReference<ByteBufferNode>(
									bufferNode);
							break;
						}
					}
				} else
				{
					bufferNode = new ByteBufferNode(bufferCapacity);
					buffersPool[i] = new SoftReference<ByteBufferNode>(
							bufferNode);
					break;
				}
				if (i == buffersPool.length - 1)
				{
					i = -1;
				}
			}

		} finally
		{
			lock.unlock();
		}
		return bufferNode.getByteBuffer();
	}

	/**
	 * 释放一个缓冲区
	 * 
	 * @param byteBuffer
	 *            缓冲区对象
	 */
	public void realse(ByteBuffer byteBuffer)
	{
		ByteBufferNode bufferNode = null;
		for (int i = 0; i < buffersPool.length; i++)
		{
			SoftReference<ByteBufferNode> reference = buffersPool[i];
			if (null != reference)
			{
				bufferNode = reference.get();
				if (bufferNode.getByteBuffer() == byteBuffer)
				{
					bufferNode.setRelease(true);
					break;
				}
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

	private static class ByteBufferNode
	{
		private boolean release;
		private ByteBuffer byteBuffer;
		private int capacity;

		public ByteBufferNode(int capacity)
		{
			this.capacity = capacity;
			byteBuffer = ByteBuffer.allocateDirect(this.capacity);
		}

		public boolean isRelease()
		{
			return release;
		}

		public void setRelease(boolean release)
		{
			this.release = release;
		}

		public ByteBuffer getByteBuffer()
		{
			return byteBuffer;
		}

		public int getCapacity()
		{
			return capacity;
		}
	}
}
