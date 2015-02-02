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
 * CA证书的基本信息
 * 
 * @author qmx 2015-1-9 上午10:10:07
 * 
 */
public class CertificateModel
{
	/**
	 * 证书密码
	 */
	private String password;
	/**
	 * 别名
	 */
	private String alias;
	/**
	 * 公钥路径
	 */
	private String certificatePath;
	/**
	 * 私钥路径
	 */
	private String keyStorePath;

	/**
	 * 获取证书密码
	 * 
	 * @return 证书密码
	 */
	public String getPassword()
	{
		return password;
	}

	/**
	 * 设置证书密码
	 * 
	 * @param password
	 *            证书密码
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	/**
	 * 获取证书别名
	 * 
	 * @return 证书别名
	 */
	public String getAlias()
	{
		return alias;
	}

	/**
	 * 设置证书别名
	 * 
	 * @param alias
	 *            证书别名
	 */
	public void setAlias(String alias)
	{
		this.alias = alias;
	}

	/**
	 * 获取公钥地址
	 * 
	 * @return 公钥地址
	 */
	public String getCertificatePath()
	{
		return certificatePath;
	}

	/**
	 * 设置公钥地址
	 * 
	 * @param certificatePath
	 *            公钥地址
	 */
	public void setCertificatePath(String certificatePath)
	{
		this.certificatePath = certificatePath;
	}

	/**
	 * 获取私钥地址
	 * 
	 * @return 私钥地址
	 */
	public String getKeyStorePath()
	{
		return keyStorePath;
	}

	/**
	 * 设置私钥地址
	 * 
	 * @param keyStorePath
	 *            私钥地址
	 */
	public void setKeyStorePath(String keyStorePath)
	{
		this.keyStorePath = keyStorePath;
	}

}
