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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 心跳信息发送适配器
 * 
 * @author qmx 2015-3-16 下午1:45:54
 * 
 */
public class HeartMessageAdapter

{
	/**
	 * 消息发送对象实例
	 */
	private static volatile Channels CHANNELS = Channels.newChannel(null);
	/**
	 * 定时任务对象
	 */
	private static ScheduledExecutorService SCHEDULED_THREAD_POOL_EXECUTOR;
	/**
	 * 客户端通道对象
	 */
	private volatile Channel channel;
	/**
	 * 唯一的实例
	 */
	private final static HeartMessageAdapter HEART_MESSAGE_ADAPTER = new HeartMessageAdapter();
	private volatile static boolean started;

	private HeartMessageAdapter()
	{
		// 固定的消息格式化对象
		CHANNELS.setMessageFormat(new MessageFormatHeartStringToString());
		// 获取缓存容器中的通道对象，对于客户端来说通常只有一个
		channel = CHANNELS.getFirstChannel();
		CHANNELS.setChannel(channel);
	}

	/**
	 * 获取心跳适配器实例
	 * 
	 * @return {@link HeartMessageAdapter}
	 */
	protected static HeartMessageAdapter getInstance()
	{
		return HEART_MESSAGE_ADAPTER;
	}

	/**
	 * 执行客户端心跳发送<br/>
	 * 客户端重连后会导致多次调用该方法所以通过started状态标识，判断当前通道是否与之前的通道是一个引用，如果是直接结束方法执行，
	 * 否则重新设置新的通道对象
	 * 
	 * @param heartCheck
	 *            {@link HeartCheck}
	 */
	protected void clientExecuteHeart(final HeartCheck heartCheck)
	{
		if (null == SCHEDULED_THREAD_POOL_EXECUTOR)
		{
			SCHEDULED_THREAD_POOL_EXECUTOR = Executors
					.newScheduledThreadPool(1);
		}
		if (started)
		{
			Channel newChannel = CHANNELS.getFirstChannel();
			if (null != newChannel && newChannel == channel)
			{
				return;
			} else
			{
				channel = newChannel;
				CHANNELS.setChannel(newChannel);
				return;
			}
		}
		started = true;
		SCHEDULED_THREAD_POOL_EXECUTOR.scheduleWithFixedDelay(new Runnable()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				CHANNELS.broadcastSingle(channel.getChannelName(),
						heartCheck.getHeartMessage());
			}
		}, heartCheck.getDelayTime(), heartCheck.getDelayTime(),
				TimeUnit.MILLISECONDS);
	}

	/**
	 * 服务端收到客户端的心跳信息后响应客户端
	 * 
	 * @param channelName
	 *            通道名称
	 * @param heartCheck
	 *            心跳资源
	 */
	protected void serverResponseClientHeart(String channelName,
			HeartCheck heartCheck)
	{
		CHANNELS.broadcastSingle(channelName, heartCheck.getHeartMessage());
	}
}
