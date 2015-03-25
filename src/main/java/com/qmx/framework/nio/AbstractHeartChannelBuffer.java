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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 心跳业务逻辑处理
 * 
 * @author qmx 2015-3-16 下午2:52:09
 * 
 */
public abstract class AbstractHeartChannelBuffer
{
	// H10
	private final static byte HEART_MSG = 72;// 72, 49, 0
	/**
	 * 客户端服务端模式
	 */
	private PointModel pointModel;
	/**
	 * 心跳机制实现
	 */
	private HeartCheck heartCheck;
	private final static Logger log = LoggerFactory
			.getLogger(AbstractHeartChannelBuffer.class);

	public PointModel getPointModel()
	{
		return pointModel;
	}

	public void setPointModel(PointModel pointModel)
	{
		this.pointModel = pointModel;
	}

	protected int heartExecute(byte oneByte, int avaliableLength,
			String channelName)
	{
		if (oneByte != HEART_MSG)
			return -1;
		else
		{
			if (avaliableLength >= 3)
			{
				if (log.isInfoEnabled())
				{
					log.info("检测到心跳->{}", channelName);
				}
				if (isServerModel())
				{
					flushAcceptTime(channelName);
					HeartMessageAdapter.getInstance()
							.serverResponseClientHeart(channelName, heartCheck);
				}
				return 3;
			} else
				return 0;
		}
	}

	/**
	 * {@link Channels}<code>.flushAcceptTime(channelName);</code>
	 * 
	 * @param channelName
	 *            通道名称
	 */
	private void flushAcceptTime(String channelName)
	{
		Channels.flushAcceptTime(heartCheck, channelName);
	}

	/**
	 * 判断当前是否是服务端模式
	 * 
	 * @return true 服务端模式
	 */
	protected boolean isServerModel()
	{
		if (null != pointModel && pointModel == PointModel.SERVER)
			return true;
		return false;
	}

	/**
	 * 判断当前是否是客户端模式
	 * 
	 * @return true 客户端模式
	 */
	protected boolean isClientModel()
	{
		if (null != pointModel && pointModel == PointModel.CLIENT)
			return true;
		return false;
	}

	/**
	 * 判断是否启用了心跳
	 * 
	 * @return true 启用
	 */
	protected boolean isHeartEnable()
	{
		if (null != heartCheck && heartCheck.isEnableHeart())
			return true;
		return false;
	}

	/**
	 * 获取心跳配置参数
	 * 
	 * @return {@link HeartCheck}
	 */
	public HeartCheck getHeartCheck()
	{
		return heartCheck;
	}

	/**
	 * 设置心跳配置参数
	 * 
	 * @param heartCheck
	 *            {@link HeartCheck}
	 */
	public void setHeartCheck(HeartCheck heartCheck)
	{
		this.heartCheck = heartCheck;
	}
}
