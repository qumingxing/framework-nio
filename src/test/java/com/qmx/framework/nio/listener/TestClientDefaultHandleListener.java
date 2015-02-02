package com.qmx.framework.nio.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
public class TestClientDefaultHandleListener implements HandleListener
{
	private List<String> testData = new ArrayList<String>();
	private final Logger log = LoggerFactory
			.getLogger(TestClientDefaultHandleListener.class);
	private final ScheduledExecutorService scheduledExecutorService = Executors
			.newSingleThreadScheduledExecutor();

	public TestClientDefaultHandleListener()
	{
		for (int i = 0; i < 500; i++)
		{
			testData.add("client" + i);
		}
	}

	@Override
	public void conneced(MessageEvent event)
	{
		// TODO Auto-generated method stub
		System.out.println("conneced");
		event.write("hello server ");
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
		event.setMessageType("123");
		event.executMessageExecutor();
		System.out.println(event.getMessage().toString());
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
		// 发送心跳
		scheduledExecutorService.scheduleAtFixedRate(new Runnable()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				// event.write(message)
				System.out.println("模拟发送心跳!");
			}
		}, 0, 6000, TimeUnit.MILLISECONDS);
	}

	@Override
	public void exception(MessageEvent event, Exception e)
	{
		// TODO Auto-generated method stub
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
