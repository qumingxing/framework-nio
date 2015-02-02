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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 2$ab3$abc
 * 
 * @author qmx 2014-12-10 上午11:45:36
 * 
 */
public class LimitChannelBuffer extends AbstractChannelBuffer implements
		ChannelBuffer
{
	// 实际操作数据
	private volatile byte[] arraysData;
	// arraysData中可用元素数量
	private volatile int arraysDataAvaliableLength;
	private final ReentrantLock lock = new ReentrantLock();
	// arraysData回收时要中断其它线程读取
	private volatile boolean synGCFlagLock = false;
	// 读线程安全停止，等待回收
	private volatile boolean loopEndFlagLock = false;
	// arraysData当前读取的位置
	private volatile int arraysDataPostion;
	// 回收的算法arraysDataPostion>gcArraysDataPostionSize时才回收，提高回收利用率
	private int gcArraysDataPostionSize = 10000;
	// 回收线程的执行频率
	private int gcLoopStepTime = 3000;// ms
	private ScheduledExecutorService executorService = Executors
			.newScheduledThreadPool(1);
	// 中转数据的数组，最终transferArraysData中的数据需要转入arraysData
	private volatile byte[] transferArraysData;
	// transferArraysData中可用元素数量
	private volatile int transferArraysAvaliableLength;
	// 数据长度默认为6位数字1000(4位)
	private int unitLength = 6;// 最大数据长度999999
	private int unit;
	// 初始化数组大小
	private static int defaultArraysLength = 1024;
	// 中转数组中最大容量，超过容量限制setBytes()会阻塞等待防止内存过小时溢出
	private int maxTransferCapcity = 10000000;
	private Thread workThread;
	public LimitChannelBuffer(int size)
	{
		arraysData = new byte[size];
		transferArraysData = new byte[size];
		processLastWork();
		startGC();
	}

	public LimitChannelBuffer()
	{
		this(defaultArraysLength);
	}

	/**
	 * 数据转移
	 * 
	 * @param bytes
	 */
	public void setInnerBytes(byte[] bytes)
	{

		checkArraysDataCapcity(bytes.length);
		System.arraycopy(bytes, 0, arraysData, arraysDataAvaliableLength,
				bytes.length);
		arraysDataAvaliableLength += bytes.length;

	}

	/**
	 * 放置新数据
	 * 
	 * @param bytes
	 */
	public void setBytes(byte[] bytes)
	{
		while (transferArraysAvaliableLength > maxTransferCapcity)
		{
			setMaxTransferCapcityWaitFor();
		}
		try
		{
			lock.lock();
			checkTransferArraysDataCapcity(bytes.length);
			System.arraycopy(bytes, 0, transferArraysData,
					transferArraysAvaliableLength, bytes.length);
			transferArraysAvaliableLength += bytes.length;
			synchronized (arraysData)
			{
				arraysData.notifyAll();
			}
		} finally
		{
			lock.unlock();
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
	 * 检查transferArraysData容量是否能满足
	 * 
	 * @param arrLegnth
	 */
	private void checkTransferArraysDataCapcity(int arrLegnth)
	{
		int oldCapacity = transferArraysData.length;
		if (arrLegnth + transferArraysAvaliableLength > oldCapacity)
		{
			int newCapacity = ((arrLegnth + transferArraysAvaliableLength) * 3) / 2 + 1;
			if (newCapacity < arrLegnth)
				newCapacity = arrLegnth;
			transferArraysData = Arrays.copyOf(transferArraysData, newCapacity);
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
		} else
		{
			System.out.println("不够..等待" + avaliableNum + "," + getNum);
		}
		return byt;
	}

	/**
	 * 从transferArraysData中获取数据并移除失效的数据，此方法是线程安全的
	 * 
	 * @param offset
	 * @param size
	 */
	public void getBytesArrays(int offset, int size)
	{
		byte[] byt = Arrays.copyOfRange(transferArraysData, offset, size);
		setInnerBytes(byt);
		System.arraycopy(transferArraysData, size, transferArraysData, 0,
				transferArraysAvaliableLength - size);
		for (int i = transferArraysAvaliableLength - size; i < transferArraysAvaliableLength; i++)
		{
			transferArraysData[i] = 0x00;
		}
		transferArraysAvaliableLength -= size;
	}

	/**
	 * 废弃数据的回收任务
	 */
	public void startGC()
	{
		executorService.scheduleAtFixedRate(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				if (arraysDataPostion > gcArraysDataPostionSize)// arraysDataAvaliableLength
				{
					synGCFlagLock = true;
					while (!loopEndFlagLock)
					{
						gcWaitFor();
					}
					System.arraycopy(arraysData, arraysDataPostion, arraysData,
							0, arraysDataAvaliableLength - arraysDataPostion);
					for (int i = arraysDataAvaliableLength - arraysDataPostion; i < arraysDataAvaliableLength; i++)
					{
						arraysData[i] = 0x00;
					}
					arraysDataAvaliableLength -= arraysDataPostion;
					arraysDataPostion = 0;
					System.out.println("GC" + "," + arraysDataAvaliableLength);
					loopEndFlagLock = false;
					synGCFlagLock = false;
				}
				System.out.println("GC END");
			}
		}, 0, gcLoopStepTime, TimeUnit.MILLISECONDS);
	}

	/**
	 * 执行最后的数据切分工作
	 */
	public void processLastWork()
	{
		workThread = new Thread()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				byte[] by = new byte[unitLength];
				while (true)
				{
					while (synGCFlagLock)
					{
						loopEndFlagLock = true;
						gcWaitFor();
					}
					try
					{
						lock.lock();
						if (transferArraysAvaliableLength > 0)
						{
							getBytesArrays(0, transferArraysAvaliableLength);
						}
					} finally
					{
						lock.unlock();
					}
					if ((arraysDataAvaliableLength - arraysDataPostion) == 0)
					{
						waitNotify();
					}

					for (int i = arraysDataPostion; i < arraysDataAvaliableLength; i++)
					{
						byte oneByte = arraysData[i];
						if (oneByte == 36)
						{
							int le = Integer.parseInt(new String(Arrays
									.copyOfRange(by, 0, unit)));
							unit = 0;
							byte[] dataArr = copyFromArraysData(i + 1, le);
							// 长度不够取
							if (dataArr == null)
							{
								// 有数据
								waitNotify();
								// 结束重新取数据
								break;
							}
							arraysDataPostion = (i + 1 + le);
							i = arraysDataPostion - 1;
							String sss = new String(dataArr);
							System.out.println(sss);
							if (synGCFlagLock)
								break;
						} else
						{
							by[unit++] = oneByte;
						}
					}
				}
			}

		};
		workThread.start();
	}

	public static void main(String[] args) throws UnsupportedEncodingException
	{
		 LimitChannelBuffer buffer = new LimitChannelBuffer();

		int aaa = 0;
		while (aaa < 1)
		{
			for (int i = 0; i < 1; i++)// 7861
			{
				String str = "ab1212323121231231cf f dd d dfgdab1212323121231231cf f dd d dfgdfab1212323121231231cf f dd d dfgdff"
						+ i;
				int a1 = str.getBytes().length + 1;
				String a = a1 + "$" + str;

				byte b1[] = a.getBytes();
				buffer.setBytes(b1);

			}

			try
			{
				Thread.sleep(2000);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			aaa++;
		}
		try
		{
			Thread.sleep(2000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] aaaaaa = new byte[]
		{ 36 };
		buffer.setBytes(aaaaaa);
		for (int i = 0; i < 10; i++)// 7861
		{
			String str = "ab1212323121231231cf f dd d dfgdab1212323121231231cf f dd d dfgdfab1212323121231231cf f dd d dfgdff"
					+ i;
			int a1 = str.getBytes().length;
			String a = a1 + "$" + str;

			byte b1[] = a.getBytes();
			buffer.setBytes(b1);

		}
		/*
		 * while (true) { System.out.println("over" +
		 * buffer.arraysDataAvaliableLength + "," +
		 * buffer.transferArraysAvaliableLength + "," +
		 * buffer.arraysDataPostion); try { Thread.sleep(5000); } catch
		 * (InterruptedException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } String str =
		 * "ab1212323121231231cf f dd d dfgdab1212323121231231cf f dd d dfgdfab1212323121231231cf f dd d dfgdff"
		 * ; int a1 = str.getBytes().length; String a = a1 + "$" + str; byte
		 * b1[] = a.getBytes(); buffer.setBytes(b1); }
		 */

	}

	@SuppressWarnings("static-access")
	private void gcWaitFor()
	{
		try
		{
			Thread.currentThread().sleep(100);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("static-access")
	private void setMaxTransferCapcityWaitFor()
	{
		try
		{
			Thread.currentThread().sleep(200);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void waitNotify()
	{
		synchronized (arraysData)
		{
			try
			{
				// 可能会导致超过gcArraysDataPostionSize不能释放，待有新的数据进入后会稍后释放
				arraysData.wait();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setThreadPool(ThreadPool threadPool)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearBytes()
	{
		// TODO Auto-generated method stub
		
	}
}
