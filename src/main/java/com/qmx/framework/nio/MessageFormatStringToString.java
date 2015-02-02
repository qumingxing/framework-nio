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

import java.util.Arrays;

/**
 * 单一的将字符串格式化后返回，该类实现了{@link MessageFormatToString}
 * 
 * @author qmx 2014-12-12 上午10:52:41
 * 
 */
public class MessageFormatStringToString implements MessageFormatToString
{
	@Override
	public byte[] format(byte[] bytes)
	{
		// TODO Auto-generated method stub
		byte[] sendBytes = bytes;
		byte[] lengthArr = (String.valueOf(sendBytes.length) + "$").getBytes();
		sendBytes = Arrays.copyOf(sendBytes, sendBytes.length
				+ lengthArr.length);
		System.arraycopy(sendBytes, 0, sendBytes, lengthArr.length,
				sendBytes.length - lengthArr.length);
		System.arraycopy(lengthArr, 0, sendBytes, 0, lengthArr.length);
		return sendBytes;
	}
}
