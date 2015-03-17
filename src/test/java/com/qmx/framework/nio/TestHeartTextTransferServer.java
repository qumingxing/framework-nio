package com.qmx.framework.nio;
import com.qmx.framework.nio.LengthSplitChannelBuffer;
import com.qmx.framework.nio.SelectorStrategy;
import com.qmx.framework.nio.Server;
import com.qmx.framework.nio.SingleSelectorStrategy;
import com.qmx.framework.nio.listener.TestServerDefaultHandleListener;
public class TestHeartTextTransferServer
{
	public static void main(String[] args)
	{
		HeartCheck heartCheck = new HeartCheck(true, 6000);
		heartCheck.setExpireInvaildRemoveChannelEnable(true);
		ConfigResources config = new ConfigResources();
		config.setPort(10086);
		config.setPointModel(PointModel.SERVER);
		config.setHeartCheck(heartCheck);
		Server server = new Server(config.getPort());
		SelectorStrategy selectorStrategy = new SingleSelectorStrategy(10, 2);
		selectorStrategy.setHandleListen(new TestServerDefaultHandleListener());
		selectorStrategy.setBufferType(LengthSplitChannelBuffer.class);
		selectorStrategy.setConfig(config);
		server.setSelectorStrategy(selectorStrategy);
		server.start();
	}
}
