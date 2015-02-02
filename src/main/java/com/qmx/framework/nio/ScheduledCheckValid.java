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

/**
 * 该对象不为空并且当前模式开启了服务端认证方式{@link ConfigResources}<code>certificateAuth</code>
 * ，定时任务会检查根据设置的时候来判断当前客户端的合法性
 * 
 * @author qmx 2015-1-15 上午10:42:17
 * 
 */
public class ScheduledCheckValid
{
	/**
	 * 定时初始化延迟时间(毫秒)
	 */
	private long initialDelay;
	/**
	 * 迭代时间(毫秒)
	 */
	private long scheduledDelay;
	/**
	 * 最大等待认证时间(毫秒)
	 */
	private long maxAcceptWaitCertificateTime;

	public long getScheduledDelay()
	{
		return scheduledDelay;
	}

	public void setScheduledDelay(long scheduledDelay)
	{
		this.scheduledDelay = scheduledDelay;
	}

	public long getInitialDelay()
	{
		return initialDelay;
	}

	public void setInitialDelay(long initialDelay)
	{
		this.initialDelay = initialDelay;
	}

	public long getMaxAcceptWaitCertificateTime()
	{
		return maxAcceptWaitCertificateTime;
	}

	public void setMaxAcceptWaitCertificateTime(
			long maxAcceptWaitCertificateTime)
	{
		this.maxAcceptWaitCertificateTime = maxAcceptWaitCertificateTime;
	}

}
