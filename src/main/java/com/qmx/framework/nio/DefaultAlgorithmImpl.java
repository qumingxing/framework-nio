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
 * 默认框架提供的算法简单实现，用户可自己扩展{@link Algorithm}接口
 * 
 * @author qmx 2014-11-27 上午11:43:22
 * 
 */
public class DefaultAlgorithmImpl implements Algorithm
{
	/**
	 * 加密算法
	 */
	private Cipher cip;

	public Cipher getCip()
	{
		return cip;
	}

	public void setCip(Cipher cip)
	{
		this.cip = cip;
	}

	@Override
	public byte[] encode(byte[] byt)
	{
		// TODO Auto-generated method stub
		byt = Base64.encode(byt).getBytes();
		return byt;
	}

	@Override
	public byte[] decode(byte[] byt)
	{
		// TODO Auto-generated method stub
		byt = Base64.decode(new String(byt));
		return byt;
	}

	@Override
	public byte[] compress(byte[] byt)
	{
		// TODO Auto-generated method stub
		byt = Zlib.compress(byt);
		return byt;
	}

	@Override
	public byte[] decompress(byte[] byt)
	{
		// TODO Auto-generated method stub
		byt = Zlib.decompress(byt);
		return byt;
	}

	@Override
	public byte[] decipher(byte[] byt) throws CipherException
	{
		// TODO Auto-generated method stub
		byt = cip.decipher(byt);
		return byt;
	}

	@Override
	public byte[] encrypt(byte[] byt) throws CipherException
	{
		// TODO Auto-generated method stub
		byt = cip.encrypt(byt);
		return byt;
	}

	@Override
	public byte[] getEncodeResult(byte[] byt)
	{
		// TODO Auto-generated method stub
		// byt = compress(byt);
		// byt = encode(byt);
		return byt;
	}

	@Override
	public byte[] getDecodeResult(byte[] byt)
	{
		// TODO Auto-generated method stub
		// byt = decode(byt);
		// byt = decompress(byt);
		return byt;
	}
}
