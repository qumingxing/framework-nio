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
 * 简单包装客户端向服务端发送认证信息时的用户名和密码
 * 
 * @author qmx 2015-1-9 上午11:33:13
 * 
 */
public class ClientCertificateUtil
{
	/**
	 * 证书密码
	 */
	private String password;
	/**
	 * 证书别名
	 */
	private String alias;
	/**
	 * 私钥地址
	 */
	private String keyStorePath;

	/**
	 * 创建一个新的客户端认证消息组装工具类
	 * 
	 * @param keyStorePath
	 *            私钥地址
	 * @param alias
	 *            别名
	 * @param password
	 *            密码
	 */
	public ClientCertificateUtil(String keyStorePath, String alias,
			String password)
	{
		this.password = password;
		this.alias = alias;
		this.keyStorePath = keyStorePath;
	}

	/**
	 * 返回一条经过组装的认证消息内容RSA加密字符串长度不能大于117否则会报错
	 * 
	 * @param userName
	 *            用户名
	 * @param pwd
	 *            密码
	 * @return 组装的认证消息内容
	 */
	public String getCert(String userName, String pwd)
	{
		String passText = userName + "&" + pwd;
		byte[] data = passText.getBytes();
		if (data.length > 117)
		{
			return null;
		}
		StringBuilder builder = new StringBuilder();
		try
		{
			byte[] encodedData = CertificateCoder.encryptByPrivateKey(data,
					getKeyStorePath(), getAlias(), getPassword());
			String sign = CertificateCoder.sign(encodedData, getKeyStorePath(),
					getAlias(), getPassword());
			builder.append(Base64.encode(encodedData)).append("&").append(sign);
			return builder.toString();
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 返回密码
	 * 
	 * @return 返回密码
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * 返回别名
	 * 
	 * @return 返回别名
	 */
	public String getAlias()
	{
		return alias;
	}

	/**
	 * 返回私钥地址
	 * 
	 * @return 私钥地址
	 */
	public String getKeyStorePath()
	{
		return keyStorePath;
	}

}
