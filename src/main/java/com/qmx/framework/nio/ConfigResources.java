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

import javax.net.ssl.SSLEngine;

/**
 * 客户端或服务端公用的配置信息
 * 
 * @author qmx 2014-12-19 上午11:27:16
 * 
 */
public class ConfigResources
{
	/**
	 * IP地址
	 */
	private String ip;
	/**
	 * 服务端端口
	 */
	private int port;
	/**
	 * 连接模式(客户端/服务端)
	 */
	private PointModel pointModel;
	/**
	 * 是否需要认证(服务端)
	 */
	private boolean certificateAuth;
	/**
	 * 连接对象(客户端/服务端)
	 */
	private AbstractConnection connection;
	/**
	 * 认证模型
	 */
	private CertificateModel certificateModel;
	/**
	 * 用户自定义实现简单的用户名密码校验
	 */
	private CertificateInterface certificateInterface;

	/**
	 * 获取服务端IP地址
	 * 
	 * @return 服务端IP地址
	 */
	public String getIp()
	{
		return ip;
	}

	/**
	 * 设置服务端IP地址
	 * 
	 * @param ip
	 *            服务端IP地址
	 */
	public void setIp(String ip)
	{
		this.ip = ip;
	}

	/**
	 * 获取服务端端口号
	 * 
	 * @return 服务端端口号
	 */
	public int getPort()
	{
		return port;
	}

	/**
	 * 设置服务端端口号
	 * 
	 * @param port
	 *            服务端端口号
	 */
	public void setPort(int port)
	{
		this.port = port;
	}

	/**
	 * 获取客户端/服务端模式枚举
	 * 
	 * @return 返回客户端/服务端模式枚举
	 */
	public PointModel getPointModel()
	{
		return pointModel;
	}

	/**
	 * 设置客户端/服务端模式枚举
	 * 
	 * @param pointModel
	 *            客户端/服务端模式枚举对象
	 */
	public void setPointModel(PointModel pointModel)
	{
		this.pointModel = pointModel;
	}

	/**
	 * 获取客户端/服务端连接对象
	 * 
	 * @return 返回客户端/服务端连接对象
	 */
	public AbstractConnection getConnection()
	{
		return connection;
	}

	/**
	 * 设置客户端/服务端连接对象
	 * 
	 * @param connection
	 *            客户端/服务端连接对象
	 */
	public void setConnection(AbstractConnection connection)
	{
		this.connection = connection;
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
	 * 获取CA证书的基本信息
	 * 
	 * @return CA证书的基本信息
	 */
	public CertificateModel getCertificateModel()
	{
		return certificateModel;
	}

	/**
	 * 设置CA证书的基本信息
	 * 
	 * @param certificateModel
	 *            CA证书的基本信息
	 */
	public void setCertificateModel(CertificateModel certificateModel)
	{
		this.certificateModel = certificateModel;
	}

	/**
	 * 获取用户自定义实现的认证接口
	 * 
	 * @return 认证接口
	 */
	public CertificateInterface getCertificateInterface()
	{
		return certificateInterface;
	}

	/**
	 * 设置用户自定义实现的认证接口
	 * 
	 * @param certificateInterface
	 *            认证接口
	 */
	public void setCertificateInterface(
			CertificateInterface certificateInterface)
	{
		this.certificateInterface = certificateInterface;
	}

}
