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
 * 基于字节编解码实现，设置相当算法和原始消息
 * 
 * @author qmx 2014-11-28 上午11:19:13
 * 
 */
public class ByteEncoderAndDecoderImpl implements ByteEncoderAndDecoder
{
	/**
	 * {@link Algorithm}用户可选的可自定义实现的算法接口
	 */
	private Algorithm algorithm;
	/**
	 * 原始消息内容
	 */
	private byte[] message;
	/**
	 * 待解码的消息内容
	 */
	private byte[] encodeByt;

	@Override
	public void encode()
	{
		// TODO Auto-generated method stub
		encodeByt = algorithm.getEncodeResult(message);
	}

	@Override
	public void decode()
	{
		// TODO Auto-generated method stub
		message = algorithm.getDecodeResult(encodeByt);
	}

	@Override
	public byte[] getMessage()
	{
		// TODO Auto-generated method stub
		return message;
	}

	@Override
	public void setMessage(Object t)
	{
		// TODO Auto-generated method stub
		this.message = (byte[]) t;
	}

	public Algorithm getAlgorithm()
	{
		return algorithm;
	}

	public void setAlgorithm(Algorithm algorithm)
	{
		this.algorithm = algorithm;
	}

	@Override
	public byte[] getEncodeByt()
	{
		// TODO Auto-generated method stub
		return encodeByt;
	}

	@Override
	public void setEncodeByt(byte[] encodeByt)
	{
		// TODO Auto-generated method stub
		this.encodeByt = encodeByt;
	}

}
