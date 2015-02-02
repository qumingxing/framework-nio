package com.qmx.framework.nio;

import com.qmx.framework.nio.ByteEncoderAndDecoderImpl;
import com.qmx.framework.nio.DefaultAlgorithmImpl;
import com.qmx.framework.nio.EncoderAndDecoderFactory;
import com.qmx.framework.nio.LengthSplitChannelBuffer;
import com.qmx.framework.nio.MessageContext;
import com.qmx.framework.nio.MessageExecutor;
import com.qmx.framework.nio.MessageFormatBytesToBytes;
import com.qmx.framework.nio.ScriptExecutor;
import com.qmx.framework.nio.SelectorStrategy;
import com.qmx.framework.nio.Server;
import com.qmx.framework.nio.SingleSelectorStrategy;
import com.qmx.framework.nio.listener.TestServerFileDefaultHandleListener;

public class TestFileTransferServer
{
	public static void main(String[] args)
	{
		MessageExecutor.register("123", new ScriptExecutor());
		Server server = new Server(10086);
		SelectorStrategy selectorStrategy = new SingleSelectorStrategy(2, 2);
		selectorStrategy
				.setHandleListen(new TestServerFileDefaultHandleListener());
		selectorStrategy.setBufferType(LengthSplitChannelBuffer.class);
		MessageContext messageContext = new MessageContext();
		messageContext.setAlgorithm(new DefaultAlgorithmImpl());
		EncoderAndDecoderFactory encoderAndDecoderFactory = new EncoderAndDecoderFactory();
		encoderAndDecoderFactory
				.setEncoderAndDecoderType(ByteEncoderAndDecoderImpl.class);
		messageContext.setEncoderAndDecoderFactory(encoderAndDecoderFactory);
		messageContext.setMessageFormat(new MessageFormatBytesToBytes());
		selectorStrategy.setMessageContext(messageContext);
		server.setSelectorStrategy(selectorStrategy);
		server.start();

	}
}
