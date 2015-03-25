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

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * 向操作系统的TCP发送缓冲区复制数据(write的动作并不是直接将数据发送给对方而是通过将数据复制到操作系统的TCP发送缓冲区中，
 * 同操作系统决定何时发送出去)，
 * 注意事项当操作系统的缓冲区已满时底层write方法在非阻塞的情况下会直接返回0导致while循环大量重复执行CPU100%，
 * 目前的办法是采用临时的选择器注册WRITE方法
 * ，临时选择器注册后调用select()方法会阻塞直到有可用的缓冲区，设置超时是防止时间过长等待，允许超时两次，每次等3秒。
 * 
 * @author qmx 2015-3-25 上午9:26:43
 * 
 */
public class Writer
{
	/**
	 * 向缓冲区复制数据
	 * 
	 * @param channel
	 *            {@link Channel}
	 * @param byteBuffer
	 *            数据内容
	 * @throws IOException
	 *             异常处理
	 */
	protected void write0(Channel channel, ByteBuffer byteBuffer)
			throws IOException
	{
		int attempts = 0;
		SelectionKey key = null;
		Selector writeSelector = null;
		SocketChannel socketChannel = channel.getChannel();
		try
		{
			while (byteBuffer.hasRemaining())
			{
				int len = socketChannel.write(byteBuffer);
				attempts++;
				if (len < 0)
				{
					throw new EOFException();
				}
				if (len == 0)
				{
					if (writeSelector == null)
					{
						writeSelector = SelectorFactory.getSelector();
						if (writeSelector == null)
						{
							continue;
						}
					}
					key = socketChannel.register(writeSelector,
							SelectionKey.OP_WRITE);
					if (writeSelector.select(3000) == 0)
					{
						if (attempts > 2)
						{
							throw new IOException("未知网络问题导致的数据写入失败->"
									+ channel.getChannelName());
						}

					} else
					{
						attempts--;
					}
				} else
				{
					attempts = 0;
				}
			}
		} catch (IOException e)
		{
			throw e;
		} finally
		{
			if (key != null)
			{
				key.cancel();
				key = null;
			}
			if (writeSelector != null)
			{
				// Cancel the key.
				try
				{
					writeSelector.selectNow();
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				SelectorFactory.returnSelector(writeSelector);
			}
		}
	}
}
