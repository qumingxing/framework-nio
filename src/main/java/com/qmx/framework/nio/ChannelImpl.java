/*
 * Copyright [2014-2015] [qumx of copyright owner]
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qmx.framework.nio;

import java.nio.channels.SocketChannel;

/**
 * 通道核心接口的实现类，主要用于设置通道的相关重要参数
 * 
 * @author qmx 2014-11-28 下午1:19:36
 * 
 */
public class ChannelImpl implements Channel
{
	/**
	 * 该通道对应的通信对象
	 */
	private SocketChannel channel;
	/**
	 * 通道名称
	 * <code>socketChannel.socket().getRemoteSocketAddress()<code>连接端远程地址及端口号
	 */
	private String channelName;
	/**
	 * 是否需要认证
	 */
	private boolean certificateAuth;
	/**
	 * 是否已经认证过
	 */
	private boolean certificateAuthed;
	/**
	 * 信道的接受时间，该时间的作用主要是检查长时间未认证的客户端，根据用户设置的最大超时时间来断开未通过认证的通道 <br/>
	 * 详细见{@link Channes} <code>startScheduledCheck</code>方法
	 */
	private long acceptDate;

	@Override
	public SocketChannel getChannel()
	{
		// TODO Auto-generated method stub
		return channel;
	}

	@Override
	public void setChannel(SocketChannel channel)
	{
		this.channel = channel;
	}

	@Override
	public void setChannelName(String channelName)
	{
		// TODO Auto-generated method stub
		this.channelName = channelName;
	}

	@Override
	public String getChannelName()
	{
		// TODO Auto-generated method stub
		return channelName;
	}

	@Override
	public boolean isCertificateAuthed()
	{
		// TODO Auto-generated method stub
		return this.certificateAuthed;
	}

	@Override
	public void setCertificateAuth(boolean certificateAuth)
	{
		// TODO Auto-generated method stub
		this.certificateAuth = certificateAuth;
	}

	@Override
	public void setCertificateAuthed(boolean certificateAuthed)
	{
		// TODO Auto-generated method stub
		this.certificateAuthed = certificateAuthed;
	}

	@Override
	public boolean isCertificateAuth()
	{
		// TODO Auto-generated method stub
		return this.certificateAuth;
	}

	@Override
	public void setAcceptDate(long date)
	{
		// TODO Auto-generated method stub
		this.acceptDate = date;
	}

	@Override
	public long getAcceptDate()
	{
		// TODO Auto-generated method stub
		return this.acceptDate;
	}

}
