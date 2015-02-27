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
 * {@link MessageEvent}中包含了接收到的数据<code>getMessage()</code>
 * 、对应的通道对象、通道名称、向该通道中写的方法。当前通道中接受数据的类型，包括用户 可以自定义实现的 {@link MessageExecutor}
 * 接口注册方法，然后设置<code>messageType</code>后，框架会自动调度 {@link MessageExecutor}接口中实现的方法。
 * 
 * @author qmx 2014-12-1 上午10:21:15
 * 
 */
public class MessageEvent extends MessageAdapter
{
	/**
	 * 业务消息类型
	 */
	private String messageType;
	/**
	 * 数据类型:字符串、二进制
	 */
	private DataType dataType;
	/**
	 * 消息传递方式{@link TransferType}
	 */
	private TransferType transferType;
	/**
	 * 同步传递消息编号
	 */
	private String messageNumber;

	public String getMessageType()
	{
		return messageType;
	}

	public void setMessageType(String messageType)
	{
		this.messageType = messageType;
	}

	public void setChannel(Channel channel)
	{
		super.setChannel(channel);
	}

	public void executMessageExecutor()
	{
		MessageExecutor messageExecutor = MessageExecutor
				.getExecutor(messageType);
		if (null != messageExecutor)
			messageExecutor.executor(this);
	}

	public void write(Object message)
	{
		super.write(message);
	}

	public DataType getDataType()
	{
		return dataType;
	}

	public void setDataType(DataType dataType)
	{
		this.dataType = dataType;
	}

	/**
	 * 获取消息传递类型{@link TransferType}
	 * 
	 * @return {@link TransferType}
	 */
	public TransferType getTransferType()
	{
		return transferType;
	}

	public void setTransferType(TransferType transferType)
	{
		this.transferType = transferType;
	}

	/**
	 * 获取同步传递消息编号
	 * 
	 * @return 同步消息编号
	 */
	public String getMessageNumber()
	{
		return messageNumber;
	}

	public void setMessageNumber(String messageNumber)
	{
		this.messageNumber = messageNumber;
	}

}
