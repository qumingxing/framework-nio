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

/**
 * 启用心跳机制维护客户端的有效性<br/>
 * 客户端服务端双向使用<br/>
 * 1、作为客户端表示客户端是否要启用发送心跳信息，发送信息的间隔时间 <br/>
 * 2、作为服务端表示是否启动检测心跳机制，到达指定的间隔时间(delayTime+(delayTime/100*30))未收到心跳信息对该通道的处理(
 * 是否要关闭该通道) 3、如果开启了服务端最大等待时间认证功能。那么心跳的间隔时间一定要大于{@link ScheduledCheckValid}
 * <code>maxAcceptWaitCertificateTime</code>
 * 最大等待时间.否则在未认证成功之前发送心跳会导致服务端认为客户端非法而断开连接。
 * 
 * @author qmx 2015-3-16 下午1:19:22
 * 
 */
public class HeartCheck
{
	/**
	 * 是否启用心跳
	 */
	private boolean enableHeart;
	/**
	 * 心跳间隔时间
	 */
	private long delayTime;
	/**
	 * 到达间隔时间未收到心跳是否要移除通道
	 */
	private boolean expireInvaildRemoveChannelEnable;
	/**
	 * 自定义心跳消息内容默认0x00(暂时不能自定义内容)
	 */
	private String heartMessage = defaultHeartMessage;
	/**
	 * 默认的消息内容
	 */
	private final static String defaultHeartMessage = "0";
	/**
	 * 心跳数据长度{@link MessageFormatHeartStringToString}
	 */
	private int hearMessageLength;// H10
	private final static String HEART = "H";
	/**
	 * 心跳监听器
	 */
	private HeartStateListener heartStateListener;

	/**
	 * 
	 * @param enableHeart
	 *            客户端(是否启用发送心跳信息)、服务端(是否启用检测心跳机制)
	 * @param delayTime客户端
	 *            (发送心跳信息的间隔时间)、服务端(是否在规定的时候内收到了心跳信息)
	 * 
	 */
	public HeartCheck(boolean enableHeart, long delayTime)
	{
		if (delayTime < 3000)
		{
			throw new IllegalStateException(
					"The delayTime value must gt 3000 ms.");
		}
		this.enableHeart = enableHeart;
		this.delayTime = delayTime;
	}

	/**
	 * 客户端(是否启用发送心跳信息)、服务端(是否启用检测心跳机制)
	 * 
	 * @return true 启用
	 */
	public boolean isEnableHeart()
	{
		return enableHeart;
	}

	/**
	 * 客户端 (发送心跳信息的间隔时间)、服务端(是否在规定的时候内收到了心跳信息)
	 * 
	 * @return 间隔时间
	 */
	public long getDelayTime()
	{
		return delayTime;
	}

	/**
	 * 是否启用了服务端 (未在delayTime规定的时间内收到消息是否关系该通道)
	 * 
	 * @return true 启用
	 */
	public boolean isExpireInvaildRemoveChannelEnable()
	{
		return expireInvaildRemoveChannelEnable;
	}

	/**
	 * 获取自定义心跳消息内容默认发送0x00
	 */
	public String getHeartMessage()
	{
		return heartMessage;
	}

	/**
	 * expireInvaildRemoveChannelEnable服务端 (未在delayTime规定的时间内收到消息是否关闭该通道)
	 * 
	 * @param expireInvaildRemoveChannelEnable
	 */
	public void setExpireInvaildRemoveChannelEnable(
			boolean expireInvaildRemoveChannelEnable)
	{
		this.expireInvaildRemoveChannelEnable = expireInvaildRemoveChannelEnable;
	}

	/**
	 * 心跳消息长度{@link MessageFormatHeartStringToString}
	 * 
	 * @return 消息长度
	 */
	public int getHearMessageLength()
	{
		if (hearMessageLength == 0)
		{
			byte[] lengthArr = (String.valueOf(HEART
					+ getHeartMessage().getBytes().length)).getBytes();
			hearMessageLength = lengthArr.length;
		}
		return hearMessageLength;
	}

	/**
	 * 获取心跳监听器
	 * 
	 * @return {@link HeartStateListener}
	 */
	public HeartStateListener getHeartStateListener()
	{
		return heartStateListener;
	}

	/**
	 * 设置心跳监听器
	 * 
	 * @param heartStateListener
	 *            {@link HeartStateListener}
	 */
	public void setHeartStateListener(HeartStateListener heartStateListener)
	{
		this.heartStateListener = heartStateListener;
	}

}
