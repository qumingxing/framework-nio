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

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 选择器选择入口该类实现了{@link SelectorStrategy}的方法，并继承了{@link SelectProcess}
 * ，当有通道注册相关事件后会通知<code>selector.select()</code>并返回
 * 
 * @author qmx 2014-12-1 上午11:45:20
 * 
 */
public class SingleSelectorStrategy extends SelectProcess implements
		SelectorStrategy
{
	/**
	 * 独立的线程维护选择器
	 */
	private ExecutorService executorService = Executors
			.newSingleThreadExecutor();

	/**
	 * 
	 * @param writePoolSize
	 *            写线程池
	 * @param readPoolSize
	 *            读线程池
	 */
	public SingleSelectorStrategy(int writePoolSize, int readPoolSize)
	{
		super(writePoolSize, readPoolSize);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param writePoolSize
	 *            写线程池
	 * @param readPoolSize
	 *            读线程池
	 * @param synchPoolSize
	 *            同步消息处理线程池
	 */
	public SingleSelectorStrategy(int writePoolSize, int readPoolSize,
			int synchPoolSize)
	{
		super(writePoolSize, readPoolSize, synchPoolSize);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ExecutorService execute(final Selector selector)
	{
		// TODO Auto-generated method stub
		super.setSelector(selector);
		executorService.submit(new Runnable()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				while (true)
				{
					try
					{
						int num = selector.select();
						if (num > 0)
						{
							select(selector.selectedKeys());
						} else
						{
							try
							{
								Thread.sleep(300);
							} catch (InterruptedException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} catch (IOException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

		return executorService;
	}

	@Override
	public void setHandleListen(HandleListener handleListener)
	{
		// TODO Auto-generated method stub
		super.setHandleListener(handleListener);
	}

	@Override
	public void setMessageContext(MessageContext context)
	{
		// TODO Auto-generated method stub
		super.setMessageContext(context);
	}
}
