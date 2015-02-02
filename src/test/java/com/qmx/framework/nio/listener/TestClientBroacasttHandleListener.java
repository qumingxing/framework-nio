package com.qmx.framework.nio.listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qmx.framework.nio.HandleListener;
import com.qmx.framework.nio.DataType;
import com.qmx.framework.nio.MessageEvent;
import com.qmx.framework.nio.StringPrintWriter;

/**
 * 
 * @author qmx 2014-12-1 上午11:12:28
 * 
 */
public class TestClientBroacasttHandleListener implements HandleListener
{
	private final Logger log = LoggerFactory
			.getLogger(TestClientBroacasttHandleListener.class);
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
		// 判断确定类型后执行
		if(event.getDataType()==DataType.STRING)
		{
			System.out.println("字符串");
			System.out.println(event.getMessage());
		}else if (event.getDataType()==DataType.BYTE)
		{
			System.out.println("字节");
			System.out.println(event.getMessage());
		}else
		{
			System.out.println(event.getMessage());
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
