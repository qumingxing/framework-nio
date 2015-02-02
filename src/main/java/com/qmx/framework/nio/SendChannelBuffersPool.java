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
	private SoftReference<ByteBuffer>[] buffers = new SoftReference[poolSize];

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
