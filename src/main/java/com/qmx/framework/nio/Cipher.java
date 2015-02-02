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
 * 加密解密算法<b>RSA</b>、<b>DESC</b>
 * 
 * @author qmx 2014-11-26 上午10:40:49
 * 
 */
public interface Cipher
{
	/**
	 * 加密
	 * 
	 * @param source
	 *            待加密内容的字节数组
	 * @return 加密后的字节数组
	 * @throws CipherException
	 *             加密失败
	 */
	public byte[] encrypt(byte[] source) throws CipherException;

	/**
	 * 解密
	 * 
	 * @param source
	 *            待解密内容的字节数组
	 * @return 解密后的字节数组
	 * @throws CipherException
	 *             解密失败
	 */
	public byte[] decipher(byte[] source) throws CipherException;
}
