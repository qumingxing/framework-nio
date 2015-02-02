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
 * 基于字符串的编码、解码、以及相关消息的算法核心接口实现
 * 
 * @author qmx 2014-12-1 下午3:25:17
 * 
 */
public class StringEncoderAndDecoderImpl implements StringEncoderAndDecoder
{
	/**
	 * 用户指定或默认的算法接口
	 */
	private Algorithm algorithm;
	/**
	 * 原始字符串消息
	 */
	private String message;
	/**
	 * 待解码的字符串字节数组
	 */
	private byte[] encodeByt;

	@Override
	public void encode()
	{
		// TODO Auto-generated method stub
		encodeByt = algorithm.getEncodeResult(message.getBytes());
	}

	@Override
	public void decode()
	{
		// TODO Auto-generated method stub
		message = new String(algorithm.getDecodeResult(encodeByt));
	}

	@Override
	public String getMessage()
	{
		// TODO Auto-generated method stub
		return message;
	}

	@Override
	public void setMessage(Object t)
	{
		// TODO Auto-generated method stub
		this.message = t.toString();
	}

	public Algorithm getAlgorithm()
	{
		return algorithm;
	}

	public void setAlgorithm(Algorithm algorithm)
	{
		this.algorithm = algorithm;
	}

	public byte[] getEncodeByt()
	{
		return encodeByt;
	}

	public void setEncodeByt(byte[] encodeByt)
	{
		this.encodeByt = encodeByt;
	}

}
