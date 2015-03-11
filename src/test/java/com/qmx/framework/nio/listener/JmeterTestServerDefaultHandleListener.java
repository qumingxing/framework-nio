package com.qmx.framework.nio.listener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
public class JmeterTestServerDefaultHandleListener implements HandleListener
{
	private final Logger log = LoggerFactory
			.getLogger(JmeterTestServerDefaultHandleListener.class);
	private final ScheduledExecutorService scheduledExecutorService = Executors
			.newSingleThreadScheduledExecutor();
	private AtomicInteger  readCount = new AtomicInteger(0);
	private AtomicInteger connectSum = new AtomicInteger(0);
	private AtomicInteger disconnectSum = new AtomicInteger(0);
	private AtomicInteger errorSum = new AtomicInteger(0);
	public JmeterTestServerDefaultHandleListener()
	{
		scheduledExecutorService.scheduleAtFixedRate(new Runnable()
		{

			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				// event.write(message)
				System.out.println("sum connected:" + connectSum.get() + ",sum disconnect:"
						+ disconnectSum.get() + ",sum read:" + readCount.get()+",sum error:"+errorSum.get());
			}
		}, 10000, 10000, TimeUnit.MILLISECONDS);
	}

	@Override
	public void conneced(MessageEvent event)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void close(MessageEvent event)
	{
		// TODO Auto-generated method stub
		disconnectSum.addAndGet(1);
	}

	@Override
	public void read(MessageEvent event)
	{
		// TODO Auto-generated method stub
		// 判断确定类型后执行
		readCount.addAndGet(1);
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
		errorSum.addAndGet(1);
	}

	@Override
	public void accept(MessageEvent event)
	{
		// TODO Auto-generated method stub
		connectSum.addAndGet(1);
	}
}
