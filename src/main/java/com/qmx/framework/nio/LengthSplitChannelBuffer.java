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
 * 单一数据类型(String或byte)的{@link ChannelBuffer}
 * 缓冲区实现，也就是客户端只可以发送一种数据结构的消息。TCP会存在拆包和粘包的情况，所以需要对数据的完整性做特殊处理。
 * 
 * <pre>
 * 示例2$ab3$abc
 * $表示分割符，读取数据的时候会忽略掉该字符
 * </pre>
 * 
 * 该缓冲区能够完成从数据的接收、认证、处理、分发，在整 个数据接收链中处于十分重要的位置
 * 
 * 
 * @author qmx 2014-12-10 上午11:45:36
 * 
 */
public class LengthSplitChannelBuffer extends AbstractChannelBuffer
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
	 * 数据长度默认为最大10位数字1000000000(10位)
	 */
	private int unitLength = 10;
	/**
	 * 存放有效数据长度的字节数组
	 */
	private byte[] by = new byte[unitLength];
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
	private MethodWorker methodWorker;
	private static final Logger logger = LoggerFactory
			.getLogger(LengthSplitChannelBuffer.class);

	public void setThreadPool(ThreadPool threadPool)
	{
		this.threadPool = threadPool;
	}

	public LengthSplitChannelBuffer(int size)
	{
		arraysData = new byte[size];
	}

	public LengthSplitChannelBuffer()
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
			DestoryChannel.destory(super.getSocketChannel(), e);
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
		int unit = 0;
		for (int i = arraysDataPostion; i < arraysDataAvaliableLength; i++)
		{
			byte oneByte = arraysData[i];
			if (super.isHeartEnable() && super.isServerModel())
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
				methodWorker.setDataType(DataType.DEFAULT);
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
				MessageFormat messageFormat = super.getMessageContext()
						.getMessageFormat();
				if (messageFormat instanceof MessageFormatToString)
					threadPool.multiExecute(methodWorker);
				else if (messageFormat instanceof MessageFormatToBytes)
				{
					// 传文件要同步否则多线程会导致顺序错乱
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
		final LengthSplitChannelBuffer buffer = new LengthSplitChannelBuffer();
		/*
		 * byte[] bbb = new byte[] { 49 }; buffer.setBytes(bbb); try {
		 * Thread.sleep(2000); } catch (InterruptedException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } byte[] bbb1 = new
		 * byte[] { 36 }; buffer.setBytes(bbb1); try { Thread.sleep(2000); }
		 * catch (InterruptedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } byte[] bbb2 = new byte[] { 97 };
		 * buffer.setBytes(bbb2);
		 */
		/*
		 * for (int i = 0; i < 37000000; i++)// 7861 { String str =
		 * "48$fas1f2as1f2as1df21sad2f1a2s1df2a1s2f1as21f2as1fd"; int a1 =
		 * str.getBytes().length; String a = a1 + "$" + str;
		 * 
		 * byte b1[] = a.getBytes(); buffer.setBytes(b1);
		 * 
		 * }
		 */
		byte[] bbb = new byte[]
		{ 57, 56, 56, 36, 80, 75, 3, 4, 20, 0, 0, 0, 8, 0, 108, 105, -112, 69,
				-16, -127, -102, 90, 72, 3, 0, 0, -65, 8, 0, 0, 7, 0, 0, 0, 49,
				50, 51, 46, 116, 120, 116, -107, 85, -51, 78, -37, 64, 16, 62,
				99, -55, -17, -80, -67, 57, -121, -122, 7, 64, 57, -124, 18,
				84, 36, 40, 85, 65, -30, 80, -11, 96, -20, -63, -79, -16, 79,
				100, -81, 11, -88, -30, 97, 34, 46, -108, -10, 4, 78, 82, 17,
				82, -60, -113, -44, -108, 2, -75, 18, 26, 85, -107, -86, -106,
				7, -24, 1, 42, -75, -89, 86, -35, 93, -37, -63, 107, 39, 109,
				-79, 37, -37, -69, 59, -33, -52, 124, 59, -33, -84, 3, -1, -28,
				-41, -21, -49, -94, -80, -30, -24, 24, 80, -93, -43, -40, 20,
				-123, -26, 85, -69, -118, 52, -64, -9, -54, -78, 101, -127, 49,
				-18, 45, -127, -125, 124, -65, -75, 57, 3, -72, 108, -85, 11,
				-74, -77, 76, 38, 122, 126, 16, -68, -1, -78, 115, 74, -33,
				-127, 47, 10, -12, -82, 31, -120, -126, 3, -78, 42, -27, 68,
				-63, -17, -114, -81, 97, 32, 96, -118, -34, -7, -40, 11, -38,
				-43, 78, -13, -43, -113, -60, 100, -9, -88, -5, -87, -43, 61,
				-7, -128, 68, -95, -10, -77, 123, -108, 9, -39, -82, 38, -121,
				-95, 17, 103, 80, 59, 118, 1, 83, -121, -18, -58, -26, 34, 121,
				61, 126, -78, -15, -100, 113, -96, 57, 81, 94, -75, -29, 55,
				65, -93, 119, 122, -39, -82, -118, -62, 52, 88, 26, 46, -49,
				85, 12, 61, 17, -123, 122, -95, 9, 83, 78, 104, 127, -21, 100,
				-113, 50, -88, 31, -44, 90, -99, 102, -3, -100, 122, 104, 93,
				-98, 111, -17, 126, 55, -109, -60, 11, -56, -126, 21, -108,
				-36, 11, 41, 55, 38, 10, 35, -28, 74, -38, -27, 93, -32, 3, 73,
				-72, -84, -69, -61, 44, 67, 119, 15, 100, 19, -92, -5, -78,
				-91, 26, 80, -78, 60, 51, 79, 83, 27, -122, 32, -68, 37, 85,
				-58, 114, -47, 113, 98, -109, 25, 112, 93, 89, -125, 73, -37,
				49, 101, -116, 76, 110, 84, 64, -82, 87, 33, 72, -115, -58, 98,
				11, -9, 108, 11, -61, 42, -90, -75, 26, 97, 87, 98, 45, 4, -11,
				121, -23, 75, 72, -30, -35, -23, -106, -117, 101, 75, 1, 123,
				9, 113, -112, 121, 123, 14, 59, -70, -91, -59, 78, 113, -103,
				114, 120, 104, -37, 70, -34, -12, 12, -84, -105, 86, 65, -15,
				48, 72, 73, 54, 113, 20, 48, 92, 64, -73, 9, -59, 74, 31, 69,
				122, 22, 5, 28, 29, 69, -2, -117, -13, -29, -67, -33, -99, -83,
				-77, -105, -37, -83, -58, -43, -59, 117, -3, 43, -43, 105, -13,
				-78, -74, -41, 59, 124, -73, 19, 92, -5, -33, 14, 59, -39, -12,
				92, -110, 53, -39, -10, -65, -28, -73, 78, -38, -124, 76, 96,
				-39, 93, 14, 113, 21, -126, 19, 5, -82, 43, 82, 74, -111, -110,
				-117, 57, -76, -62, -34, -52, 93, 68, -91, -12, 20, 44, -116,
				-128, 61, 11, 28, 58, 79, 84, -106, 52, -118, -118, -63, 107,
				87, -31, 70, 41, 7, 90, 90, -128, -95, -121, 27, 125, 33, -117,
				-24, 109, 0, 42, 33, -58, 16, 50, 103, 43, -53, 125, 103, -56,
				-27, 70, 5, 62, 9, -118, -25, -52, 99, 23, 96, -128, -126, 109,
				7, -71, -15, -57, 32, 96, -76, -58, 101, 58, -83, -69, 24, 44,
				66, -81, -52, 15, 7, -32, 121, 0, -13, 34, 10, 125, 89, -123,
				108, 11, 40, -35, 96, -84, -70, -111, 126, -120, 124, 122, -51,
				110, -96, -40, -60, -79, -126, 119, -49, 100, 69, -127, 10,
				-34, 127, -37, -71, 96, 39, 100, 120, 64, 50, -79, -46, -102,
				-48, 62, 44, 89, -118, -83, 2, -19, -58, -12, 54, -46, -71, 92,
				95, -36, -52, 94, -117, -19, -99, -94, -91, 78, 0, -5, -110,
				114, 121, -107, 125, -11, -37, -115, 39, -54, -110, -108, 24,
				62, 54, -32, -119, -109, 36, 22, 104, 114, -113, -120, -35,
				-102, -124, 29, 15, 66, -30, -15, 77, 17, 21, 111, -47, -48,
				21, -60, -87, 46, 35, 48, 98, -54, -74, -127, 108, -62, -4,
				-20, -60, 44, 42, 122, -40, -66, -85, -47, 28, 100, 12, 106,
				36, 19, -28, 98, 111, 113, -88, -126, -61, -93, 113, -104, 106,
				-29, -118, 69, -122, -47, -20, -108, 89, -119, 85, 18, -83, 39,
				-50, 78, -23, 95, -22, 10, -127, -3, 114, -92, 96, -31, 106,
				102, -49, 17, 100, 102, 6, -56, 105, -16, 65, 57, -80, -120,
				-109, 50, -43, -19, 26, -87, 37, 89, -99, -118, 78, -84, -120,
				84, 38, 22, -51, -77, 104, 104, 54, 41, 90, -39, -52, 18, 28,
				30, -10, 6, -108, -90, -99, 85, 85, 38, 106, 10, -63, -97, -12,
				-73, 72, 34, -11, -117, 8, -35, -78, -2, -14, 12, 3, -35, 41,
				32, -14, 43, 102, -26, 97, 75, 13, 104, 20, 106, 48, -106, 88,
				-4, -97, -82, 88, -89, 15, 7, -80, -25, 88, -95, -40, -24, -20,
				-6, 31, 80, 75, 1, 2, 63, 0, 20, 0, 0, 0, 8, 0, 108, 105, -112,
				69, -16, -127, -102, 90, 72, 3, 0, 0, -65, 8, 0, 0, 7, 0, 36,
				0, 0, 0, 0, 0, 0, 0, 32, 0, 0, 0, 0, 0, 0, 0, 49, 50, 51, 46,
				116, 120, 116, 10, 0, 32, 0, 0, 0, 0, 0, 1, 0, 24, 0, -24, 124,
				-72, -69, -18, 24, -48, 1, -89, 103, 83, 78, -36, 25, -48, 1,
				-89, 103, 83, 78, -36, 25, -48, 1, 80, 75, 5, 6, 0, 0, 0, 0, 1,
				0, 1, 0, 89, 0, 0, 0, 109, 3, 0, 0, 0, 0 };
		buffer.setBytes(bbb);

	}

	@Override
	public void clearBytes()
	{
		// TODO Auto-generated method stub
		arraysData = null;
	}

}
