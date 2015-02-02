package com.qmx.framework.nio.listener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

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
public class TestServerFileDefaultHandleListener implements HandleListener
{
	private final Logger log = LoggerFactory
			.getLogger(TestServerFileDefaultHandleListener.class);
	@Override
	public void conneced(MessageEvent event)
	{
		// TODO Auto-generated method stub
		System.out.println("conneced");
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

	}

	static int aaa = 0;

	@Override
	public void write(MessageEvent event)
	{
		// TODO Auto-generated method stub
		System.out.println("write");
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
		InputStream inputStream = null;
		try
		{// /usr/woniu/olts/resources/123.txt
			inputStream = new FileInputStream(
					"/usr/woniu/olts/resources/123.txt");// d:\\sst-client-20141107182427.zip
			byte[] aa = new byte[8096];
			int temp = 0;
			while ((temp = inputStream.read(aa)) != -1)
			{
				aaa += temp;
				System.out.println("累计写" + aaa);
				byte[] sendMsg = Arrays.copyOfRange(aa, 0, temp);
				event.write(sendMsg);
			}
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				inputStream.close();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("写完 ");
	}
}
