package com.qmx.framework.nio;

import java.io.IOException;
import java.nio.channels.Selector;

public class SelectorFactory
{
	public static Selector getSelector()
	{
		try
		{
			return Selector.open();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static void returnSelector(Selector selector)
	{
		try
		{
			selector.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
