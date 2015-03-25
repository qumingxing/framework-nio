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
 * 混合数据类型(String,byte)的{@link ChannelBuffer}
 * 缓冲区实现，也就是客户端可以发送二进制也可以发字符串。TCP会存在拆包和粘包的情况，所以需要对数据的完整性做特殊处理。
 * 
 * <pre>
 * 示例2$Bab3$Sabc
 * B表示字节类型
 * S表示字符串类型
 * $表示分割符，读取数据的时候会忽略掉该字符
 * </pre>
 * 
 * 该缓冲区能够完成从数据的接收、认证、处理、分发，在整 个数据接收链中处于十分重要的位置
 * 
 * @author qmx 2014-12-10 上午11:45:36
 * 
 */
public class HybridLengthSplitChannelBuffer extends AbstractChannelBuffer
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
	private int gcArraysDataPostionSize = defaultArraysLength / 100 * 80;;
	/**
	 * 数据长度默认为最大10位数字1000000000(10位)
	 */
	private int unitLength = 10;
	private int unit;
	/**
	 * 默认初始化数组大小
	 */
	private static int defaultArraysLength = 5000;
	/**
	 * 存放有效数据长度的字节数组
	 */
	private byte[] by = new byte[unitLength];
	/**
	 * 分发池
	 */
	private ThreadPool threadPool;
	/**
	 * 完整的读取一段数据后会创建该对象
	 */
	private MethodWorker methodWorker;
	private static final Logger logger = LoggerFactory
			.getLogger(HybridLengthSplitChannelBuffer.class);

	public void setThreadPool(ThreadPool threadPool)
	{
		this.threadPool = threadPool;
	}

	public HybridLengthSplitChannelBuffer(int size)
	{
		arraysData = new byte[size];
	}

	public HybridLengthSplitChannelBuffer()
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
		unit = 0;
		byte dataType = 0x00;
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
			if (oneByte == 36)
			{
				if (i + 1 < arraysDataAvaliableLength)
				{
					dataType = arraysData[++i];
				}
				int le = Integer.parseInt(new String(Arrays.copyOfRange(by, 0,
						unit)));
				unit = 0;
				byte[] dataArr = copyFromArraysData(i + 1, le);
				// 长度不够取
				if (dataArr == null)
				{
					break;
				}
				arraysDataPostion = (i + 1 + le);
				i = arraysDataPostion - 1;
				methodWorker = new MethodWorker();
				methodWorker.setChannelBuffer(this);
				methodWorker.setMethodName(HandleEnum.read);
				methodWorker.setByt(dataArr);
				if (dataType == 83)// S
				{
					methodWorker.setDataType(DataType.STRING);
				} else if (dataType == 66)// B
				{
					// 传文件要同步否则多线程会导致顺序错乱
					methodWorker.setDataType(DataType.BYTE);
				}
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
				if (methodWorker.getDataType() == DataType.STRING)
				{
					threadPool.multiExecute(methodWorker);
				} else
				{
					threadPool.singleExecute(methodWorker);
				}
				// String sss = new String(dataArr);
				// System.out.println(sss);
				gcArrays();
			} else
			{
				by[unit++] = oneByte;
			}
		}
	}

	public static void main(String[] args) throws UnsupportedEncodingException
	{
		final HybridLengthSplitChannelBuffer buffer = new HybridLengthSplitChannelBuffer();
		for (int i = 0; i < 5; i++)// 7861
		{
			if (i != 4)
			{
				String str = "48fas1f2as1f2as1df21sad2f1a2s1df2a1s2f1as21f2as1fd"
						+ i;
				int a1 = str.getBytes().length;
				String a = a1 + "$B" + str;// S83 B66
				byte b1[] = a.getBytes();
				buffer.setBytes(b1);
			} else
			{
				String str = "48fas1f2as1f2as1df21sad2f1a2s1df2a1s2f1as21f2as1fd"
						+ i;
				int a1 = str.getBytes().length + 1;
				String a = a1 + "$B" + str;// S83 B66
				byte b1[] = a.getBytes();
				buffer.setBytes(b1);
			}

		}
		try
		{
			Thread.sleep(5000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte b1[] = new byte[]
		{ 97 };
		buffer.setBytes(b1);

		for (int i = 10; i < 15; i++)// 7861
		{

			String str = "48fas1f2as1f2as1df21sad2f1a2s1df2a1s2f1as21f2as1fd"
					+ i;
			int a1 = str.getBytes().length;
			String a = a1 + "$S" + str;// S83 B66
			byte b2[] = a.getBytes();
			buffer.setBytes(b2);

		}
	}

	@Override
	public void clearBytes()
	{
		// TODO Auto-generated method stub
		arraysData = null;
	}

}
