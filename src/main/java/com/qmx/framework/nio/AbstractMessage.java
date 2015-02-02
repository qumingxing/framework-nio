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
 * 消息顶层抽象，该类主要用于设置消息的编码解码对象，设置原始待发送的消息，以及设置待解码的字节数组
 * 
 * @author qmx 2014-11-27 下午1:43:13
 * 
 */
public abstract class AbstractMessage
{

	/**
	 * 
	 * 消息的编码解码对象
	 */
	private EncoderAndDecoder encoderAndDecoder;

	/**
	 * 获取消息的编码解码对象
	 * 
	 * @return 编码解码对象
	 */
	public EncoderAndDecoder getEncoderAndDecoder()
	{
		return encoderAndDecoder;
	}

	/**
	 * 设置消息的编码解码对象
	 * 
	 * @param encoderAndDecoder
	 *            编码解码对象
	 * 
	 */
	public void setEncoderAndDecoder(EncoderAndDecoder encoderAndDecoder)
	{
		this.encoderAndDecoder = encoderAndDecoder;
	}

	/**
	 * 如果是发送消息，此处是待发送消息的原始数据
	 * 
	 * @param message
	 *            待发送消息的原始数据
	 */
	protected void setMessage(Object message)
	{
		encoderAndDecoder.setMessage(message);
	}

	/**
	 * 获取消息的原始数据
	 * 
	 * @return <code>String</code>|<code>byte[]</code>
	 */
	public Object getMessage()
	{
		return encoderAndDecoder.getMessage();
	}

	/**
	 * 设置接收消息时待解码的二进制数据
	 * 
	 * @param byt
	 *            待解码的二进制数据
	 */
	protected void setEncodeByt(byte[] byt)
	{
		encoderAndDecoder.setEncodeByt(byt);
	}
}
