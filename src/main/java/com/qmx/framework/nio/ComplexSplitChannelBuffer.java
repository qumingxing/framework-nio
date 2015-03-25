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

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 支持同步和异步以及混合数据类型(String,byte)的{@link ChannelBuffer}
 * 缓冲区实现，也就是客户端可以发送二进制也可以发字符串。TCP会存在拆包和粘包的情况，所以需要对数据的完整性做特殊处理。
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
 * 该缓冲区能够完成从数据的接收、认证、处理、分发，在整 个数据接收链中处于十分重要的位置
 * 
 * @author qmx 2014-12-10 上午11:45:36
 * 
 */
public class ComplexSplitChannelBuffer extends AbstractChannelBuffer
{
	/**
	 * 实际操作数据
	 */
	private byte[] arraysData;
	/**
	 * <code>arraysData</code>中可用元素数量
	 */
	private int arraysDataAvaliableLength;
	/**
	 * <code>arraysData</code>当前读取的位置
	 */
	private int arraysDataPostion;
	/**
	 * 300000=300KB<br/>
	 * 回收的算法<code>arraysDataPostion>gcArraysDataPostionSize</code>时才回收，提高回收利用率
	 */
	private int gcArraysDataPostionSize = defaultArraysLength / 100 * 80;
	/**
	 * 默认初始化数组大小
	 */
	private static int defaultArraysLength = 5000;
	/**
	 * 分发池
	 */
	private ThreadPool threadPool;
	/**
	 * 完整的读取一段数据后会创建该对象
	 */
	// private MethodWorker methodWorker;
	private final static int SYNCHRONIZED = 83;// S
	private final static int ASYNCHRONY = 65;// A
	private final static int STRING = 83;// S
	private final static int BYTE = 66;// B
	private final static int REQUEST = 82;// R
	private final static int RESPONSE = 69;// E
	private static final Logger logger = LoggerFactory
			.getLogger(ComplexSplitChannelBuffer.class);

	public void setThreadPool(ThreadPool threadPool)
	{
		this.threadPool = threadPool;
	}

	public ComplexSplitChannelBuffer(int size)
	{
		arraysData = new byte[size];
	}

	public ComplexSplitChannelBuffer()
	{
		this(defaultArraysLength);
	}

	/**
	 * 数据转移
	 * 
	 * @param bytes
	 */
	public void setBytes(byte[] bytes)
	{

		checkArraysDataCapcity(bytes.length);
		System.arraycopy(bytes, 0, arraysData, arraysDataAvaliableLength,
				bytes.length);
		arraysDataAvaliableLength += bytes.length;
		try
		{
			readWork();
		} catch (Exception e)
		{
			logger.info("数据读取错误" + e.getMessage());
			DestoryChannel.destory(super.getChannel(), e);
		}

	}

	/**
	 * 检查arraysData容量是否能满足
	 * 
	 * @param arrLegnth
	 */
	private void checkArraysDataCapcity(int arrLegnth)
	{
		int oldCapacity = arraysData.length;
		if (arrLegnth + arraysDataAvaliableLength > oldCapacity)
		{
			int newCapacity = ((arrLegnth + arraysDataAvaliableLength) * 3) / 2 + 1;
			if (newCapacity < arrLegnth)
				newCapacity = arrLegnth;
			arraysData = Arrays.copyOf(arraysData, newCapacity);
		}
	}

	/**
	 * 从arraysData中提取数据
	 * 
	 * @param offset
	 * @param size
	 * @return
	 */
	public byte[] copyFromArraysData(int offset, int size)
	{
		byte[] byt = null;
		// 最新可用元素数量
		int avaliableNum = arraysDataAvaliableLength - offset;// arraysDataPostion;
		// 本次取的数量1,2,3,4,5 2 2
		int getNum = size;// + offset;
		// 是否有足够的数据可取
		if (avaliableNum >= getNum)
		{
			byt = Arrays.copyOfRange(arraysData, offset, size + offset);
			// arraysDataPostion = offset + size;
		} else
		{
			logger.debug("不够..等待" + avaliableNum + "," + getNum);
		}
		return byt;
	}

	public void gcArrays()
	{

		if (arraysDataPostion > gcArraysDataPostionSize)
		{

			System.arraycopy(arraysData, arraysDataPostion, arraysData, 0,
					arraysDataAvaliableLength - arraysDataPostion);
			for (int i = arraysDataAvaliableLength - arraysDataPostion; i < arraysDataAvaliableLength; i++)
			{
				arraysData[i] = 0x00;
			}
			arraysDataAvaliableLength -= arraysDataPostion;
			arraysDataPostion = 0;
			logger.debug("GC" + arraysDataPostion + ","
					+ arraysDataAvaliableLength);
		}
	}

	/**
	 * 执行最后的数据切分工作
	 */
	public void readWork() throws CertificateAuthException
	{
		byte dataType = 0x00;
		MethodWorker methodWorker = null;
		int messageLengthScale = MessageLengthConvert.getFormatDefaultLength();
		for (int i = arraysDataPostion; i < arraysDataAvaliableLength; i++)
		{
			byte oneByte = arraysData[i];
			if (super.isHeartEnable())
			{
				int i_index_position = super.heartExecute(oneByte,
						arraysDataAvaliableLength - arraysDataPostion,
						super.getChannelName());
				if (i_index_position == 3)
				{
					arraysDataPostion = i + i_index_position;
					i = arraysDataPostion - 1;
					continue;
				} else if (i_index_position == 0)
					break;
			}
			// SR100000001S000101234567890
			if (oneByte == SYNCHRONIZED)
			{
				if ((arraysDataAvaliableLength - arraysDataPostion) >= 17)
				{
					int transferDirection = copyFromArraysData(i + 1, 1)[0];
					int le = 9;
					byte[] messageNumber = copyFromArraysData(i + 2, le);
					dataType = copyFromArraysData(i + 2 + le, 1)[0];
					byte[] dataLength = copyFromArraysData(i + le + 3,
							messageLengthScale);
					int dlength = Integer.parseInt(new String(dataLength));
					byte[] dataArr = copyFromArraysData(i + le + 3
							+ messageLengthScale, dlength);
					// 长度不够取
					if (dataArr == null)
					{
						break;
					}
					arraysDataPostion = (i + le + 3 + messageLengthScale + dlength);
					i = arraysDataPostion - 1;
					if (transferDirection == REQUEST)
					{
						methodWorker = createMessageWork(dataArr, new String(
								messageNumber), TransferType.SYNCHRONIZED,
								dataType);
					} else if (transferDirection == RESPONSE)
						SynchronizedThreadPool.getInstance().setResponse(
								new String(messageNumber), dataArr);
				}
			} else if (oneByte == ASYNCHRONY)// AS000101234567890
			{

				dataType = copyFromArraysData(i + 1, 1)[0];
				byte[] dataLength = copyFromArraysData(i + 2,
						messageLengthScale);
				int dlength = Integer.parseInt(new String(dataLength));
				byte[] dataArr = copyFromArraysData(i + 2 + messageLengthScale,
						dlength);
				// 长度不够取
				if (dataArr == null)
				{
					break;
				}
				arraysDataPostion = (i + 2 + messageLengthScale + dlength);
				i = arraysDataPostion - 1;
				methodWorker = createMessageWork(dataArr, null,
						TransferType.ASYNCHRONY, dataType);
			} else
				throw new IllegalStateException("The first byte was invalid.");
			if (super.isCertificateAuth())
			{
				boolean authRes = super.certificateAuth(methodWorker);
				if (!authRes)
				{
					throw new CertificateAuthException("身份认证失败"
							+ super.getChannelName());
				}
				if (isAuthMark())
					continue;
			}
			if (null != methodWorker)
			{
				if (methodWorker.getDataType() == DataType.STRING)
				{
					threadPool.multiExecute(methodWorker);
				} else
				{
					threadPool.singleExecute(methodWorker);
				}
			}
			gcArrays();
		}
	}

	/**
	 * 创建{@link MethodWorker}
	 * 
	 * @param dataArr
	 *            消息体数据
	 * @param messageNumber
	 *            同步消息编号
	 * @param transferType
	 *            传递类型
	 * @param dataType
	 *            数据类型
	 * @return {@link MethodWorker}
	 */
	private MethodWorker createMessageWork(byte[] dataArr,
			String messageNumber, TransferType transferType, byte dataType)
	{
		MethodWorker methodWorker = new MethodWorker();
		methodWorker.setChannelBuffer(this);
		methodWorker.setMethodName(HandleEnum.read);
		methodWorker.setByt(dataArr);
		methodWorker.setMessageNumber(messageNumber);
		methodWorker.setTransferType(transferType);
		if (dataType == STRING)// S
		{
			methodWorker.setDataType(DataType.STRING);
		} else if (dataType == BYTE)// B
		{
			// 传文件要同步否则多线程会导致顺序错乱
			methodWorker.setDataType(DataType.BYTE);
		}
		return methodWorker;
	}

	public static void main(String[] args) throws UnsupportedEncodingException
	{
		// S100000001S000101234567890<br/>
		// AS000101234567890<br/>
		// SB000101234567890<br/>
		final ComplexSplitChannelBuffer buffer = new ComplexSplitChannelBuffer();
		for (int i = 0; i < 10000; i++)
		{
			buffer.setBytes("SR100000001S00010123456789".getBytes());
			buffer.setBytes(new byte[]
			{ 48 });

		}
		/*
		 * for (int i = 0; i < 5; i++)// 7861 { if (i != 4) { String str =
		 * "48fas1f2as1f2as1df21sad2f1a2s1df2a1s2f1as21f2as1fd" + i; int a1 =
		 * str.getBytes().length; String a = a1 + "$B" + str;// S83 B66 byte
		 * b1[] = a.getBytes(); buffer.setBytes(b1); } else { String str =
		 * "48fas1f2as1f2as1df21sad2f1a2s1df2a1s2f1as21f2as1fd" + i; int a1 =
		 * str.getBytes().length + 1; String a = a1 + "$B" + str;// S83 B66 byte
		 * b1[] = a.getBytes(); buffer.setBytes(b1); }
		 * 
		 * } try { Thread.sleep(5000); } catch (InterruptedException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); } byte b1[] =
		 * new byte[] { 97 }; buffer.setBytes(b1);
		 * 
		 * for (int i = 10; i < 15; i++)// 7861 {
		 * 
		 * String str = "48fas1f2as1f2as1df21sad2f1a2s1df2a1s2f1as21f2as1fd" +
		 * i; int a1 = str.getBytes().length; String a = a1 + "$S" + str;// S83
		 * B66 byte b2[] = a.getBytes(); buffer.setBytes(b2);
		 * 
		 * }
		 */
	}

	@Override
	public void clearBytes()
	{
		// TODO Auto-generated method stub
		arraysData = null;
		SynchronizedThreadPool.getInstance().clearAllSyncResponsePool();
	}
}
