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

import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLEngine;

/**
 * 通道缓冲区核心接口，所有接收到数据会通过该接口中的实现方法加工处理， 消息的认证分发。
 * 
 * @author qmx 2014-12-11 上午10:08:06
 * 
 */
public interface ChannelBuffer
{
	/**
	 * 从{@link SocketChannel}通道中接收到原始数据后，设置到通道处理缓冲区中，缓冲区会对原始数据进行处理。
	 * 
	 * @param bytes
	 */
	public void setBytes(byte[] bytes);

	/**
	 * 清空缓冲区中的所有数据，释放重量级资源
	 */
	public void clearBytes();

	/**
	 * 设置当前缓冲区对应的{@link SocketChannel}通道
	 * 
	 * @param socketChannel
	 *            socket通道
	 */
	public void setChannel(SocketChannel socketChannel);

	/**
	 * 设置当前缓冲区对应的{@link MessageContext}对象。
	 * 
	 * @param messageContext
	 *            该对象的作用是主要对消息格式化、消息编码解码、以及算法的封装
	 */
	public void setMessageContext(MessageContext messageContext);

	/**
	 * 设置用户实现的事件监听接口对象，缓冲区会将接收到的一条完整数据，通过使用单/多线程分发到
	 * <code>{@link HandleListener}read</code>方法事件中。
	 * 
	 * @param handleListener
	 */
	public void setHandleListener(HandleListener handleListener);

	/**
	 * 获取用户实现的事件监听接口对象
	 * 
	 * @return HandleListener 事件监听接口
	 */
	public HandleListener getHandleListener();

	/**
	 * 设置工作线程池，该线程池的主作作用是分发事件
	 * 
	 * @param WorkTaskThreadPool
	 *            事件工作线程池
	 */
	public void setThreadPool(ThreadPool threadPool);

	/**
	 * 获取当前缓冲区对应的{@link MessageContext}对象。
	 * 
	 * @return MessageContext
	 */
	public MessageContext getMessageContext();

	/**
	 * 获取当前通道对应的{@link SocketChannel}对象
	 * 
	 * @return SocketChannel
	 */
	public SocketChannel getSocketChannel();

	/**
	 * 获取当前的主选择器
	 * 
	 * @return Selector 选择器对象
	 */
	public Selector getSelector();

	/**
	 * 设置当前主选择器
	 * 
	 * @param selector
	 *            选择器对象
	 */
	public void setSelector(Selector selector);

	/**
	 * 判断读状态是否已准备好
	 * 
	 * @return <code>true</code>已准备好
	 */
	public boolean isReadReady();

	/**
	 * 设置读状态是否已准备好
	 * 
	 * @param ready
	 *            <code>true</code>已准备好
	 */
	public void setReadReady(boolean ready);

	/**
	 * 判断写状态是否已准备好
	 * 
	 * @return <code>true</code>已准备好
	 */
	public boolean isWriteReady();

	/**
	 * 设置写状态是否已准备好
	 * 
	 * @param ready
	 *            <code>true</code>已准备好
	 */
	public void setWriteReady(boolean ready);

	/**
	 * 设置当前通道对应的远程客户端的名称
	 * 
	 * @param channelName
	 *            <code>socketChannel.socket().getRemoteSocketAddress()<code>连接端远程地址及端口号
	 */
	public void setChannelName(String channelName);

	/**
	 * 获取当前通道对应的远程客户端的名称
	 * 
	 * @return <code>socketChannel.socket().getRemoteSocketAddress()<code>连接端远程地址及端口号
	 */
	public String getChannelName();

	/**
	 * 获取是否需要认证
	 * <p>
	 * 服务端是否需要认证。客户端应该是比较明确的知道自己该连哪个服务端的地址，而服务端对于客户端的连接身份是需要验证得知的，
	 * 通常服务端的做法是会以验证客户端的IP为主来确认客户端的身份
	 * ，然而当客户端无独立固定IP的情况下，也就是不能通过可靠IP来验证身份，可能服务端对客户端的身份不容易做判断，所以出现了认证的概念。
	 * 因使用的复杂性及加密的性能考虑 framework-nio未使用JDK自带的{@link SSLEngine}
	 * 相关的基于双向认证的API。而是采用了灵活的服务端单向认证客户端的方法， 也就是客户端再{@link HandleListener}
	 * <code>connect</code>方法连接上服务端的时候向服务端发送一段经过证书加密的密文和客户端的数字签名
	 * ，服务端收到后对密文进行解密并调用用户实现的{@link CertificateInterface}
	 * 接口对用户名和密码进行确认以及对签名的合法性进行认证
	 * ，只有认证通过的客户端才会认为是合法的，才能接收和发送数据。未通过认证的客户端会延迟或立即断开连接
	 * 。如果双方对发送和接收数据的敏感性考虑客户端和服务端之间可以协商一个统一的加密方法实现{@link Algorithm}接口，或明文传输提高性能
	 * ， <code>RAS<code>算法每次最多只能加密117个字节避免双向认证多次加密后带来的性能严重降低。
	 * </p>
	 * 
	 * @return <code>true</code>需要认证
	 */
	public boolean isCertificateAuth();

	/**
	 * 设置是否需要认证
	 * <p>
	 * 服务端是否需要认证。客户端应该是比较明确的知道自己该连哪个服务端的地址，而服务端对于客户端的连接身份是需要验证得知的，
	 * 通常服务端的做法是会以验证客户端的IP为主来确认客户端的身份
	 * ，然而当客户端无独立固定IP的情况下，也就是不能通过可靠IP来验证身份，可能服务端对客户端的身份不容易做判断，所以出现了认证的概念。
	 * 因使用的复杂性及加密的性能考虑 framework-nio未使用JDK自带的{@link SSLEngine}
	 * 相关的基于双向认证的API。而是采用了灵活的服务端单向认证客户端的方法， 也就是客户端再{@link HandleListener}
	 * <code>connect</code>方法连接上服务端的时候向服务端发送一段经过证书加密的密文和客户端的数字签名
	 * ，服务端收到后对密文进行解密并调用用户实现的{@link CertificateInterface}
	 * 接口对用户名和密码进行确认以及对签名的合法性进行认证
	 * ，只有认证通过的客户端才会认为是合法的，才能接收和发送数据。未通过认证的客户端会延迟或立即断开连接
	 * 。如果双方对发送和接收数据的敏感性考虑客户端和服务端之间可以协商一个统一的加密方法实现{@link Algorithm}接口，或明文传输提高性能
	 * ， <code>RAS<code>算法每次最多只能加密117个字节避免双向认证多次加密后带来的性能严重降低。
	 * </p>
	 * 
	 * @param certificateAuth
	 *            <code>true</code>需要认证
	 */
	public void setCertificateAuth(boolean certificateAuth);

	/**
	 * 服务端对客户端的认证过程
	 * 
	 * @param methodWorker
	 *            {@link MethodWorker}
	 * 
	 * @return <code>true</code>认证成功
	 * @throws CertificateAuthException
	 *             解密失败或认证信息非法
	 */
	public boolean certificateAuth(MethodWorker methodWorker)
			throws CertificateAuthException;

	/**
	 * 设置CA证书的基本信息
	 * 
	 * @param certificateModel
	 *            {@link CertificateModel}
	 */
	public void setCertificateModel(CertificateModel certificateModel);

	/**
	 * 设置用户自定义实现的认证接口
	 * 
	 * @param certificateInterface
	 *            认证接口实现类
	 */
	public void setCertificateInterface(
			CertificateInterface certificateInterface);

	/**
	 * 是否已经认证过
	 * 
	 * @return <code>true</code>已经认证过
	 */
	public boolean isCertificateAuthed();
}
