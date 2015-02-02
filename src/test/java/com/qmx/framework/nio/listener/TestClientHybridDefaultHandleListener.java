package com.qmx.framework.nio.listener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qmx.framework.nio.DataType;
import com.qmx.framework.nio.HandleListener;
import com.qmx.framework.nio.MessageEvent;
import com.qmx.framework.nio.MessageFormatHybridStringToString;
import com.qmx.framework.nio.StringPrintWriter;

/**
 * 
 * @author qmx 2014-12-1 上午11:12:28
 * 
 */
public class TestClientHybridDefaultHandleListener implements HandleListener
{
	private List<String> testData = new ArrayList<String>();
	private final static MessageFormatHybridStringToString MESSAGE_FORMAT_HYBRID_STRING_TO_STRING = new MessageFormatHybridStringToString();
	//private final static MessageFormatHybridBytesToBytes MESSAGE_FORMAT_HYBRID_BYTES_TO_BYTES = new MessageFormatHybridBytesToBytes();
	private OutputStream outputStream;
	private final Logger log = LoggerFactory
			.getLogger(TestClientHybridDefaultHandleListener.class);
	public TestClientHybridDefaultHandleListener()
	{
		for (int i = 0; i < 500; i++)
		{
			testData.add("client" + i);
		}
		try
		{
			outputStream = new FileOutputStream(
					"e:\\sst-client-20141107182427.zip", true);
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		;
	}

	@Override
	public void conneced(MessageEvent event)
	{
		// TODO Auto-generated method stub
		System.out.println("conneced");
		event.setMessageFormat(MESSAGE_FORMAT_HYBRID_STRING_TO_STRING);
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
		if (event.getDataType() == DataType.STRING)
		{
			System.out.println(event.getMessage().toString());
			if (testData.size() > 0)
			{
				event.setMessageFormat(MESSAGE_FORMAT_HYBRID_STRING_TO_STRING);
				String str = testData.remove(0);
				event.write(str);
			}
		} else if (event.getDataType() == DataType.BYTE)
		{
			byte[] byt = (byte[]) event.getMessage();
			aaa += byt.length;
			System.out.println("累计读" + aaa);

			try
			{
				outputStream.write(byt);
			} catch (Exception e)
			{
				// TODO: handle exception
				e.printStackTrace();
			}
			try
			{
				outputStream.flush();
				if (aaa == 37339411)
				{
					outputStream.close();
				}
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
