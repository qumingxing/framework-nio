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

import java.nio.channels.SocketChannel;
import javax.net.ssl.SSLEngine;

/**
 * 通道核心接口，维护通道的{@link SocketChannel}通信对象，通道各种状态信息
 * 
 * @author qmx 2014-11-28 下午1:19:05
 * 
 */
public interface Channel
{
	/**
	 * 返回当前通道的原始{@link SocketChannel}对象
	 * 
	 * @return {@link SocketChannel}对象
	 */
	public SocketChannel getChannel();

	/**
	 * 设置当前通道的原始{@link SocketChannel}对象
	 * 
	 * @param channel
	 *            {@link SocketChannel}对象
	 */
	public void setChannel(SocketChannel channel);

	/**
	 * 设置当前通道的别名，在框架内维护。
	 * 
	 * @param channelName
	 *            <code>socketChannel.socket().getRemoteSocketAddress()<code>连接端远程地址及端口号
	 */
	public void setChannelName(String channelName);

	/**
	 * 获取当前通道的别名，在框架内维护。
	 * 
	 * @return <code>socketChannel.socket().getRemoteSocketAddress()<code>连接端远程地址及端口号
	 */
	public String getChannelName();

	/**
	 * 当前通道是否已经认证过
	 * 
	 * @return <code>true</code>已经认证过
	 */
	public boolean isCertificateAuthed();

	/**
	 * 设置 当前通道是否已经认证过
	 * 
	 * @param certificateAuthed
	 *            <code>true</code>已经认证过
	 */
	public void setCertificateAuthed(boolean certificateAuthed);

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
	 * 设置服务端接受时间(accept)，该时间 的作用是根据给定的最大等待认证时间定时检查客户端的有效性，服务端必须要启用认证功能，否则该方法不会生效
	 * {@link Channels}<code>startScheduledCheck</code>
	 * 
	 * @param date
	 *            接受时间
	 */
	public void setAcceptDate(long date);

	/**
	 * 获服务端接受时间(accept)，该时间 的作用是根据给定的最大等待认证时间定时检查客户端的有效性，服务端必须要启用认证功能，否则该方法不会生效
	 * {@link Channels}<code>startScheduledCheck</code>
	 * 
	 * @return 接受时间
	 */
	public long getAcceptDate();
}
