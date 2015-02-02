package com.qmx.framework.nio.listener;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qmx.framework.nio.ByteEncoderAndDecoderImpl;
import com.qmx.framework.nio.DataType;
import com.qmx.framework.nio.DefaultAlgorithmImpl;
import com.qmx.framework.nio.HandleListener;
import com.qmx.framework.nio.MessageEvent;
import com.qmx.framework.nio.MessageFormatHybridBytesToBytes;
import com.qmx.framework.nio.MessageFormatHybridStringToString;
import com.qmx.framework.nio.StringPrintWriter;

/**
 * 
 * @author qmx 2014-12-1 上午11:12:28
 * 
 */
public class TestServerHybridDefaultHandleListener implements HandleListener
{
	private List<String> testData = new ArrayList<String>();
	private final static MessageFormatHybridStringToString MESSAGE_FORMAT_HYBRID_STRING_TO_STRING = new MessageFormatHybridStringToString();
	private final static MessageFormatHybridBytesToBytes MESSAGE_FORMAT_HYBRID_BYTES_TO_BYTES = new MessageFormatHybridBytesToBytes();
	private final Logger log = LoggerFactory
			.getLogger(TestServerHybridDefaultHandleListener.class);

	public TestServerHybridDefaultHandleListener()
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
		if (event.getDataType() == DataType.STRING)
		{
			System.out.println(event.getMessage());
			if (testData.size() > 0)
			{
				String str = testData.remove(0);
				if (str.equals("server200"))
				{
					try
					{
						Thread.sleep(5000);
					} catch (InterruptedException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					event.setMessageFormat(MESSAGE_FORMAT_HYBRID_BYTES_TO_BYTES);
					ByteEncoderAndDecoderImpl impl = new ByteEncoderAndDecoderImpl();
					impl.setAlgorithm(new DefaultAlgorithmImpl());
					event.setEncoderAndDecoder(impl);
					InputStream inputStream = null;
					try
					{
						inputStream = new FileInputStream(
								"/usr/woniu/olts/resources/123.txt");
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
				} else
				{
					event.setMessageFormat(MESSAGE_FORMAT_HYBRID_STRING_TO_STRING);
					event.write(str);
				}

			}
		} else if (event.getDataType() == DataType.BYTE)
		{

		}
		// TODO Auto-generated method stub

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
