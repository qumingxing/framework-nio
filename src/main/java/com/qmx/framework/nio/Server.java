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
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ExecutorService;

/**
 * 服务端对象继承了{@link AbstractConnection}抽象类。
 * 
 * @author qmx 2014-12-1 上午11:48:48
 * 
 */
public class Server extends AbstractConnection
{
	/**
	 * 服务端的{@link ServerSocketChannel}
	 */
	private ServerSocketChannel serverSocketChannel;
	/**
	 * 服务端启动用的IP和端口{@link InetSocketAddress}
	 */
	private InetSocketAddress inetAddress;
	/**
	 * @see ServerSocket
	 */
	private ServerSocket serverSocket;
	/**
	 * 主选择器
	 */
	private Selector selector;
	/**
	 * 选择器策略@see SelectorStrategy
	 */
	private SelectorStrategy selectorStrategy;

	public Server(int port)
	{
		//String host = InetAddress.getLocalHost().getHostAddress();
		//inetAddress = new InetSocketAddress(InetAddress.getByName(host),port);
		inetAddress = new InetSocketAddress(port);
	}

	public ServerSocket getServerSocket()
	{
		serverSocket = serverSocketChannel.socket();
		return serverSocket;
	}

	public void selectorOpen() throws IOException
	{
		selector = Selector.open();
	}

	public void bind() throws IOException
	{
		getServerSocket().bind(inetAddress);
	}

	public SelectorStrategy getSelectorStrategy()
	{
		return selectorStrategy;
	}

	public void setSelectorStrategy(SelectorStrategy selectorStrategy)
	{
		this.selectorStrategy = selectorStrategy;
	}

	public void serverSocketChannelOpen() throws IOException
	{
		serverSocketChannel = ServerSocketChannel.open();
		serverSocketChannel.configureBlocking(false);
	}

	public void register(int opts) throws ClosedChannelException
	{
		serverSocketChannel.register(selector, opts);
	}

	public void start()
	{
		try
		{
			serverSocketChannelOpen();
			bind();
			selectorOpen();
			register(SelectionKey.OP_ACCEPT);
			selectorStrategy.execute(selector);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		Server server = new Server(9999);
		server.start();
	}

	@Override
	public ExecutorService getSelectorThread()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Selector getSelector()
	{
		// TODO Auto-generated method stub
		return selector;
	}
}
