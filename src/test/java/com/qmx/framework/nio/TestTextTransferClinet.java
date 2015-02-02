package com.qmx.framework.nio;

import com.qmx.framework.nio.Client;
import com.qmx.framework.nio.ConfigResources;
import com.qmx.framework.nio.LengthSplitChannelBuffer;
import com.qmx.framework.nio.MessageExecutor;
import com.qmx.framework.nio.PointModel;
import com.qmx.framework.nio.ScriptExecutor;
import com.qmx.framework.nio.SelectorStrategy;
import com.qmx.framework.nio.SingleSelectorStrategy;
import com.qmx.framework.nio.listener.TestClientDefaultHandleListener;

public class TestTextTransferClinet
{
	public static void main(String[] args)
	{
		MessageExecutor.register("123", new ScriptExecutor());
		ConfigResources config = new ConfigResources();
		config.setIp("172.18.70.109");
		config.setPort(10086);
		config.setPointModel(PointModel.CLIENT);
		Client client = new Client(config.getIp(), config.getPort());
		SelectorStrategy selectorStrategy = new SingleSelectorStrategy(2, 2);
		selectorStrategy.setHandleListen(new TestClientDefaultHandleListener());
		selectorStrategy.setBufferType(LengthSplitChannelBuffer.class);
		selectorStrategy.setConfig(config);
		client.setSelectorStrategy(selectorStrategy);
		client.start();
		config.setConnection(client);
	}
}
