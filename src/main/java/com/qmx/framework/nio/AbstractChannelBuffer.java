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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 该抽象类主要是对每个通道创建的{@link ChannelBuffer}中共享部分的抽取
 * 
 * @author qmx 2014-12-11 上午10:05:41
 * 
 */
public abstract class AbstractChannelBuffer extends AbstractHeartChannelBuffer implements ChannelBuffer
{
	/**
	 * 当前{@link ChannelBuffer}下的{@link SocketChannel}对象
	 */
	private SocketChannel socketChannel;
	/**
	 * {@link MessageContext}主要对消息格式化、消息编码解码、以及算法的封装
	 */
	private MessageContext messageContext;
	/**
	 * {@link HandleListener}提供客户端实现，对消息传递中重要方法回调
	 */
	private HandleListener handleListener;
	/**
	 * {@link Selector} 主选择器
	 */
	private Selector selector;
	/**
	 * 设置读状态是否准备好
	 */
	private boolean readReady;
	/**
	 * 设置写状态是否已经准备好
	 */
	private boolean writeReady;
	/**
	 * 当前通道的别名，在框架内维护，该名称来源于{@link SocketChannel} 的
	 * <code>socketChannel.socket().getRemoteSocketAddress()<code>连接端远程地址及端口号
	 */
	private String channelName;
	/**
	 * 服务端是否需要对客户端的身份进行认证
	 */
	private boolean certificateAuth;
	/**
	 * 服务端是否对客户端的身份已经认证过
	 */
	private boolean isCertificateAuthed;
	/**
	 * {@link CertificateModel} 认证模型主要维护CA证书的基础信息配置
	 */
	private CertificateModel certificateModel;
	/**
	 * {@link CertificateInterface} 提供给客户端实现，主要对认证过程中用户自定义的用户名和密码简单验证
	 */
	private CertificateInterface certificateInterface;
	/**
	 * 内部使用表示该消息是否是认证信息
	 */
	private boolean authMark;
	private final static Logger log = LoggerFactory
			.getLogger(AbstractChannelBuffer.class);
	

	@Override
	public void setChannel(SocketChannel socketChannel)
	{
		// TODO Auto-generated method stub
		this.socketChannel = socketChannel;
		setChannelName(this.socketChannel.socket().getRemoteSocketAddress()
				.toString());
	}

	@Override
	public void setMessageContext(MessageContext messageContext)
	{
		// TODO Auto-generated method stub
		this.messageContext = messageContext;
	}

	@Override
	public void setHandleListener(HandleListener handleListener)
	{
		// TODO Auto-generated method stub
		this.handleListener = handleListener;
	}

	@Override
	public HandleListener getHandleListener()
	{
		// TODO Auto-generated method stub
		return this.handleListener;
	}

	public MessageContext getMessageContext()
	{
		return messageContext;
	}

	public SocketChannel getSocketChannel()
	{
		return socketChannel;
	}

	public void setSocketChannel(SocketChannel socketChannel)
	{
		this.socketChannel = socketChannel;
	}

	public Selector getSelector()
	{
		return selector;
	}

	public void setSelector(Selector selector)
	{
		this.selector = selector;
	}

	@Override
	public boolean isReadReady()
	{
		// TODO Auto-generated method stub
		return readReady;
	}

	@Override
	public void setReadReady(boolean ready)
	{
		// TODO Auto-generated method stub
		this.readReady = ready;
	}

	@Override
	public boolean isWriteReady()
	{
		// TODO Auto-generated method stub
		return writeReady;
	}

	@Override
	public void setWriteReady(boolean ready)
	{
		// TODO Auto-generated method stub
		this.writeReady = ready;
	}

	@Override
	public void setChannelName(String channelName)
	{
		// TODO Auto-generated method stub
		this.channelName = channelName;
	}

	@Override
	public String getChannelName()
	{
		// TODO Auto-generated method stub
		return this.channelName;
	}

	@Override
	public boolean isCertificateAuth()
	{
		// TODO Auto-generated method stub
		return this.certificateAuth;
	}

	public void setCertificateAuth(boolean certificateAuth)
	{
		this.certificateAuth = certificateAuth;
	}

	public boolean certificateAuth(MethodWorker methodWorker)
			throws CertificateAuthException
	{
		if (this.certificateAuth)
		{
			if (!this.isCertificateAuthed)
			{
				// 结构 base64(加密(username&pwd))&sign
				try
				{
					String allString = decode(methodWorker);
					String[] authArr = allString.split("\\&");
					byte[] userPwdByte = Base64.decode(authArr[0]);
					String sign = authArr[1];
					if (userPwdByte.length != 128)
					{
						return false;
					}
					byte[] decodedData = CertificateCoder.decryptByPublicKey(
							userPwdByte, certificateModel.getCertificatePath());
					boolean verify = CertificateCoder.verify(userPwdByte, sign,
							certificateModel.getCertificatePath());
					if (!verify)
						return false;
					isCertificateAuthed = true;
					String[] user = new String(decodedData).split("\\&");
					if (null != certificateInterface)
					{
						AuthModel authModel = new AuthModel();
						authModel.setUserName(user[0]);
						authModel.setPasswd(user[1]);
						isCertificateAuthed = certificateInterface
								.certificateUser(authModel);
					}
					Channels.channelAuthResult(socketChannel,
							isCertificateAuthed);
					authMark = true;
					log.info("身份认证:{},{}", getChannelName(),
							isCertificateAuthed == true ? "成功" : "失败");
					return isCertificateAuthed;
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					log.info("身份认证:{},{}", getChannelName(), "失败");
					throw new CertificateAuthException("身份认证失败"
							+ getChannelName());
				}
			} else
				authMark = false;
		}
		return true;
	}

	@Override
	public void setCertificateModel(CertificateModel certificateModel)
	{
		// TODO Auto-generated method stub
		this.certificateModel = certificateModel;
	}

	@Override
	public void setCertificateInterface(
			CertificateInterface certificateInterface)
	{
		// TODO Auto-generated method stub
		this.certificateInterface = certificateInterface;
	}

	@Override
	public boolean isCertificateAuthed()
	{
		// TODO Auto-generated method stub
		return isCertificateAuthed;
	}

	/**
	 * 是认证信息要跳过循环
	 * 
	 * @return
	 */
	public boolean isAuthMark()
	{
		return authMark;
	}

	/**
	 * 对{@link EncoderAndDecoder}中的<code>encode</code>方法信息解码
	 * 
	 * @see Algorithm
	 * @param methodWorker
	 * @return Base64加密信息+数字签名
	 */
	private String decode(MethodWorker methodWorker)
	{
		MessageEvent event = methodWorker.newMessageEvent();
		if (methodWorker.getDataType() == DataType.BYTE)
		{
			return new String((byte[]) event.getMessage());
		}
		return event.getMessage().toString();
	}
}
