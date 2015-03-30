package com.qmx.framework.nio.listener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qmx.framework.nio.HandleListener;
import com.qmx.framework.nio.MessageEvent;
import com.qmx.framework.nio.StringPrintWriter;

/**
 * 
 * @author qmx 2014-12-1 上午11:12:28
 * 
 */
public class TestClientFileDefaultHandleListener implements HandleListener
{
	private List<String> testData = new ArrayList<String>();
	private OutputStream outputStream;
	private final Logger log = LoggerFactory
			.getLogger(TestClientFileDefaultHandleListener.class);

	public TestClientFileDefaultHandleListener()
	{
		for (int i = 0; i < 100; i++)
		{
			testData.add("client" + i);
		}
	}

	@Override
	public void conneced(MessageEvent event)
	{
		// TODO Auto-generated method stub
		System.out.println("conneced");
		/*
		 * Map<String, String> msg = new HashMap<String, String>();
		 * msg.put("msg", "client send test msg");
		 */
		event.write("client send test msg".getBytes());
	}

	@Override
	public void close(MessageEvent event)
	{
		// TODO Auto-generated method stub
		System.out.println("close");
	}

	@Override
	public void read(MessageEvent event)
	{
		// TODO Auto-generated method stub
		byte[] byt = (byte[]) event.getMessage();
		if (null == outputStream)
		{
			try
			{
				outputStream = new FileOutputStream(
						"e:\\sst-client-20141107182427.zip", true);// sst-client-20141107182427.zip
			} catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (byt.length == 1 && byt[0] == '\n')
		{
			System.out.println("read over");
			try
			{
				outputStream.flush();
				outputStream.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}

		try
		{
			outputStream.write(byt);
		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	public void write(MessageEvent event)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void exception(MessageEvent event, Exception e)
	{
		// TODO Auto-generated method stub
		// e.printStackTrace();
		StringPrintWriter stringWriter = new StringPrintWriter();
		e.printStackTrace(stringWriter);
		log.error("异常->{}\n{}", event.getChannel().getChannelName(),
				stringWriter.getString());
	}

	@Override
	public void accept(MessageEvent event)
	{
		// TODO Auto-generated method stub
		System.out.println("accept");
	}
}
