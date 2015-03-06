package com.qmx.framework.nio;
import com.qmx.framework.nio.LengthSplitChannelBuffer;
import com.qmx.framework.nio.SelectorStrategy;
import com.qmx.framework.nio.Server;
import com.qmx.framework.nio.SingleSelectorStrategy;
import com.qmx.framework.nio.listener.JmeterTestServerDefaultHandleListener;
public class JmeterTestTextTransferServer
{
	public static void main(String[] args)
	{
		ConfigResources config = new ConfigResources();
		config.setPort(10086);
		config.setPointModel(PointModel.SERVER);
		Server server = new Server(config.getPort());
		SelectorStrategy selectorStrategy = new SingleSelectorStrategy(10, 10);
		selectorStrategy.setHandleListen(new JmeterTestServerDefaultHandleListener());
		selectorStrategy.setBufferType(LengthSplitChannelBuffer.class);
		selectorStrategy.setConfig(config);
		server.setSelectorStrategy(selectorStrategy);
		server.start();
	}
}
