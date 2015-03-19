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
 * 心跳状态监听器用户可以自定义实现
 * 
 * @author qmx 2015-3-19 下午1:40:37
 * 
 */
public interface HeartStateListener
{
	/**
	 * 检测到心跳
	 * 
	 * @param channel
	 *            通道
	 */
	public void checkHeart(Channel channel);

	/**
	 * 未在要求的时候内收到心跳信息连接是否要断开取决于{@link HeartCheck}
	 * 中的expireInvaildRemoveChannelEnable设置
	 * 
	 * @param channel
	 *            通道
	 */
	public void uncheckHeart(Channel channel);
}
