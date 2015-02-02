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
 * 主要对消息格式化、消息编码解码、以及算法的封装
 * 
 * @author qmx 2014-12-3 下午4:10:50
 * 
 */
public class MessageContext
{
	/**
	 * 消息的格式化框架内置了一些实现类,用户也可以自己定制一些类实现{@link MessageFormatToBytes}或
	 * {@link MessageFormatToString}接口
	 * 
	 * <pre>
	 * 	{@link MessageFormatBytesToBytes}
	 *  {@link MessageFormatHybridBytesToBytes}
	 *  {@link MessageFormatHybridStringToString}
	 *  {@link MessageFormatStringToString}
	 * </pre>
	 */
	private MessageFormat messageFormat;
	/**
	 * 编码解码创建工厂，根据用户传入的{@link DataType}类型来决定使用哪种编解码对象， 如果未提供编解码对象会使用默认的
	 * <code>encoderAndDecoderType</code>类型
	 */
	private EncoderAndDecoderFactory encoderAndDecoderFactory;
	/**
	 * {@link Algorithm}用户可选的可自定义实现的算法接口
	 */
	private Algorithm algorithm;
	/**
	 * 默认的{@link MessageContext}对象
	 */
	private static MessageContext defaultMessageContext;

	public MessageFormat getMessageFormat()
	{
		return messageFormat;
	}

	public void setMessageFormat(MessageFormat messageFormat)
	{
		this.messageFormat = messageFormat;
	}

	public EncoderAndDecoderFactory getEncoderAndDecoderFactory()
	{
		return encoderAndDecoderFactory;
	}

	public void setEncoderAndDecoderFactory(
			EncoderAndDecoderFactory encoderAndDecoderFactory)
	{
		this.encoderAndDecoderFactory = encoderAndDecoderFactory;
	}

	public Algorithm getAlgorithm()
	{
		return algorithm;
	}

	public void setAlgorithm(Algorithm algorithm)
	{
		this.algorithm = algorithm;
	}

	public void setDefaultMessageContext(MessageContext defaultContext)
	{
		defaultMessageContext = defaultContext;
	}

	public static MessageContext getDefaultMessageContext()
	{
		if (null != defaultMessageContext)
			return defaultMessageContext;
		return DefaultMessageContext.getDefaultMessageContext();
	}

	static class DefaultMessageContext
	{
		private static final MessageContext messageContext = new MessageContext();

		static
		{
			messageContext.setAlgorithm(new DefaultAlgorithmImpl());
			EncoderAndDecoderFactory encoderAndDecoderFactory = new EncoderAndDecoderFactory();
			encoderAndDecoderFactory
					.setEncoderAndDecoderType(StringEncoderAndDecoderImpl.class);
			messageContext
					.setEncoderAndDecoderFactory(encoderAndDecoderFactory);
			messageContext.setMessageFormat(new MessageFormatStringToString());
		}

		protected static MessageContext getDefaultMessageContext()
		{
			return messageContext;
		}
	}
}
