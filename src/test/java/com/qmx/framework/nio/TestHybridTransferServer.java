package com.qmx.framework.nio;

import com.qmx.framework.nio.HybridLengthSplitChannelBuffer;
import com.qmx.framework.nio.MessageExecutor;
import com.qmx.framework.nio.ScriptExecutor;
import com.qmx.framework.nio.SelectorStrategy;
import com.qmx.framework.nio.Server;
import com.qmx.framework.nio.SingleSelectorStrategy;
import com.qmx.framework.nio.listener.TestServerHybridDefaultHandleListener;

public class TestHybridTransferServer
{
	public static void main(String[] args)
	{
		MessageExecutor.register("123", new ScriptExecutor());
		Server server = new Server(10086);
		SelectorStrategy selectorStrategy = new SingleSelectorStrategy(10, 2);
		selectorStrategy
				.setHandleListen(new TestServerHybridDefaultHandleListener());
		selectorStrategy.setBufferType(HybridLengthSplitChannelBuffer.class);
		server.setSelectorStrategy(selectorStrategy);
		server.start();
	}
}
