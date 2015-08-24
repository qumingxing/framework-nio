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
import java.nio.ByteBuffer;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 123\r456\r
 * 
 * @author qmx 2014-12-10 上午11:45:36
 * 
 */
public class DelimiterLimitChannelBuffer extends AbstractChannelBuffer implements
		ChannelBuffer
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
	private MethodWorker methodWorker;
	private static final Logger logger = LoggerFactory
			.getLogger(LengthSplitChannelBuffer.class);

	public void setThreadPool(ThreadPool threadPool)
	{
		this.threadPool = threadPool;
	}

	public DelimiterLimitChannelBuffer(int size)
	{
		arraysData = new byte[size];
	}

	public DelimiterLimitChannelBuffer()
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
		byt = Arrays.copyOfRange(arraysData, offset, size);
			// arraysDataPostion = offset + size;
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
			if (oneByte == 13)//\r
			{
				byte[] dataArr = copyFromArraysData(arraysDataPostion, i);
				// 长度不够取
				if (dataArr == null)
				{
					break;
				}
				arraysDataPostion = (i + 1);
				i = arraysDataPostion - 1;
				//System.out.println(new String(dataArr));
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
			}
		}
	}

	public static void main(String[] args) throws UnsupportedEncodingException, InterruptedException
	{
		//System.out.print(aa);
		DelimiterLimitChannelBuffer limitChannelBuffer = new DelimiterLimitChannelBuffer();
		String aaab = "123\r4\r5\r6789\rabc";
		ByteBuffer buffer = ByteBuffer.allocate(aaab.getBytes().length);
		buffer.put(aaab.getBytes());
		limitChannelBuffer.setBytes(buffer.array());
		Thread.sleep(5000);
		String cc = "de\rfghi\rjk\r";
		ByteBuffer buffer111 = ByteBuffer.allocate(cc.getBytes().length);
		buffer111.put(cc.getBytes());
		limitChannelBuffer.setBytes(buffer111.array());
		limitChannelBuffer.setBytes(buffer111.array());

	}

	@Override
	public void clearBytes()
	{
		// TODO Auto-generated method stub
		arraysData = null;
	}
}
