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
 * {@link EncoderAndDecoder}编码解码创建工厂，根据用户传入的{@link DataType}类型来决定使用哪种编解码对象，
 * 如果未提供编解码对象会使用默认的<code>encoderAndDecoderType</code>类型
 * 
 * @author qmx 2014-12-3 下午4:14:38
 * 
 */
public class EncoderAndDecoderFactory
{
	/**
	 * 默认的编解码类型
	 */
	private Class<? extends EncoderAndDecoder> encoderAndDecoderType;

	public void setEncoderAndDecoderType(
			Class<? extends EncoderAndDecoder> encoderAndDecoderType)
	{
		this.encoderAndDecoderType = encoderAndDecoderType;
	}

	public EncoderAndDecoder getInstance(DataType dataType)
	{
		if (dataType == DataType.STRING)
		{
			return new StringEncoderAndDecoderImpl();
		} else if (dataType == DataType.BYTE)
		{
			return new ByteEncoderAndDecoderImpl();
		} else
		{
			if (encoderAndDecoderType == StringEncoderAndDecoderImpl.class)
			{
				return new StringEncoderAndDecoderImpl();
			} else if (encoderAndDecoderType == ByteEncoderAndDecoderImpl.class)
			{
				return new ByteEncoderAndDecoderImpl();
			}
		}
		return null;
	}

	public static StringEncoderAndDecoder getStringEncoderAndDecoder()
	{
		return new StringEncoderAndDecoderImpl();
	}

	public static ByteEncoderAndDecoder getByteEncoderAndDecoder()
	{
		return new ByteEncoderAndDecoderImpl();
	}
}
