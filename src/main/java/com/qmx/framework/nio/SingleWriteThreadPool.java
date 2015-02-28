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

/**
 * 进行文件发送传输时要保证传输的顺序，所以要使用当前线程<br>
 * SingleWriteThreadPool会与当前channel关联
 * 
 * @author qmx 2014-12-12 下午3:56:27
 * 
 */
public class SingleWriteThreadPool implements ThreadPool
{
	/**
	 * 发送缓冲区大小，可以重复使用的
	 */
	private ByteBuffer byteBuffer = ByteBuffer.allocateDirect(9999999);

	private SingleWriteThreadPool()
	{
		createThreadPool();
	}

	public static SingleWriteThreadPool getInstance()
	{
		return new SingleWriteThreadPool();
	}

	@Override
	public void setPoolSize(int size)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public int getPoolSize()
	{
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public void createThreadPool()
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void multiExecute(final Worker worker)
	{
		// TODO Auto-generated method stub
		WriteWorker writeWorker = (WriteWorker) worker;
		SocketChannel socketChannel = writeWorker.getChannel().getChannel();
		byteBuffer.put(writeWorker.getData());
		byteBuffer.flip();
		try
		{
			while (byteBuffer.hasRemaining())
			{
				socketChannel.write(byteBuffer);
			}
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			DestoryChannel.destory(socketChannel, e);
		} finally
		{
			byteBuffer.clear();
		}
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
	}
}
