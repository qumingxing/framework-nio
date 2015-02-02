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

import java.nio.channels.Selector;
import java.util.concurrent.ExecutorService;

/**
 * 多线程选择器选择入口该类实现了{@link SelectorStrategy}的方法，并继承了{@link SelectProcess}
 * ，当有通道注册相关事件后会通知<code>selector.select()</code>并返回
 * 
 * @author qmx 2014-12-1 上午11:45:42
 * 
 */
public class ThreadPoolSelectorStrategy extends SelectProcess implements
		SelectorStrategy
{
	/**
	 * 多线程维护选择器
	 */
	private static final SelectorThreadPool SELECTOR_POOL = SelectorThreadPool
			.getInstance();

	public ThreadPoolSelectorStrategy(int poolSize, int writePoolSize,
			int readPoolSize)
	{
		super(writePoolSize, readPoolSize);
		SELECTOR_POOL.setPoolSize(poolSize);
		SELECTOR_POOL.createThreadPool();
		// TODO Auto-generated constructor stub
	}

	@Override
	public ExecutorService execute(Selector selector)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHandleListen(HandleListener handleListener)
	{
		// TODO Auto-generated method stub
		super.setHandleListener(handleListener);
	}
}
