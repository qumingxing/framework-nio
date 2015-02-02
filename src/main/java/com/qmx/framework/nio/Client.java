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
import java.net.InetSocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;

/**
 * 客户端对象继承了{@link AbstractConnection}抽象类，目的是在客户端重新连接的时候调用其实现的方法，和利用原有参数。
 * 
 * @author qmx 2014-12-1 上午11:48:48
 * 
 */
public class Client extends AbstractConnection
{
	/**
	 * 客户端与服务端连接的单一的{@link SocketChannel}通道
	 */
	private SocketChannel socketChannel;
	/**
	 * 客户端连接服务端所需要的IP、Port信息
	 */
	private InetSocketAddress inetAddress;
	/**
	 * 客户端选择器
	 */
	private Selector selector;
	/**
	 * 客户端选择器策略，可以自行扩展{@link SelectorStrategy}接口
	 */
	private SelectorStrategy selectorStrategy;
	/**
	 * 客户端选择器线程，当客户端意外断开后，重连之前需要终止当前线程，否则会形成递归调用
	 */
	private ExecutorService selectorThread;

	/**
	 * 根据指定的IP和端口号来创建一个新的客户端对象
	 * 
	 * @param ip
	 *            服务端IP地址
	 * @param port
	 *            服务端端口号
	 */
	public Client(String ip, int port)
	{
		inetAddress = new InetSocketAddress(ip, port);
	}

	/**
	 * 打开客户端主选择器
	 * 
	 * @throws IOException
	 *             如果打开失败
	 */
	public void selectorOpen() throws IOException
	{
		selector = Selector.open();
	}

	/**
	 * 向服务端发起连接
	 * 
	 * @throws IOException
	 *             如果连接失败
	 */
	public void connect() throws IOException
	{
		socketChannel.connect(inetAddress);
	}

	/**
	 * 获取当前设置的选择器实现策略
	 * 
	 * @return 返回选择器实现策略
	 */
	public SelectorStrategy getSelectorStrategy()
	{
		return selectorStrategy;
	}

	/**
	 * 设置当前的选择器实现策略
	 * 
	 * @param selectorStrategy
	 *            选择器实现策略
	 */
	public void setSelectorStrategy(SelectorStrategy selectorStrategy)
	{
		this.selectorStrategy = selectorStrategy;
	}

	/**
	 * 打开通道
	 * 
	 * @throws IOException
	 *             打开失败
	 */
	public void serverSocketChannelOpen() throws IOException
	{
		socketChannel = SocketChannel.open();
		socketChannel.configureBlocking(false);
	}

	/**
	 * 注册选择器事件
	 * 
	 * @param opts
	 *            事件标识
	 * @throws ClosedChannelException
	 *             注册失败
	 */
	public void register(int opts) throws ClosedChannelException
	{
		socketChannel.register(selector, opts);
	}

	public void start()
	{
		try
		{
			serverSocketChannelOpen();
			selectorOpen();
			connect();
			register(SelectionKey.OP_CONNECT);
			selectorThread = selectorStrategy.execute(selector);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public ExecutorService getSelectorThread()
	{
		// TODO Auto-generated method stub
		return selectorThread;
	}

	@Override
	public Selector getSelector()
	{
		// TODO Auto-generated method stub
		return selector;
	}
}
