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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * 压缩、解压缩算法
 * 
 * @author qmx 2013-03-12 下午1:48:02
 * 
 */
public class Zlib
{
	/**
	 * 压缩
	 * 
	 * @param data
	 *            待压缩数据
	 * @return byte[] 压缩后的数据
	 */
	public static byte[] compress(byte[] data)
	{
		byte[] output = new byte[0];
		Deflater compresser = new Deflater();

		compresser.reset();
		compresser.setInput(data);
		compresser.finish();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
		try
		{
			byte[] buf = new byte[1024];
			while (!compresser.finished())
			{
				int i = compresser.deflate(buf);
				bos.write(buf, 0, i);
			}
			output = bos.toByteArray();
		} catch (Exception e)
		{
			output = data;
			e.printStackTrace();
		} finally
		{
			try
			{
				bos.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		compresser.end();
		return output;
	}

	/**
	 * 压缩
	 * 
	 * @param data
	 *            待压缩数据
	 * 
	 * @param os
	 *            输出流
	 */
	public static void compress(byte[] data, OutputStream os)
	{
		DeflaterOutputStream dos = new DeflaterOutputStream(os);

		try
		{
			dos.write(data, 0, data.length);

			dos.finish();

			dos.flush();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 解压缩
	 * 
	 * @param data
	 *            待压缩的数据
	 * @return byte[] 解压缩后的数据
	 */
	public static byte[] decompress(byte[] data)
	{
		byte[] output = new byte[0];

		Inflater decompresser = new Inflater();
		decompresser.reset();
		decompresser.setInput(data);

		ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
		try
		{
			byte[] buf = new byte[1024];
			while (!decompresser.finished())
			{
				int i = decompresser.inflate(buf);
				o.write(buf, 0, i);
			}
			output = o.toByteArray();
		} catch (Exception e)
		{
			output = data;
			e.printStackTrace();
		} finally
		{
			try
			{
				o.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		decompresser.end();
		return output;
	}

	/**
	 * 解压缩
	 * 
	 * @param is
	 *            输入流
	 * @return byte[] 解压缩后的数据
	 */
	public static byte[] decompress(InputStream is)
	{
		InflaterInputStream iis = new InflaterInputStream(is);
		ByteArrayOutputStream o = new ByteArrayOutputStream(1024);
		try
		{
			int i = 1024;
			byte[] buf = new byte[i];

			while ((i = iis.read(buf, 0, i)) > 0)
			{
				o.write(buf, 0, i);
			}

		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return o.toByteArray();
	}

	public static void main(String[] args) throws IOException
	{
		InputStream inputStream = new FileInputStream("d:/SkillContainer.div");
		byte[] bb = new byte[5000];
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();

		while (inputStream.read(bb) != -1)
		{
			arrayOutputStream.write(bb);
		}
		System.out
				.println(new String(arrayOutputStream.toByteArray()).length());
		byte[] b = compress("1".getBytes());
		System.out.println(new String(b));
		byte[] c = decompress(b);
		System.out.println(new String(c).length());
		inputStream.close();
	}
}
