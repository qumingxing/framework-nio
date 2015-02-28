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

import java.nio.channels.Selector;
import java.util.concurrent.ExecutorService;

/**
 * 服务端或客户端的通道选择器操作策略接口，该接口为{@link Server},{@link Client}的启动入口，也同时是各种可选项参数输入的入口
 * 
 * @author qmx 2014-12-1 上午11:45:02
 * 
 */
public interface SelectorStrategy
{
	/**
	 * 选择器执行线程池
	 * 
	 * @param selector
	 *            选择器
	 * @return 返回线程池对象当客户端重连时通过该对象终止线程的执行
	 */
	public ExecutorService execute(Selector selector);

	/**
	 * 设置用户自定义的监听对象{@link HandleListener}
	 * 
	 * @param handleListener
	 *            监听对象
	 */
	public void setHandleListen(HandleListener handleListener);

	/**
	 * 设置消息上下文{@link MessageContext}
	 * 
	 * @param context
	 *            消息上下文
	 */
	public void setMessageContext(MessageContext context);

	public MessageContext getMessageContext();

	/**
	 * 设置接收数据的缓冲区通道类型
	 * 
	 * @param bufferType
	 *            {@link ChannelBuffer}的实现类
	 */
	public void setBufferType(Class<? extends ChannelBuffer> bufferType);

	/**
	 * 设置客户端服务端配置文件资源
	 * 
	 * @param config
	 *            客户端服务端配置文件资源ConfigResources
	 */
	public void setConfig(ConfigResources config);

	/**
	 * 根据给定的最大等待认证时间定时检查客户端的有效性，服务端必须要启用认证功能，否则该方法不会生效，
	 * 也就是在一个新的客户端连入时要在要求的时间内完成认证否则认为无效的客户端。
	 * 
	 * @param scheduledCheckValid
	 *            {@link ScheduledCheckValid} 认证的超过等参数配置
	 */
	public void setScheduledCheckValid(ScheduledCheckValid scheduledCheckValid);

	/**
	 * 设置接收数据缓冲区大小(默认2048)，不会小于1024，如果该小于1024会取1024
	 * 
	 * @param capacity
	 *            缓冲区大小
	 */
	public void setReadBufferCapacity(int capacity);

}
