package com.qmx.framework.nio.listener;

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
public class TestAuthServerDefaultHandleListener implements HandleListener
{
	private List<String> testData = new ArrayList<String>();
	private final Logger log = LoggerFactory
			.getLogger(TestAuthServerDefaultHandleListener.class);
	public TestAuthServerDefaultHandleListener()
	{
		for (int i = 0; i < 500; i++)
		{
			testData.add("server" + i);
		}
	}

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

	static int aaa = 0;

	@Override
	public void read(MessageEvent event)
	{
		// TODO Auto-generated method stub
		// 判断确定类型后执行
		System.out.println(event.getMessage());
		if (testData.size() > 0)
		{
			String str = testData.remove(0);
			event.write(str);
		}
	}

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
	}
}
