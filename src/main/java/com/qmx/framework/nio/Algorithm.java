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
 * 该接口封装了一些可能用于传输过程中常见(编码、解码、压缩、解压缩)算法的方法，框架内部主要通过调用 {@link Algorithm}的
 * <code>getEncodeResult</code>和<code>getDecodeResult</code>
 * 来获取最终的结果，所以其它方法用户可以自由参考实现，框架内部提供的{@link DefaultAlgorithmImpl}
 * 的实现未对消息做认证处理直接返回原始字节数组。
 * 
 * @author qmx 2014-11-27 上午11:38:26
 * 
 * @param
 */
public interface Algorithm
{
	/**
	 * 编码
	 * 
	 * @param byt
	 *            待编码的字节数组
	 * @return 编码后的字节数组
	 */
	public byte[] encode(byte[] byt);

	/**
	 * 解码
	 * 
	 * @param byt
	 *            待解码的字节数组
	 * @return 解码后的字节数组
	 */
	public byte[] decode(byte[] byt);

	/**
	 * 压缩
	 * 
	 * @param byt
	 *            待压缩的字节数组
	 * @return 压缩后的字节数组
	 */
	public byte[] compress(byte[] byt);

	/**
	 * 解压缩
	 * 
	 * @param byt
	 *            待解压缩的字节数组
	 * @return 解压缩后的字节数组
	 */
	public byte[] decompress(byte[] byt);

	/**
	 * 解密
	 * 
	 * @param byt
	 *            待解密的字节数组
	 * @return 解密后的字节数组
	 */
	public byte[] decipher(byte[] byt) throws CipherException;

	/**
	 * 加密
	 * 
	 * @param byt
	 *            待加密的字节数组
	 * @return 加密后的字节数组
	 */
	public byte[] encrypt(byte[] byt) throws CipherException;

	/**
	 * 框架API只会调用<code>getEncodeResult</code>和<code>getDecodeResult</code>
	 * 方法，这两个方法的实现必须对称如：编码时<code>encode(),compress</code>，那么解码时就要
	 * <code>decompress,decode</code>分别对应编码和解码，其它方法都是提供用户自己参考可选择的是否要实现的方法
	 * 
	 * @param byt
	 *            原始的未经编码的字节数组
	 * @return 一系列的方法调用之后返回的字节数组
	 */
	public byte[] getEncodeResult(byte[] byt);

	/**
	 * 框架API只会调用<code>getEncodeResult</code>和<code>getDecodeResult</code>
	 * 方法，这两个方法的实现必须对称如：编码时<code>encode(),compress</code>，那么解码时就要
	 * <code>decompress,decode</code>分别对应编码和解码，其它方法都是提供用户自己参考可选择的是否要实现的方法
	 * 
	 * @param byt
	 *            原始的未经解码的字节数组
	 * @return 一系列的方法调用之后返回的字节数组
	 */
	public byte[] getDecodeResult(byte[] byt);
}
