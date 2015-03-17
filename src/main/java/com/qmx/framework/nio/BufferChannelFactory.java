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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import javax.net.ssl.SSLEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * 当每个个通道打开后该工厂都会创建一个根据用户指定的{@link ChannelBuffer}
 * 的实现，维护每个通道新增、删除、获取。设置每个通道所需要的可选参数。
 * </p>
 * 
 * @author qmx 2014-12-11 上午9:35:20
 * 
 */
public class BufferChannelFactory
{
	/**
	 * 维护所有{@link ChannelBuffer} 通道的<code>Map</code>集合 该集合的键由
	 * <code>socketChannel.socket().getRemoteSocketAddress()<code>连接端远程地址及端口号构成
	 */
	private Map<String, ChannelBuffer> channelBuffers = new ConcurrentHashMap<String, ChannelBuffer>();
	/**
	 * 单例的通道创建工厂
	 */
	private final static BufferChannelFactory BUFER_CHANNEL_FACTORY = new BufferChannelFactory();
	private static final Logger logger = LoggerFactory
			.getLogger(BufferChannelFactory.class);
	/**
	 * 可选参数是否需要认证，如果设置了需要认证会将该参数传递到{@link ChannelBuffer}的实现内部
	 */
	private boolean certificateAuth;
	/**
	 * 可选参数认证模型，封装了证书的基本信息，如果设置了认证会将该参数传递到{@link ChannelBuffer}的实现内部
	 */
	private CertificateModel certificateModel;
	/**
	 * 可选参数用户自定义实现的基于用户名密码的认证接口，主要对认证过程中用户自定义的用户名和密码简单验证
	 * 如果设置了认证并且该对象不为空的时候，会将该参数传递到{@link ChannelBuffer}
	 * 的实现内部。框架内部会在认证结束后回调该接口中的方法
	 */
	private CertificateInterface certificateInterface;
	/**
	 * 心跳机制
	 */
	private HeartCheck heartCheck;
	/**
	 * 客户端服务端模式
	 */
	private PointModel pointModel;

	private BufferChannelFactory()
	{

	}

	/**
	 * 获取唯一的{@link BufferChannelFactory}工厂
	 * 
	 * @return 缓冲区创建工厂
	 */
	public static BufferChannelFactory getBuferChannelFactory()
	{
		return BUFER_CHANNEL_FACTORY;
	}

	/**
	 * 移除一个{@link ChannelBuffer}。当客户端或服务端断开后会删除该缓冲区实现释放资源
	 * 
	 * @param socketChannel
	 *            缓冲区对应的通道
	 * @return 返回一个空的或已移出成功的缓冲区对象
	 */
	public ChannelBuffer removeBuffer(String channelName)
	{
		ChannelBuffer channelBuffer = channelBuffers.remove(channelName);
		if (null != channelBuffer)
		{
			channelBuffer.clearBytes();
			logger.info("remove channelBuffer success" + channelName);
			return channelBuffer;
		}
		return null;
	}

	/**
	 * 移除所有元素，返回所有元素的值列表。主要针对客户端使用，因为当服务端断开后{@link SocketChannel}客户端是无法取到
	 * <code>getRemoteSocketAddress</code> 值，导致removeBuffer方法
	 * {@link ChannelBuffer}无法删除，使用已被关闭的 {@link Selector}
	 * 对象报错。当客户端或服务端断开后会删除该缓冲区实现释放资源
	 * 
	 * 
	 * @return 返回一个空的或所有移出成功的缓冲区对象
	 */
	public List<ChannelBuffer> removeAllBuffer()
	{
		Iterator<Entry<String, ChannelBuffer>> allBuffers = channelBuffers
				.entrySet().iterator();
		List<ChannelBuffer> channelBuffersList = new ArrayList<ChannelBuffer>();
		while (allBuffers.hasNext())
		{
			Entry<String, ChannelBuffer> singleBuffer = allBuffers.next();
			ChannelBuffer channelBuffer = singleBuffer.getValue();
			channelBuffer.clearBytes();
			logger.info("remove channelBuffer success" + singleBuffer.getKey());
			channelBuffersList.add(channelBuffer);
			allBuffers.remove();
		}
		return channelBuffersList;
	}

	/**
	 * 当客户端或服务端连接上或接受一个连接或触发其它选择器键的时候会获取当前通道对应的{@link ChannelBuffer}缓冲区，
	 * 如果该缓冲区未创建那么根据<code>Class<? extends ChannelBuffer> bufferType</code>
	 * 创建一个新的缓冲区。如果缓冲区存在那么返回该缓冲区。
	 * 
	 * @param socketChannel
	 *            缓冲区对应的通道
	 * @param messageContext
	 *            {@link MessageContext}
	 * @param handleListener
	 *            {@link HandleListener}
	 * @param threadPool
	 *            工作线程池
	 * @param selector
	 *            主选择器
	 * @param bufferType
	 *            缓冲区类型
	 * @return 返回一个新的或已存在的缓冲区
	 */
	public ChannelBuffer getBuffer(SocketChannel socketChannel,
			MessageContext messageContext, HandleListener handleListener,
			ThreadPool threadPool, Selector selector,
			Class<? extends ChannelBuffer> bufferType)
	{
		if (null == socketChannel)
		{
			ChannelBuffer channelBuffer = new DefaultChannelBuffer();
			channelBuffer.setHandleListener(handleListener);
			channelBuffer.setMessageContext(messageContext);
			return channelBuffer;
		}
		String clientSign = socketChannel.socket().getRemoteSocketAddress()
				.toString();
		ChannelBuffer channelBuffer = channelBuffers.get(clientSign);
		if (null == channelBuffer)
		{
			ChannelBuffer newBuffer = null;
			if (bufferType == LengthSplitChannelBuffer.class)
			{
				newBuffer = new LengthSplitChannelBuffer();
			} else if (bufferType == HybridLengthSplitChannelBuffer.class)
			{
				newBuffer = new HybridLengthSplitChannelBuffer();
			} else if (bufferType == ComplexSplitChannelBuffer.class)
			{
				newBuffer = new ComplexSplitChannelBuffer();
			} else
			{
				newBuffer = new DefaultChannelBuffer();
			}
			newBuffer.setCertificateAuth(certificateAuth);
			newBuffer.setCertificateModel(certificateModel);
			newBuffer.setCertificateInterface(certificateInterface);
			newBuffer.setChannel(socketChannel);
			newBuffer.setMessageContext(messageContext);
			newBuffer.setHandleListener(handleListener);
			newBuffer.setThreadPool(threadPool);
			newBuffer.setSelector(selector);
			newBuffer.setHeartCheck(heartCheck);
			newBuffer.setPointModel(pointModel);
			channelBuffers.put(clientSign, newBuffer);
			return newBuffer;
		}
		return channelBuffer;
	}

	/**
	 * 根据通道的唯一标识获取一个通道上的{@link ChannelBuffer}对象
	 * 
	 * @param socketChannel
	 *            通道对象
	 * @return {@link ChannelBuffer}
	 */
	public ChannelBuffer getBuffer(SocketChannel socketChannel)
	{
		String clientSign = socketChannel.socket().getRemoteSocketAddress()
				.toString();
		ChannelBuffer channelBuffer = channelBuffers.get(clientSign);
		if (null != channelBuffer)
		{
			return channelBuffer;
		}
		return null;
	}

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
	public boolean isCertificateAuth()
	{
		return certificateAuth;
	}

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
	public void setCertificateAuth(boolean certificateAuth)
	{
		this.certificateAuth = certificateAuth;
	}

	/**
	 * 设置CA证书的基本信息
	 * 
	 * @param certificateModel
	 *            {@link CertificateModel}
	 */
	public void setCertificateModel(CertificateModel certificateModel)
	{
		this.certificateModel = certificateModel;
	}

	/**
	 * 设置用户自定义实现的认证接口
	 * 
	 * @param certificateInterface
	 *            认证接口实现类
	 */
	public void setCertificateInterface(
			CertificateInterface certificateInterface)
	{
		this.certificateInterface = certificateInterface;
	}

	/**
	 * 获取心跳配置对象
	 * 
	 * @return {@link HeartCheck}
	 */
	public HeartCheck getHeartCheck()
	{
		return heartCheck;
	}

	/**
	 * 设置心跳配置对象
	 * 
	 * @param heartCheck
	 *            {@link HeartCheck}
	 */
	public void setHeartCheck(HeartCheck heartCheck)
	{
		this.heartCheck = heartCheck;
	}

	/**
	 * 客户端、服务端模式设置
	 * 
	 * @param pointModel
	 *            {@link PointModel}
	 */
	public void setPointModel(PointModel pointModel)
	{
		this.pointModel = pointModel;
	}

}
