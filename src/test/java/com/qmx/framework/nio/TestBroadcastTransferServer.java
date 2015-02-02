package com.qmx.framework.nio;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import com.qmx.framework.nio.ByteEncoderAndDecoderImpl;
import com.qmx.framework.nio.Channels;
import com.qmx.framework.nio.DefaultAlgorithmImpl;
import com.qmx.framework.nio.EncoderAndDecoderFactory;
import com.qmx.framework.nio.LengthSplitChannelBuffer;
import com.qmx.framework.nio.MessageContext;
import com.qmx.framework.nio.MessageExecutor;
import com.qmx.framework.nio.MessageFormatBytesToBytes;
import com.qmx.framework.nio.MessageFormatStringToString;
import com.qmx.framework.nio.ScriptExecutor;
import com.qmx.framework.nio.SelectorStrategy;
import com.qmx.framework.nio.Server;
import com.qmx.framework.nio.SingleSelectorStrategy;
import com.qmx.framework.nio.StringEncoderAndDecoderImpl;
import com.qmx.framework.nio.listener.TestServerBroacasttHandleListener;

public class TestBroadcastTransferServer
{
	static int aaa;

	public static void main(String[] args)
	{
		MessageExecutor.register("123", new ScriptExecutor());
		Server server = new Server(10086);
		SelectorStrategy selectorStrategy = new SingleSelectorStrategy(10, 2);
		selectorStrategy
				.setHandleListen(new TestServerBroacasttHandleListener());
		selectorStrategy.setBufferType(LengthSplitChannelBuffer.class);
		MessageContext messageContext = new MessageContext();
		messageContext.setAlgorithm(new DefaultAlgorithmImpl());
		EncoderAndDecoderFactory encoderAndDecoderFactory = new EncoderAndDecoderFactory();
		encoderAndDecoderFactory
				.setEncoderAndDecoderType(StringEncoderAndDecoderImpl.class);
		messageContext.setEncoderAndDecoderFactory(encoderAndDecoderFactory);
		messageContext.setMessageFormat(new MessageFormatStringToString());
		selectorStrategy.setMessageContext(messageContext);
		server.setSelectorStrategy(selectorStrategy);
		server.start();
		try
		{
			Thread.sleep(5000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Channels channels = Channels.newChannel(null);
		for (int i = 0; i < 500; i++)
			channels.broadcast("hello aaa" + i);
		try
		{
			Thread.sleep(5000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		encoderAndDecoderFactory
				.setEncoderAndDecoderType(ByteEncoderAndDecoderImpl.class);
		messageContext.setEncoderAndDecoderFactory(encoderAndDecoderFactory);
		messageContext.setMessageFormat(new MessageFormatBytesToBytes());
		channels.changeMessageContext(messageContext);
		InputStream inputStream = null;
		try
		{
			inputStream = new FileInputStream("d:\\Categories.xml");
			byte[] aa = new byte[8096];
			int temp = 0;
			while ((temp = inputStream.read(aa)) != -1)
			{
				aaa += temp;
				System.out.println("累计写" + aaa);
				byte[] sendMsg = Arrays.copyOfRange(aa, 0, temp);
				channels.broadcast(sendMsg);
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
		try
		{
			Thread.sleep(5000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		encoderAndDecoderFactory
				.setEncoderAndDecoderType(StringEncoderAndDecoderImpl.class);
		messageContext.setEncoderAndDecoderFactory(encoderAndDecoderFactory);
		messageContext.setMessageFormat(new MessageFormatStringToString());
		channels.changeMessageContext(messageContext);
		for (int i = 0; i < 500; i++)
		{
			channels.broadcast("hello bbbbbb" + i);
		}
			
	}
}
