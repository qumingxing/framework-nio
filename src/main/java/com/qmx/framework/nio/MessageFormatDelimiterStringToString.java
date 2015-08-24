package com.qmx.framework.nio;

import java.util.Arrays;

public class MessageFormatDelimiterStringToString implements
		MessageFormatToString
{

	@Override
	public byte[] format(byte[] bytes)
	{
		// TODO Auto-generated method stub
		byte[] sendBytes = bytes;
		byte[] lengthArr = "\r".getBytes();
		sendBytes = Arrays.copyOf(sendBytes, sendBytes.length
				+ lengthArr.length);
		System.arraycopy(lengthArr, 0, sendBytes, sendBytes.length - 1, 1);
		return sendBytes;
	}
}
