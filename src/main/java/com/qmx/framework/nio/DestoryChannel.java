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
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通道卸载释放缓冲区和客户端重连核心类
 * 
 * @author qmx 2014-12-19 上午10:36:34
 * 
 */
public class DestoryChannel
{
	/**
	 * 工作线程用于回调客户端设置的{@link HandleListener}实现的<code>close</code>或
	 * <code>exception</code>方法
	 */
	private static WorkTaskThreadPool workTaskThreadPool;
	/**
	 * 客户端或服务端的配置资源
	 */
	private static ConfigResources config;
	/**
	 * 客户端重连次数
	 */
	private static int reconnectCount = 0;
	private static final Logger logger = LoggerFactory
			.getLogger(DestoryChannel.class);
	/**
	 * 当前连接状态标识
	 */
	private static volatile boolean connected = true;
	/**
	 * 客户端正常连接的时候该对象会调用{@link Object}的<code>wait()</code>阻塞等待,连接断开后会
	 * <code>notify()</code>该对象恢复重连工作。
	 */
	private static final Object waitForReconnect = new Object();
	private final static Logger log = LoggerFactory
			.getLogger(DestoryChannel.class);
	static
	{
		reconnect();
	}

	/**
	 * 设置工作线程池负责分发{@link HandleListener}事件
	 * 
	 * @param workTaskThreadPool1
	 *            线程池{@link WorkTaskThreadPool}
	 */
	public static void setWorkTaskThreadPool(
			WorkTaskThreadPool workTaskThreadPool1)
	{
		workTaskThreadPool = workTaskThreadPool1;
	}

	/**
	 * 设置客户端服务端配置文件资源
	 * 
	 * @param config1
	 *            客户端服务端配置文件资源{@link ConfigResources}
	 */
	public static void setConfig(ConfigResources config1)
	{
		config = config1;
	}

	/**
	 * 卸载资源
	 * 
	 * @param channel
	 *            通道
	 * @param ex
	 *            可能发生的外部的异常对象
	 */
	public static void destory(SocketChannel channel, Exception ex)
	{
		boolean flag = channel.isConnected();
		if (flag)
		{
			ChannelBuffer channelBuffer = BufferChannelFactory
					.getBuferChannelFactory().removeBuffer(channel);
			if (null != channelBuffer)
			{
				if (null != ex)
				{
					MethodWorker exMethodWorker = new MethodWorker();
					exMethodWorker.setChannelBuffer(channelBuffer);
					exMethodWorker.setMethodName(HandleEnum.exception);
					exMethodWorker.setException(ex);
					workTaskThreadPool.multiExecute(exMethodWorker);
				}
				MethodWorker closeMethodWorker = new MethodWorker();
				closeMethodWorker.setChannelBuffer(channelBuffer);
				closeMethodWorker.setMethodName(HandleEnum.close);
				workTaskThreadPool.multiExecute(closeMethodWorker);

			}
			Channels.removeChannel(channel);
			try
			{
				channel.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				StringPrintWriter stringWriter = new StringPrintWriter();
				e.printStackTrace(stringWriter);
				log.error("异常->{}\n{}", channelBuffer.getChannelName(),
						stringWriter.getString());
			}
		}
		if (null != config && null != config.getPointModel()
				&& config.getPointModel() == PointModel.CLIENT)
		{
			synchronized (waitForReconnect)
			{
				if (connected)
				{
					connected = false;
					waitForReconnect.notifyAll();
				}
			}
		}
	}

	/**
	 * 客户端重新连接，重连之前要将主选择器关闭，并将线程结束掉，否则线程不释放下次再创建会形成递归调用创建大量线程
	 */
	private static void reconnect()
	{
		new Thread()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				while (true)
				{
					synchronized (waitForReconnect)
					{
						if (connected)
							try
							{
								waitForReconnect.wait();
							} catch (InterruptedException e1)
							{
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						connected = true;
						AbstractConnection connection = config.getConnection();
						try
						{
							connection.getSelector().close();
							connection.getSelectorThread().awaitTermination(
									2000, TimeUnit.MILLISECONDS);
						} catch (Exception e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						if (null != connection)
						{
							logger.debug("重连开始" + reconnectCount++);
							try
							{
								Thread.sleep(2000);
							} catch (InterruptedException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							connection.start();
						}
					}
				}

			}
		}.start();
	}
}
