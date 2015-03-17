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
	/**
	 * 默认的接收数据缓冲大小
	 */
	private int defaultBufferCapacity = 2048;

	/**
	 * 构造一个设置读、写线程池大小的{@link SelectProcess}对象
	 * 
	 * @param writePoolSize
	 *            写线程池大小
	 * @param readPoolSize
	 *            读线程池大小
	 */
	protected SelectProcess(int writePoolSize, int readPoolSize)
	{
		WRITE_POOL.setPoolSize(writePoolSize);
		WRITE_POOL.createThreadPool();
		MessageAdapter.setThreadPool(WRITE_POOL);
		workTaskThreadPool.setPoolSize(readPoolSize);
		workTaskThreadPool.createThreadPool();
		DestoryChannel.setWorkTaskThreadPool(workTaskThreadPool);
	}

	/**
	 * 构造一个设置读、写、同步等待线程池大小的{@link SelectProcess}对象
	 * 
	 * @param writePoolSize
	 *            写线程池大小
	 * @param readPoolSize
	 *            读线程池大小
	 * @param synchPoolSize
	 *            同步发送消息等待线程池大小
	 */
	protected SelectProcess(int writePoolSize, int readPoolSize,
			int synchPoolSize)
	{
		WRITE_POOL.setPoolSize(writePoolSize);
		WRITE_POOL.createThreadPool();
		MessageAdapter.setThreadPool(WRITE_POOL);
		workTaskThreadPool.setPoolSize(readPoolSize);
		workTaskThreadPool.createThreadPool();
		SYNCHRONIZED_THREAD_POOL.setPoolSize(synchPoolSize);
		SYNCHRONIZED_THREAD_POOL.createThreadPool();
		DestoryChannel.setWorkTaskThreadPool(workTaskThreadPool);
	}

	/**
	 * 核心选择器业务处理
	 * 
	 * @param keys
	 *            选择器键
	 */
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
					socketChannel.socket().setSendBufferSize(8096);
					socketChannel.socket().setReceiveBufferSize(8096);
					socketChannel.configureBlocking(false);
					String channelName = socketChannel.socket()
							.getRemoteSocketAddress().toString();
					Channel channel = new ChannelImpl();
					channel.setChannel(socketChannel);
					channel.setAcceptDate(System.currentTimeMillis());
					channel.setChannelName(channelName);
					if (bufferChannelFactory.isCertificateAuth())
						channel.setCertificateAuth(true);
					Channels.addChannel(channelName, channel);
					accept(socketChannel);
				} catch (IOException e)
				{
					exception(null, e);
				}
			} else if (key.isConnectable())
			{
				SocketChannel socketChannel = (SocketChannel) key.channel();
				if (socketChannel.isConnectionPending())
				{
					Channel channel = null;
					try
					{
						socketChannel.finishConnect();
						String channelName = socketChannel.socket()
								.getRemoteSocketAddress().toString();
						channel = new ChannelImpl();
						channel.setChannel(socketChannel);
						channel.setChannelName(channelName);
						Channels.addChannel(channelName, channel);
						connect(socketChannel);
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						DestoryChannel.destory(channel, e);
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

	/**
	 * 接受一个新的客户端连接
	 * 
	 * @param socketChannel
	 *            客户端通道
	 */
	private void accept(SocketChannel socketChannel)
	{
		ChannelBuffer channelBuffer = getChannelBufer(socketChannel);
		MethodWorker methodWorker = newMethodWorker(channelBuffer,
				HandleEnum.accept);
		workTaskThreadPool.multiExecute(methodWorker);
	}

	/**
	 * 通道发生异常
	 * 
	 * @param socketChannel
	 *            通道对象
	 * @param e
	 *            异常对象
	 */
	private void exception(SocketChannel socketChannel, Exception e)
	{
		ChannelBuffer channelBuffer = getChannelBufer(socketChannel);
		MethodWorker methodWorker = newMethodWorker(channelBuffer,
				HandleEnum.exception);
		methodWorker.setException(e);
		workTaskThreadPool.multiExecute(methodWorker);
	}

	/**
	 * 客户端已连接上一个服务端
	 * 
	 * @param socketChannel
	 *            通道对象
	 */
	private void connect(SocketChannel socketChannel)
	{
		// 设置连接状态为已连接
		DestoryChannel.CURRENT_CONNECT_STATE = DestoryChannel.CONNECTED;
		ChannelBuffer channelBuffer = getChannelBufer(socketChannel);
		MethodWorker methodWorker = newMethodWorker(channelBuffer,
				HandleEnum.connect);
		workTaskThreadPool.multiExecute(methodWorker);
		heartProcess();
	}

	/**
	 * 客户端发送心跳机制执行<br/>
	 * 只有是客户端并且启用了心跳机制才可以执行。
	 */
	private void heartProcess()
	{
		if (null == config)
			return;
		if (null != config.getHeartCheck()
				&& config.getHeartCheck().isEnableHeart())
		{
			HeartCheck heartCheck = config.getHeartCheck();
			if (null != config.getPointModel()
					&& config.getPointModel() == PointModel.CLIENT)
			{
				HeartMessageAdapter.getInstance().executeHeart(heartCheck);
			}
		}
	}

	/**
	 * 客户端\服务端读取消息内容
	 * 
	 * @param channel
	 *            消息通道
	 * @param key
	 *            选择键
	 */
	private void read(final SocketChannel channel, final SelectionKey key)
	{

		// TODO Auto-generated method stub
		int temp = 0;
		int sumByte = 0;
		ByteBuffer byteBuffer = null;
		try
		{
			byteBuffer = buffersPool.getByteBuffer(defaultBufferCapacity);
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
			}
			if (temp == -1)
			{
				key.cancel();
				DestoryChannel.destory(Channels.getChannel(channel), null);
			}
		} catch (IOException e)
		{
			key.cancel();
			DestoryChannel.destory(Channels.getChannel(channel), e);
		} finally
		{
			buffersPool.realse(byteBuffer);
		}
	}

	/**
	 * 服务端接受到一个新的连接或客户端第一次连接成功一个服务端后执行写方法
	 * 
	 * @param socketChannel
	 *            消息通道
	 */
	private void write(SocketChannel socketChannel)
	{
		ChannelBuffer channelBuffer = getChannelBufer(socketChannel);
		MethodWorker methodWorker = newMethodWorker(channelBuffer,
				HandleEnum.write);
		workTaskThreadPool.multiExecute(methodWorker);
	}

	/**
	 * 获取设置的{@link HandleListener}对象
	 * 
	 * @return {@link HandleListener}
	 */
	public HandleListener getHandleListener()
	{
		return handleListener;
	}

	/**
	 * 设置{@link HandleListener}对象
	 * 
	 * @param handleListener
	 *            {@link HandleListener}
	 */
	public void setHandleListener(HandleListener handleListener)
	{
		this.handleListener = handleListener;
	}

	/**
	 * 设置全局的消息上下文环境
	 * 
	 * @param context
	 *            {@link MessageContext}
	 */
	public void setMessageContext(MessageContext context)
	{
		this.messageContext = context;
		this.messageContext.setDefaultMessageContext(messageContext);
	}

	/**
	 * 获取消息的上下文环境
	 * 
	 * @return {@link MessageContext}
	 */
	public MessageContext getMessageContext()
	{
		if (null != this.messageContext)
		{
			return this.messageContext;
		}
		setMessageContext(MessageContext.getDefaultMessageContext());
		return this.messageContext;
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

	/**
	 * 设置消息接受缓冲区的类型
	 * 
	 * @param bufferType
	 *            {@link ChannelBuffer}
	 */
	public void setBufferType(Class<? extends ChannelBuffer> bufferType)
	{
		this.bufferType = bufferType;
	}

	/**
	 * 设置全局的资源配置对象
	 * 
	 * @param config
	 *            {@link ConfigResources}
	 */
	public void setConfig(ConfigResources config)
	{
		this.config = config;
		// 服务端专有的业务配置
		if (config.getPointModel() == PointModel.SERVER)
		{
			// 服务端认证相关配置
			if (this.config.isCertificateAuth())
			{
				bufferChannelFactory.setCertificateAuth(true);
				bufferChannelFactory.setCertificateModel(config
						.getCertificateModel());
				bufferChannelFactory.setCertificateInterface(config
						.getCertificateInterface());
			}
			// 服务端心跳检测配置
			bufferChannelFactory.setHeartCheck(config.getHeartCheck());
			Channels.checkHeart(config.getHeartCheck());
		}
		// 公共配置
		bufferChannelFactory.setPointModel(config.getPointModel());
		DestoryChannel.setConfig(this.config);
	}

	/**
	 * 服务端启动检查认证客户端的有效性
	 * 
	 * @param scheduledCheckValid
	 *            {@link ScheduledCheckValid}
	 */
	public void setScheduledCheckValid(ScheduledCheckValid scheduledCheckValid)
	{
		// TODO Auto-generated method stub
		Channels.startScheduledCheck(scheduledCheckValid);
	}

	/**
	 * 设置默认的接收数据缓冲区大小
	 * 
	 * @param defaultBufferCapacity
	 *            缓冲区大小
	 */
	public void setDefaultBufferCapacity(int defaultBufferCapacity)
	{
		this.defaultBufferCapacity = defaultBufferCapacity;
	}

}
