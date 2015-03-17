package com.qmx.framework.nio;

import com.qmx.framework.nio.Client;
import com.qmx.framework.nio.HybridLengthSplitChannelBuffer;
import com.qmx.framework.nio.MessageExecutor;
import com.qmx.framework.nio.ScriptExecutor;
import com.qmx.framework.nio.SelectorStrategy;
import com.qmx.framework.nio.SingleSelectorStrategy;
import com.qmx.framework.nio.listener.TestClientHybridDefaultHandleListener;

public class TestHybridTransferClinet
{
	public static void main(String[] args)
	{
		MessageExecutor.register("123", new ScriptExecutor());
		Client client = new Client("172.18.70.109", 10086);
		SelectorStrategy selectorStrategy = new SingleSelectorStrategy(2, 2);
		selectorStrategy
				.setHandleListen(new TestClientHybridDefaultHandleListener());
		selectorStrategy.setBufferType(HybridLengthSplitChannelBuffer.class);
		client.setSelectorStrategy(selectorStrategy);
		client.start();
	}
}
