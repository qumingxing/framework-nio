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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * 核心心选择器选择过过程，所有通道选择的底层操作都聚集在这里，该类提供接收绝大部分的可选属性，并再各自的业务中将相应的属性注入到目标模块中。
 * 
 * @author qmx 2014-12-1 下午3:51:26
 * 
 */
public class SelectProcess
{
	/**
	 * @see HandleListener
	 */
	private HandleListener handleListener;
	/**
	 * @see MessageContext
	 */
	private MessageContext messageContext;
	/**
	 * 接收缓冲区池的创建{@link ChannelBuffersPool}
	 */
	private ChannelBuffersPool buffersPool = new ChannelBuffersPool();
	/**
	 * 工作线程池用于分发各类请求
	 */
	private static final WorkTaskThreadPool workTaskThreadPool = WorkTaskThreadPool
			.getInstance();
	/**
	 * 写线程池，并发操作的时候提高吞吐量
	 */
	private static final WriteThreadPool WRITE_POOL = WriteThreadPool
			.getInstance();
	/**
	 * 同步消息处理线程池
	 */
	private static final SynchronizedThreadPool SYNCHRONIZED_THREAD_POOL = SynchronizedThreadPool
			.getInstance();
	/**
	 * 主选择器
	 */
	private Selector selector;
	/**
	 * 设置{@link ChannelBuffer}类型。见{@link BufferChannelFactory}
	 */
	private Class<? extends ChannelBuffer> bufferType;
	/**
	 * @see ConfigResources
	 */
	private ConfigResources config;
	/**
	 * {@link BufferChannelFactory}工厂的创建，用于生成新的或获取已有的{@link ChannelBuffer}
	 */
	private BufferChannelFactory bufferChannelFactory = BufferChannelFactory
			.getBuferChannelFactory();

	public SelectProcess(int writePoolSize, int readPoolSize)
	{
		WRITE_POOL.setPoolSize(writePoolSize);
		WRITE_POOL.createThreadPool();
		MessageAdapter.setThreadPool(WRITE_POOL);
		// recieveMessage = new RecieveMessage(writePoolSize);
		workTaskThreadPool.setPoolSize(readPoolSize);
		workTaskThreadPool.createThreadPool();
		DestoryChannel.setWorkTaskThreadPool(workTaskThreadPool);
	}

	public SelectProcess(int writePoolSize, int readPoolSize, int synchPoolSize)
	{
		WRITE_POOL.setPoolSize(writePoolSize);
		WRITE_POOL.createThreadPool();
		MessageAdapter.setThreadPool(WRITE_POOL);
		// recieveMessage = new RecieveMessage(writePoolSize);
		workTaskThreadPool.setPoolSize(readPoolSize);
		workTaskThreadPool.createThreadPool();
		SYNCHRONIZED_THREAD_POOL.setPoolSize(synchPoolSize);
		SYNCHRONIZED_THREAD_POOL.createThreadPool();
		DestoryChannel.setWorkTaskThreadPool(workTaskThreadPool);
	}

	protected void select(Set<SelectionKey> keys)
	{
		Iterator<SelectionKey> iter = keys.iterator();
		while (iter.hasNext())
		{
			SelectionKey key = iter.next();
			iter.remove();
			if (!key.isValid())
			{
				key.cancel();
				continue;
			}
			if (key.isAcceptable())
			{
				ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key
						.channel();
				try
				{
					SocketChannel socketChannel = serverSocketChannel.accept();
					// ByteChannel byteChannel = socketChannel;byteChannel.r
					socketChannel.socket().setSendBufferSize(8096);
					socketChannel.socket().setReceiveBufferSize(8096);
					// socketChannel.socket().setKeepAlive(true);长时间处理空闲是否要关闭,默认false
					// socketChannel.socket().setTcpNoDelay(true);
					socketChannel.configureBlocking(false);
					Channel channel = new ChannelImpl();
					channel.setChannel(socketChannel);
					channel.setAcceptDate(new Date());
					if (bufferChannelFactory.isCertificateAuth())
						channel.setCertificateAuth(true);
					Channels.addChannel(socketChannel.socket()
							.getRemoteSocketAddress().toString(), channel);
					accept(socketChannel);

					// socketChannel.register(selector, SelectionKey.OP_READ);
				} catch (IOException e)
				{
					exception(null, e);
				}
			} else if (key.isConnectable())
			{
				SocketChannel socketChannel = (SocketChannel) key.channel();
				if (socketChannel.isConnectionPending())
				{
					try
					{
						while (!socketChannel.finishConnect())
						{
							Thread.sleep(500);
						}
						Channel channel = new ChannelImpl();
						channel.setChannel(socketChannel);
						Channels.addChannel(socketChannel.socket()
								.getRemoteSocketAddress().toString(), channel);
						connect(socketChannel);
						// socketChannel.register(selector,
						// SelectionKey.OP_WRITE);
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						DestoryChannel.destory(socketChannel, e);
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} else if (key.isReadable())
			{
				SocketChannel socketChannel = (SocketChannel) key.channel();
				read(socketChannel, key);

			} else if (key.isWritable())
			{
				SocketChannel socketChannel = (SocketChannel) key.channel();
				write(socketChannel);
			}
		}
	}

	private void accept(SocketChannel socketChannel)
	{
		ChannelBuffer channelBuffer = getChannelBufer(socketChannel);
		MethodWorker methodWorker = newMethodWorker(channelBuffer,
				HandleEnum.accept);
		workTaskThreadPool.multiExecute(methodWorker);
	}

	private void exception(SocketChannel socketChannel, Exception e)
	{
		ChannelBuffer channelBuffer = getChannelBufer(socketChannel);
		MethodWorker methodWorker = newMethodWorker(channelBuffer,
				HandleEnum.exception);
		methodWorker.setException(e);
		workTaskThreadPool.multiExecute(methodWorker);
	}

	private void connect(SocketChannel socketChannel)
	{
		// 设置连接状态为已连接
		DestoryChannel.CURRENT_CONNECT_STATE = DestoryChannel.CONNECTED;
		ChannelBuffer channelBuffer = getChannelBufer(socketChannel);
		MethodWorker methodWorker = newMethodWorker(channelBuffer,
				HandleEnum.connect);
		workTaskThreadPool.multiExecute(methodWorker);
	}

	private void read(final SocketChannel channel, final SelectionKey key)
	{

		// TODO Auto-generated method stub
		int temp = 0;
		int sumByte = 0;
		try
		{
			// 5$abcde2$aa
			ByteBuffer byteBuffer = buffersPool.getByteBuffer(16000);
			while ((temp = channel.read(byteBuffer)) > 0)
			{
				sumByte += temp;
				if (!byteBuffer.hasRemaining())
					break;
			}
			if (sumByte > 0)
			{
				byteBuffer.flip();
				int outCount = byteBuffer.remaining();
				byte[] byt = new byte[outCount];
				byteBuffer.get(byt);
				ChannelBuffer channelBuffer = getChannelBufer(channel);
				channelBuffer.setBytes(byt);
				buffersPool.realse(byteBuffer);
			}
			if (temp == -1)
			{
				key.cancel();
				/*
				 * channel.close(); ChannelBuffer channelBuffer =
				 * getChannelBufer(channel); MethodWorker methodWorker =
				 * newMethodWorker(channelBuffer, HandleEnum.close);
				 * workTaskThreadPool.multiExecute(methodWorker);
				 */
				DestoryChannel.destory(channel, null);
			}
		} catch (IOException e)
		{
			key.cancel();
			// channel.close();
			/*
			 * ChannelBuffer channelBuffer = getChannelBufer(channel);
			 * MethodWorker methodWorker = newMethodWorker(channelBuffer,
			 * HandleEnum.exception); methodWorker.setException(e);
			 * workTaskThreadPool.multiExecute(methodWorker);
			 */
			DestoryChannel.destory(channel, e);
		}
	}

	private void write(SocketChannel socketChannel)
	{
		ChannelBuffer channelBuffer = getChannelBufer(socketChannel);
		MethodWorker methodWorker = newMethodWorker(channelBuffer,
				HandleEnum.write);
		workTaskThreadPool.multiExecute(methodWorker);
	}

	public HandleListener getHandleListener()
	{
		return handleListener;
	}

	public void setHandleListener(HandleListener handleListener)
	{
		this.handleListener = handleListener;
	}

	public void setMessageContext(MessageContext context)
	{
		this.messageContext = context;
		this.messageContext.setDefaultMessageContext(messageContext);
		// Channels.setMessageContext(messageContext);
		// recieveMessage.setMessageContext(this.messageContext);
	}

	public MessageContext getMessageContext()
	{
		if (null != this.messageContext)
		{
			return this.messageContext;
		}
		setMessageContext(MessageContext.getDefaultMessageContext());
		return this.messageContext;
		// Channels.setMessageContext(messageContext);
		// recieveMessage.setMessageContext(this.messageContext);
	}

	public Selector getSelector()
	{
		return selector;
	}

	public void setSelector(Selector selector)
	{
		this.selector = selector;
	}

	public ChannelBuffer getChannelBufer(SocketChannel channel)
	{
		if (null == messageContext)
		{
			messageContext = MessageContext.getDefaultMessageContext();
		}
		if (null == bufferType)
		{
			setBufferType(DefaultChannelBuffer.class);
		}
		ChannelBuffer channelBuffer = bufferChannelFactory.getBuffer(channel,
				messageContext, handleListener, workTaskThreadPool, selector,
				this.bufferType);
		return channelBuffer;
	}

	public MethodWorker newMethodWorker(ChannelBuffer channelBuffer,
			HandleEnum handleEnum)
	{
		MethodWorker methodWorker = new MethodWorker();
		methodWorker.setChannelBuffer(channelBuffer);
		methodWorker.setMethodName(handleEnum);
		return methodWorker;
	}

	public void setBufferType(Class<? extends ChannelBuffer> bufferType)
	{
		this.bufferType = bufferType;
	}

	public void setConfig(ConfigResources config)
	{
		this.config = config;
		if (config.getPointModel() == PointModel.SERVER)
		{
			if (this.config.isCertificateAuth())
			{
				bufferChannelFactory.setCertificateAuth(true);
				bufferChannelFactory.setCertificateModel(config
						.getCertificateModel());
				bufferChannelFactory.setCertificateInterface(config
						.getCertificateInterface());
			}
		}
		DestoryChannel.setConfig(this.config);
	}

	public void setScheduledCheckValid(ScheduledCheckValid scheduledCheckValid)
	{
		// TODO Auto-generated method stub
		Channels.startScheduledCheck(scheduledCheckValid);
	}
}
