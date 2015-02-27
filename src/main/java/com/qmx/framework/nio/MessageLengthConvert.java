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
 * 将一个给定长度数字转换成指定位数的长度 <br/>
 * 1000转换后01000
 * 
 * @author qmx 2015-2-26 上午11:26:11
 * 
 */
public class MessageLengthConvert
{
	private static int formatDefaultLength = 5;

	/**
	 * 更新全局的默认长度
	 * 
	 * @param globalLength
	 *            更新全局的默认长度
	 */
	public static void updateGlobalLength(int globalLength)
	{
		formatDefaultLength = globalLength;
	}

	/**
	 * 获取默认的或设置过的新的消息位数长度
	 * 
	 * @return 消息位数长度
	 */
	public static int getFormatDefaultLength()
	{
		return formatDefaultLength;
	}

	/**
	 * 给定长度后将其转换为指定<code>formatDefaultLength</code>位数长度
	 * 
	 * @param messageLength字符串的长度
	 * @return 转换后的结果
	 */
	public static String getLengthString(int messageLength)
	{
		return getLengthString(messageLength, formatDefaultLength);
	}

	/**
	 * 给定长度和指定位数后将其转换为指定<code>formatLength</code>位数长度
	 * 
	 * @param stringLength字符串的长度
	 * @param formatLength
	 *            指定转换长度
	 * @return 转换后的结果
	 */
	private static String getLengthString(int messageLength, int formatLength)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(messageLength);
		int unintLen = sb.length();
		if (unintLen < formatLength)
		{
			int appendLength = formatLength - unintLen;
			for (int i = 0; i < appendLength; i++)
			{
				sb.insert(0, "0");
			}
		} else if (unintLen > formatLength)
		{
			throw new IllegalArgumentException(
					"stringLength's length was too big than formatLength.please call updateGlobalLength method set new default value");
		}
		return sb.toString();
	}
}
