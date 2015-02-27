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

import java.util.Arrays;

/**
 * 增强方式将字节格式化标识(B)后返回，该类实现了{@link EnhanceMessageFormatToBytes}
 * 
 * <pre>
 * 示例
 * SR100000001S000101234567890<br/>
 * SE100000001S000101234567890<br/>
 * AS000101234567890<br/>
 * SRB000101234567890<br/>
 * SEB000101234567890<br/>
 * 同步传递类型S同步、(R请求E响应)、后跟随9位唯一消息编号、(S字符串B字节)、5位长度不足补0
 * 异步传递类型A异步、(S字符串B字节)、5位长度不足补0
 * </pre>
 * 
 * @author qmx 2014-12-12 上午10:52:41
 * 
 */
public class MessageFormatEnhanceBytesToBytes implements
		EnhanceMessageFormatToBytes
{
	private final static String SYNCHRONIZED = "S";
	private final static String ASYNCHRONY = "A";
	private final static String REQUEST = "R";
	private final static String RESPONSE = "E";
	/**
	 * 设置消息的传递类型默认ASYNCHRONY传递
	 */
	private TransferType transferType;
	/**
	 * 同步消息的传输方向
	 */
	private TransferDirection direction;

	/**
	 * 创建一个默认的异步传递方式的消息格式化对象
	 * 
	 */
	public MessageFormatEnhanceBytesToBytes()
	{
		this.transferType = TransferType.ASYNCHRONY;
	}

	/**
	 * 响应同步消息时要将请求过来的消息编号回发回去，否则找不到对应的响应消息
	 */
	private String messageNumber;

	/**
	 * 创建一个指定传递方式的消息格式化对象
	 * 
	 * @param transferType
	 *            {@link TransferType}
	 */
	public MessageFormatEnhanceBytesToBytes(TransferType transferType)
	{
		this.transferType = transferType;
	}

	/**
	 * 创建一个指定传递方式和同步消息的传递方向的消息格式化对象
	 * 
	 * @param transferType
	 *            {@link TransferType}
	 * @param redirection
	 *            {@link TransferDirection}
	 */
	public MessageFormatEnhanceBytesToBytes(TransferType transferType,
			TransferDirection direction)
	{
		this(transferType);
		this.direction = direction;
	}

	@Override
	public void setTransferType(TransferType transferType)
	{
		this.transferType = transferType;
	}

	@Override
	public byte[] format(TransferType transferType, byte[] bytes)
	{
		// TODO Auto-generated method stub
		byte[] sendBytes = bytes;
		byte[] lengthArr = null;
		if (transferType == TransferType.SYNCHRONIZED)
		{
			if (null == direction)
				throw new IllegalStateException(
						"synchronized message must set TransferDirection object.");
			if (direction == TransferDirection.REQUEST)
			{
				lengthArr = (String.valueOf(SYNCHRONIZED
						+ REQUEST
						+ MessageNumber.getMessageNumber()
						+ "B"
						+ MessageLengthConvert
								.getLengthString(sendBytes.length))).getBytes();
			} else
			{
				if (null == messageNumber)
					throw new IllegalStateException(
							"synchronized response need requested message number please set messageNumber value.");
				lengthArr = (String.valueOf(SYNCHRONIZED
						+ RESPONSE
						+ messageNumber
						+ "B"
						+ MessageLengthConvert
								.getLengthString(sendBytes.length))).getBytes();
			}

		} else if (transferType == TransferType.ASYNCHRONY)
		{
			lengthArr = (String.valueOf(ASYNCHRONY + "B"
					+ MessageLengthConvert.getLengthString(sendBytes.length)))
					.getBytes();
		}

		sendBytes = Arrays.copyOf(sendBytes, sendBytes.length
				+ lengthArr.length);
		System.arraycopy(sendBytes, 0, sendBytes, lengthArr.length,
				sendBytes.length - lengthArr.length);
		System.arraycopy(lengthArr, 0, sendBytes, 0, lengthArr.length);
		return sendBytes;
	}

	@Override
	public byte[] format(byte[] bytes)
	{
		return format(transferType, bytes);
	}

	@Override
	public TransferType getTransferType()
	{
		// TODO Auto-generated method stub
		return transferType;
	}

	@Override
	public TransferDirection getTransferDirection()
	{
		// TODO Auto-generated method stub
		return direction;
	}

	@Override
	public void setTransferDirection(TransferDirection direction)
	{
		// TODO Auto-generated method stub
		this.direction = direction;
	}

	@Override
	public void setMessageNumber(String messageNumber)
	{
		// TODO Auto-generated method stub
		this.messageNumber = messageNumber;
	}
}
