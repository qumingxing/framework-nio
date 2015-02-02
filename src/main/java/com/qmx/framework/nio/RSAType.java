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
 * <code>RSA</code>双向认证方式
 * 
 * @author qmx 2015-1-15 下午5:09:36
 * 
 */
public enum RSAType
{
	PUBLICKEY_ENCRYPT(1), PUBLICKEY_DECIPHER(2), PRIVATEKEY_ENCRYPT(3), PRIVATEKEY_DECIPHER(
			4);
	private int value;

	private RSAType(int value)
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}
}
