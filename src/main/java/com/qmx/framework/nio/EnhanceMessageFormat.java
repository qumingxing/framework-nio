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
 * 复杂的消息传递类型父接口{@link }
 * 
 * @author qmx 2015-2-27 上午11:15:20
 * 
 */
public interface EnhanceMessageFormat
{
	/**
	 * 获取消息传递类型
	 * 
	 * @return {@link TransferType}
	 */
	public TransferType getTransferType();

	/**
	 * 获取同步消息的传输方向(请求、响应)，只有<code>TransferType.SYNCHRONIZED</code>才有意义.
	 * 
	 * @return {@link TransferDirection}
	 */
	public TransferDirection getTransferDirection();

	/**
	 * 设置同步消息的传输方向(请求、响应)，只有<code>TransferType.SYNCHRONIZED</code>才有意义.
	 * 
	 * @param redirection
	 *            {@link TransferDirection}
	 */
	public void setTransferDirection(TransferDirection redirection);

	/**
	 * 根据传递的类型包装二进制
	 * 
	 * @param transferType
	 *            传递类型{@link TransferType}
	 * @param bytes
	 *            字节数组
	 * @return 返回处理后的字节数组
	 */
	public byte[] format(TransferType transferType, byte[] bytes);

	/**
	 * 设置消息的传递类型
	 * 
	 * @param transferType
	 *            {@link TransferType}
	 */
	public void setTransferType(TransferType transferType);

	/**
	 * 设置响应同步消息时要将请求过来的消息编号回发回去，否则找不到对应的响应消息
	 * 
	 * @param messageNumber
	 *            请求的消息编号
	 */
	public void setMessageNumber(String messageNumber);
}
