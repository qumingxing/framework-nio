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
 * 未加数字签名代码只实现了(公私钥)加密解密过程<br>
 * 数字签名原理A和B各自将公钥交给对方，并保留私钥<br>
 * A使用B提供的公钥加密并使用自己的私钥签名，B使用A提供的公钥验证签名，验证通过后再使用B的私钥解密，B与A一样
 * 
 * @author qmx 2014-11-26 上午10:59:51
 * 
 */
public class RSACipherImpl implements Cipher
{
	/**
	 * 公钥加密OR私钥加密
	 */
	private int encryptType;
	/**
	 * 公钥解密OR私钥解密
	 */
	private int decipherType;
	/**
	 * 私钥密码
	 */
	private String password;
	/**
	 * 证书别名
	 */
	private String alias;
	/**
	 * 证书位置
	 */
	private String certificatePath;
	/**
	 * 密钥库位置
	 */
	private String keyStorePath;

	public RSACipherImpl(int encryptType, int decipherType, String alias,
			String password, String certificatePath, String keyStorePath)
	{
		this.encryptType = encryptType;
		this.decipherType = decipherType;
		this.password = password;
		this.alias = alias;
		this.certificatePath = certificatePath;
		this.keyStorePath = keyStorePath;
	}

	@Override
	public byte[] encrypt(byte[] source) throws CipherException
	{
		try
		{
			byte[] encrypt = null;
			if (encryptType == RSAType.PUBLICKEY_ENCRYPT.getValue())
			{
				encrypt = CertificateCoder.encryptByPublicKey(source,
						certificatePath);
			} else if (encryptType == RSAType.PRIVATEKEY_ENCRYPT.getValue())
			{
				encrypt = CertificateCoder.encryptByPrivateKey(source,
						keyStorePath, alias, password);
			}
			return encrypt;
		} catch (Exception e)
		{
			throw new CipherException(e);
		}
	}

	@Override
	public byte[] decipher(byte[] source) throws CipherException
	{
		// TODO Auto-generated method stub
		try
		{
			byte[] decrypt = null;
			if (decipherType == RSAType.PUBLICKEY_DECIPHER.getValue())
			{
				decrypt = CertificateCoder.decryptByPublicKey(source,
						certificatePath);
			} else if (decipherType == RSAType.PRIVATEKEY_DECIPHER.getValue())
			{
				decrypt = CertificateCoder.decryptByPrivateKey(source,
						keyStorePath, alias, password);
			}
			return decrypt;
		} catch (Exception e)
		{
			throw new CipherException(e);
		}
	}

	public static void main(String[] args) throws CipherException
	{
		String sss = "1111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111";//max 117
		RSACipherImpl rasCipherImpl = new RSACipherImpl(
				RSAType.PRIVATEKEY_ENCRYPT.getValue(),
				RSAType.PUBLICKEY_DECIPHER.getValue(), "test", "123456",
				"D:\\soft\\Java\\bin\\test.cer",
				"D:\\soft\\Java\\bin\\test.keystore");
		byte[] en = rasCipherImpl.encrypt(sss.getBytes());
		System.out.println(new String(en));
		byte []de = rasCipherImpl.decipher(en);
		System.out.println(new String(de));
	}
}
