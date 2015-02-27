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
 * 消息在发送前预处理适配器，包括对消息的编码，格式化，单/多线程传输方式
 * 
 * @author qmx 2014-12-1 上午10:19:18
 * 
 */
public class MessageAdapter extends AbstractMessage
{
	/**
	 * 当前通道对象
	 */
	private Channel channel;
	/**
	 * 消息格式化接口可以通过{@link MessageContext}的<code>setMessageFormat</code>
	 * 方法设置，如果未设置该对象， 框架会使用{@link MessageContext}中默认的
	 * <code>getDefaultMessageContext</code>来处理消息，默认的格式化类为
	 * {@link MessageFormatStringToString}。
	 */
	private MessageFormat messageFormat;
	/**
	 * 在非二进制消息发送时使用的写线程池
	 */
	private static ThreadPool threadPool;

	/**
	 * 发送异步消息
	 * 
	 * @param message
	 *            消息内容
	 */
	protected void write(Object message)
	{
		byte[] sendMsg = null;
		// 普通文本类发送
		if (messageFormat instanceof MessageFormatToString)
		{
			super.setMessage(message);
			super.getEncoderAndDecoder().encode();
			sendMsg = super.getEncoderAndDecoder().getEncodeByt();
			WriteWorker writeWorker = new WriteWorker();
			writeWorker.setChannel(channel);
			writeWorker.setData(messageFormat.format(sendMsg));
			threadPool.multiExecute(writeWorker);
		}
		// 文件传输
		else if (messageFormat instanceof MessageFormatToBytes)
		{
			super.setMessage(message);
			super.getEncoderAndDecoder().encode();
			sendMsg = super.getEncoderAndDecoder().getEncodeByt();
			WriteWorker writeWorker = new WriteWorker();
			writeWorker.setChannel(channel);
			writeWorker.setData(messageFormat.format(sendMsg));
			// 文件要使用当前线程发送保证顺序
			SingleWriteThreadPool singleWriteThread = SingleWriteThreadPoolFactory
					.getSingleWriteThreadPoolFactory().getSingleWriteThread(
							channel.getChannel());
			singleWriteThread.multiExecute(writeWorker);
		}
	}

	/**
	 * 发送异步消息并同步等待返回的结果，默认3秒超时
	 * 
	 * @param message
	 *            发送的消息内容
	 * @return 响应的消息内容
	 */
	protected Object writeSynchronized(Object message)
	{
		this.write(message);
		return SynchronizedThreadPool.getInstance().getResult(
				MessageNumber.getStoreMessageNumber());
	}

	/**
	 * 自定义全局超时时间，发送异步消息并同步等待返回的结果
	 * 
	 * @param message
	 *            发送的消息内容
	 * @param timeout
	 *            超时时间(毫秒)
	 * @return 响应的消息内容
	 */
	protected Object writeSynchronized(Object message, long timeout)
	{
		this.write(message);
		SynchronizedThreadPool.getInstance().setDefaultSynchronizedTimeout(
				timeout);
		return SynchronizedThreadPool.getInstance().getResult(
				MessageNumber.getStoreMessageNumber());
	}

	public Channel getChannel()
	{
		return channel;
	}

	public void setChannel(Channel channel)
	{
		this.channel = channel;
	}

	public MessageFormat getMessageFormat()
	{
		return messageFormat;
	}

	public void setMessageFormat(MessageFormat messageFormat)
	{
		this.messageFormat = messageFormat;
	}

	public static ThreadPool getThreadPool()
	{
		return threadPool;
	}

	public static void setThreadPool(ThreadPool threadPool1)
	{
		threadPool = threadPool1;
	}
}
