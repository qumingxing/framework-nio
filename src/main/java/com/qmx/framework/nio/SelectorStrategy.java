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
	public ExecutorService execute(Selector selector);

	public void setHandleListen(HandleListener handleListener);

	public void setMessageContext(MessageContext context);

	public MessageContext getMessageContext();

	public void setBufferType(Class<? extends ChannelBuffer> bufferType);

	public void setConfig(ConfigResources config);

	public void setScheduledCheckValid(ScheduledCheckValid scheduledCheckValid);

}
