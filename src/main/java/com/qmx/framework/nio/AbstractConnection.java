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
 * 抽象的连接对象类，目前主要作用是针对客户端断线重连
 * 
 * @author qmx 2014-12-19 上午11:40:13
 * 
 */
public abstract class AbstractConnection
{
	/**
	 * 客户端或服务端启动
	 */
	public abstract void start();

	/**
	 * 获取主选择器线程
	 * 
	 * @return 主选择器线程
	 */
	public abstract ExecutorService getSelectorThread();

	/**
	 * 获取主选择器
	 * 
	 * @return 主选择器
	 */
	public abstract Selector getSelector();
}
