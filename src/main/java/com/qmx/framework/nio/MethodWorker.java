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
 * 当请求连接或服务端接受一个请求后或当缓冲区{@link ChannelBuffer}中完整的接收到一条数据后会创建一个
 * {@link MethodWorker}对象，该对象的一个主要作用是设置当前的操作行为和 创建{@link MessageEvent}并使用
 * {@link EncoderAndDecoder}解码。创建后的{@link MessageEvent} 会通过分发单/多线程池调用
 * {@link HandleListener} 中的一系列方法入参。
 * 
 * @author qmx 2014-12-11 下午4:45:32
 * 
 */

public class MethodWorker implements Worker
{
	/**
	 * 操作行为
	 */
	private HandleEnum methodName;
	/**
	 * 如果是从缓冲区接收的数据时，该数组存放未经解码的数据
	 */
	private byte[] byt;
	/**
	 * 如果{@link HandleEnum}为<code>exception</code>，那么这里存储异常对象
	 */
	private Exception exception;
	/**
	 * 存储当前缓冲区对象
	 */
	private ChannelBuffer channelBuffer;
	/**
	 * 存储数据类型，根据数据的类型来决定使用何种{@link EncoderAndDecoder}来进行解码
	 */
	private DataType dataType;

	public ChannelBuffer getChannelBuffer()
	{
		return channelBuffer;
	}

	public void setChannelBuffer(ChannelBuffer channelBuffer)
	{
		this.channelBuffer = channelBuffer;
	}

	public Exception getException()
	{
		return exception;
	}

	public void setException(Exception exception)
	{
		this.exception = exception;
	}

	public HandleEnum getMethodName()
	{
		return methodName;
	}

	public void setMethodName(HandleEnum methodName)
	{
		this.methodName = methodName;
	}

	public byte[] getByt()
	{
		return byt;
	}

	public void setByt(byte[] byt)
	{
		this.byt = byt;
	}

	public MessageEvent newMessageEvent()
	{
		// TODO Auto-generated method stub
		MessageEvent event = new MessageEvent();
		Channel channel = new ChannelImpl();
		channel.setChannel(channelBuffer.getSocketChannel());
		channel.setChannelName(channelBuffer.getChannelName());
		event.setChannel(channel);
		EncoderAndDecoder encoderAndDecoder = channelBuffer.getMessageContext()
				.getEncoderAndDecoderFactory().getInstance(dataType);
		encoderAndDecoder.setAlgorithm(channelBuffer.getMessageContext()
				.getAlgorithm());
		event.setEncoderAndDecoder(encoderAndDecoder);
		event.setMessageFormat(channelBuffer.getMessageContext()
				.getMessageFormat());
		event.setDataType(dataType);
		if (null != byt)
		{
			event.setEncodeByt(byt);
			event.getEncoderAndDecoder().decode();
		}
		return event;
	}

	public DataType getDataType()
	{
		return dataType;
	}

	public void setDataType(DataType dataType)
	{
		this.dataType = dataType;
	}

}
