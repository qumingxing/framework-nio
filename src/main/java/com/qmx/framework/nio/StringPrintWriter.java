package com.qmx.framework.nio;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 
 * @author qmx 2013-1-17 下午3:53:26
 * 
 */
public class StringPrintWriter extends PrintWriter
{
	public StringPrintWriter()
	{
		super(new StringWriter());
	}

	public StringPrintWriter(int initialSize)
	{
		super(new StringWriter(initialSize));
	}

	public String getString()
	{
		return ((StringWriter) this.out).toString();
	}

	@Override
	public String toString()
	{
		return getString();
	}
}
