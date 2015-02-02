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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 包括用户 可以自定义实现的 {@link MessageExecutor} 接口注册方法，然后设置{@link MessageEvent}的
 * <code>messageType</code>后，框架会自动调度 {@link MessageExecutor}接口中实现的方法。完成用户业务操作。
 * 
 * @author qmx 2014-12-1 上午10:27:32
 * 
 */
public abstract class MessageExecutor
{
	/**
	 * 所有注册的业务{@link MessageExecutor}对象集合，键为用户自定义的唯一业务标识。
	 */
	private static Map<String, MessageExecutor> executors = new ConcurrentHashMap<String, MessageExecutor>();

	public static void register(String alias, MessageExecutor executor)
	{
		executors.put(alias, executor);
	}

	public static MessageExecutor getExecutor(String alias)
	{
		return executors.get(alias);
	}

	public abstract void executor(MessageEvent event);
}
