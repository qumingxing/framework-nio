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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 维护所有通道对象，群发消息单点发消息，定时检查客户端的合法性
 * 
 * @author qmx 2014-12-1 上午10:38:41
 * 
 */
public class Channels extends MessageAdapter
{
	/**
	 * 维护所有{@link Channel} 通道的<code>Map</code>集合 该集合的键由
	 * <code>socketChannel.socket().getRemoteSocketAddress()<code>连接端远程地址及端口号构成
	 */
	private static Map<String, Channel> channels = new ConcurrentHashMap<String, Channel>();
	/**
	 * 如果用户设置了{@link ScheduledCheckValid}对象会触发该定时任务，定时任务会根据设置的轮询参数检查客户端的合法性，
	 * 对超过最大时间仍未认证的客户端会强制断开基连接
	 */
	private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors
			.newSingleThreadScheduledExecutor();

	/**
	 * {@link Channels}发送的消息不依赖于特定的通道，而是可以向所有通道或指定的通道发送消息，它已经脱离了
	 * {@link HandleListener}事件的控制， 所有需要定制{@link MessageContext}对象。
	 * 
	 * @param messageContext
	 *            主要对消息格式化、消息编码解码、以及算法的封装
	 */
	private Channels(MessageContext messageContext)
	{
		if (null == messageContext)
		{
			messageContext = MessageContext.getDefaultMessageContext();
		}
		EncoderAndDecoder encoderAndDecoder = messageContext
				.getEncoderAndDecoderFactory().getInstance(null);
		encoderAndDecoder.setAlgorithm(messageContext.getAlgorithm());
		this.setEncoderAndDecoder(encoderAndDecoder);
		this.setMessageFormat(messageContext.getMessageFormat());
	}

	/**
	 * 获取所有通道的键名称集合，键名称由
	 * <code>socketChannel.socket().getRemoteSocketAddress()<code>连接端远程地址及端口号构成
	 * 
	 * @return 所有通道的键列表
	 */
	public static List<String> getClients()
	{
		return new ArrayList<String>(channels.keySet());
	}

	/**
	 * 用户可以随时切换新的{@link MessageContext}对象，以完成其它方式对消息的算法处理，如果为空将取默认的
	 * <code>MessageContext.getDefaultMessageContext()</code>对象。
	 * 
	 * @param messageContext
	 *            新的MessageContext对象
	 */
	public void changeMessageContext(MessageContext messageContext)
	{
		if (null == messageContext)
		{
			messageContext = MessageContext.getDefaultMessageContext();
		}
		EncoderAndDecoder encoderAndDecoder = messageContext
				.getEncoderAndDecoderFactory().getInstance(null);
		encoderAndDecoder.setAlgorithm(messageContext.getAlgorithm());
		this.setEncoderAndDecoder(encoderAndDecoder);
		this.setMessageFormat(messageContext.getMessageFormat());
	}

	/**
	 * 静态方法创建一个新的{@link Channels}对象，只有创建后的{@link Channels}对象才能完成发送消息获取客户端的操作。
	 * 
	 * @param messageContext
	 *            如果为空将取默认的
	 *            <code>MessageContext.getDefaultMessageContext()</code>对象。
	 * @return 新的{@link Channels}对象
	 */
	public static Channels newChannel(MessageContext messageContext)
	{
		Channels newChannel = new Channels(messageContext);
		return newChannel;
	}

	/**
	 * 静态方法增加一个客户端，或服务端
	 * 
	 * @param alias
	 *            别名由
	 *            <code>socketChannel.socket().getRemoteSocketAddress()<code>连接端远程地址及端口号构成
	 * @param channel
	 *            新的{@link Channel}对象
	 */
	public static void addChannel(String alias, Channel channel)
	{
		channels.put(alias, channel);
	}

	/**
	 * 静态方法移除一个客户端，或服务端，当客户端或服务端断开后双方都会移除各自维护的通道对象。如果认证不通过的对象也会移除。
	 * 
	 * @param socketChannel
	 *            通道对象
	 */
	public static void removeChannel(SocketChannel socketChannel)
	{
		String clientSign = socketChannel.socket().getRemoteSocketAddress()
				.toString();
		channels.remove(clientSign);
	}

	/**
	 * 设置对应通道的认证结果，如果认证成功后需要调用此方法设置该通道的认证状态。否则当发送消息的时候，如果通道没有认证通过，会断开该通道的连接。
	 * 
	 * @param socketChannel
	 *            通道
	 * @param authResult
	 *            <code>true</code>认证通过
	 */
	public static void channelAuthResult(SocketChannel socketChannel,
			boolean authResult)
	{
		String clientSign = socketChannel.socket().getRemoteSocketAddress()
				.toString();
		Channel channel = channels.get(clientSign);
		if (null != channel)
		{
			channel.setCertificateAuthed(authResult);
		}
	}

	/**
	 * 根据给定的最大等待认证时间定时检查客户端的有效性，服务端必须要启用认证功能，否则该方法不会生效，
	 * 也就是在一个新的客户端连入时要在要求的时间内完成认证否则认为无效的客户端。
	 * 
	 * @param scheduledCheckValid
	 *            认证的超过等参数配置
	 */
	protected static void startScheduledCheck(
			final ScheduledCheckValid scheduledCheckValid)
	{
		SCHEDULED_EXECUTOR_SERVICE.scheduleWithFixedDelay(new Runnable()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				Iterator<Entry<String, Channel>> ite = channels.entrySet()
						.iterator();
				Date acceptDate = null;
				Date calculateFutureTime = null;
				while (ite.hasNext())
				{
					Entry<String, Channel> entry = ite.next();
					Channel channel = entry.getValue();
					if (!channel.isCertificateAuth())
					{
						SCHEDULED_EXECUTOR_SERVICE.shutdown();
						return;
					}
					if (!channel.isCertificateAuthed())
					{
						acceptDate = channel.getAcceptDate();
						calculateFutureTime = new Date();
						calculateFutureTime.setTime(acceptDate.getTime()
								+ scheduledCheckValid
										.getMaxAcceptWaitCertificateTime());
						if (calculateFutureTime.getTime() < System
								.currentTimeMillis())
						{
							DestoryChannel.destory(
									entry.getValue().getChannel(),
									new CertificateAuthException(
											"到达最大等待身份认证许可时间["
													+ acceptDate.toString()
													+ "]["
													+ calculateFutureTime
															.toString()
													+ "]，身份认证失败!"
													+ entry.getKey()));
						}
					}

				}
			}
		}, scheduledCheckValid.getInitialDelay(), scheduledCheckValid
				.getScheduledDelay(), TimeUnit.MILLISECONDS);
	}

	/**
	 * 群消息发送，如果启用的认证功能，只有通过认证的客户端才能收到消息。
	 * 
	 * @param message
	 *            待发送的消息内容
	 */
	public void broadcast(Object message)
	{

		Iterator<Entry<String, Channel>> ite = channels.entrySet().iterator();
		while (ite.hasNext())
		{
			Entry<String, Channel> entry = ite.next();
			if (auth(entry.getValue()))
			{
				super.setChannel(entry.getValue());
				super.write(message);
			} /*
			 * else DestoryChannel .destory( entry.getValue().getChannel(), new
			 * CertificateAuthException("身份认证失败!" + entry.getKey()));
			 */
		}
	}

	/**
	 * 群消息发送，如果启用的认证功能，只有通过认证的客户端才能收到消息。
	 * 
	 * @param client
	 *            发送目标
	 *            <code>socketChannel.socket().getRemoteSocketAddress()<code>连接端远程地址及端口号构成，可以通过<code>getClients</code>
	 *            方法获得
	 * @param message
	 *            待发送的消息内容
	 */
	public void broadcastSingle(String client, Object message)
	{
		Channel channel = channels.get(client);
		if (null != channel)
		{
			if (auth(channel))
			{
				super.setChannel(channel);
				super.write(message);
			}/*
			 * else DestoryChannel.destory(channel.getChannel(), new
			 * CertificateAuthException("身份认证失败!" + client));
			 */
		}
	}

	/**
	 * 判断是否需要认证，如果需要认证检查认证结果是否正确
	 * 
	 * @param channel
	 * @return
	 */
	private boolean auth(Channel channel)
	{
		if (channel.isCertificateAuth())
		{
			if (channel.isCertificateAuthed())
			{
				return true;
			}
			return false;
		}
		return true;
	}
}
