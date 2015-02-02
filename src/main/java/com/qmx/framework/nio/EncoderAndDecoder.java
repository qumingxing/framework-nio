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
 * 编码、解码、以及相关消息的算法核心接口
 * 
 * @author qmx 2014-11-28 上午11:03:23
 * 
 */
public interface EncoderAndDecoder
{
	/**
	 * 对消息进行编码
	 */
	public void encode();

	/**
	 * 获取待解码信息
	 * 
	 * @return 待解码信息
	 */
	public byte[] getEncodeByt();

	/**
	 * 设置待解码信息
	 * 
	 * @param encodeByt
	 *            待解码信息
	 */
	public void setEncodeByt(byte[] encodeByt);

	/**
	 * 解码
	 */
	public void decode();

	/**
	 * 获取原始的消息内容
	 * 
	 * @return 消息内容
	 */
	public Object getMessage();

	/**
	 * 设置原始消息内容
	 * 
	 * @param t
	 *            消息内容
	 */
	public void setMessage(Object t);

	/**
	 * 设置自定义实现的算法接口
	 * 
	 * @param algorithm
	 *            自定义实现的算法接口类
	 */
	public void setAlgorithm(Algorithm algorithm);
}
