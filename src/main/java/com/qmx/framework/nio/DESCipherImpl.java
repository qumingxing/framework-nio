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
 * 默认的对称加密实现
 * 
 * @author qmx 2014-11-26 上午10:44:09
 * 
 */
public class DESCipherImpl implements Cipher
{
	/**
	 * 私钥
	 */
	private String key;

	public DESCipherImpl(String key)
	{
		this.key = key;
	}

	@Override
	public byte[] encrypt(byte[] source) throws CipherException
	{
		// TODO Auto-generated method stub
		if (null == source)
			throw new CipherException("加密数据为NULL");
		return DESEncrypt.getEncString(new String(source), key).getBytes();
	}

	@Override
	public byte[] decipher(byte[] source) throws CipherException
	{
		// TODO Auto-generated method stub
		if (null == source)
			throw new CipherException("解密数据为NULL");
		return DESEncrypt.getDesString(new String(source), key).getBytes();
	}

}
