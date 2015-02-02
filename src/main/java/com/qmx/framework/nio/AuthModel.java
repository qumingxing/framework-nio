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
 * 简单的认证实体，在服务端需要认证的时候需要客户端在connect的时候将用户名和密码，连同加密信息及签名传到服务端，
 * 当服务端解密成功并且签名正确的情况下，如果用户设置了{@link CertificateInterface}的实现，会将用户名和密码包装成
 * {@link AuthModel} 对象调用{@link CertificateInterface}的
 * <code>certificateUser</code>方法。该方法返回一个布尔值，只有用户返回<code>true</code>的情况
 * 下框架内容才会认为整个认证过程全部成功。否则会认为该通道非法断开该通道。
 * 
 * @author qmx 2015-1-9 上午10:05:14
 * 
 */
public class AuthModel
{
	/**
	 * <code>{userName:qmx,passwd:123456,sign:s4a54DXFD54EF12SFS}</code> <br/>
	 * 用户名
	 */
	private String userName;
	/**
	 * 密码
	 */
	private String passwd;

	/**
	 * 获取客户端传过来的用户名
	 * 
	 * @return 用户名
	 */
	public String getUserName()
	{
		return userName;
	}

	/**
	 * 设置客户端传过来的用户名
	 * 
	 * @param userName
	 *            用户名
	 */
	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	/**
	 * 获取客户端传过来的密码
	 * 
	 * @return 密码
	 */
	public String getPasswd()
	{
		return passwd;
	}

	/**
	 * 设置客户端传过来的密码
	 * 
	 * @param passwd
	 *            密码
	 */
	public void setPasswd(String passwd)
	{
		this.passwd = passwd;
	}

}
