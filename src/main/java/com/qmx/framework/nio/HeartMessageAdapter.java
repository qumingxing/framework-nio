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
	private static final Channels CHANNELS = Channels.newChannel(null);
	/**
	 * 定时任务对象
	 */
	private static final ScheduledExecutorService SCHEDULED_THREAD_POOL_EXECUTOR = Executors
			.newScheduledThreadPool(1);
	/**
	 * 客户端通道对象
	 */
	private final Channel channel;
	/**
	 * 唯一的实例
	 */
	private final static HeartMessageAdapter HEART_MESSAGE_ADAPTER = new HeartMessageAdapter();

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
	 * 执行心跳发送
	 * 
	 * @param heartCheck
	 *            {@link HeartCheck}
	 */
	protected void executeHeart(final HeartCheck heartCheck)
	{
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
}
