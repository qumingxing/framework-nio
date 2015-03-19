package com.qmx.framework.nio.listener;

import com.qmx.framework.nio.Channel;
import com.qmx.framework.nio.HeartStateListener;

/**
 * 
 * @author qmx 2015-3-19 下午2:03:10
 * 
 */
public class HeartStateListenerImpl implements HeartStateListener
{

	@Override
	public void checkHeart(Channel channel)
	{
		// TODO Auto-generated method stub
		System.out.println("checkHeart" + channel.getChannelName());
	}

	@Override
	public void uncheckHeart(Channel channel)
	{
		// TODO Auto-generated method stub
		System.out.println("uncheckHeart" + channel.getChannelName());
	}

}
