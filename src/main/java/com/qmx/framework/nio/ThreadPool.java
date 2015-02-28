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
 * 核心线程池接口
 * 
 * @author qmx 2014-11-28 下午3:47:12
 * 
 */
public interface ThreadPool
{
	/**
	 * 设置线程池大小
	 * 
	 * @param size
	 *            线程池大小
	 */
	public void setPoolSize(int size);

	/**
	 * 获取线程池大小
	 * 
	 * @return 线程池大小
	 */
	public int getPoolSize();

	/**
	 * 创建线程池
	 */
	public void createThreadPool();

	/**
	 * 多线程池任务执行
	 * 
	 * @param worker
	 *            任务对象
	 */
	public void multiExecute(Worker worker);

	/**
	 * 单线程任务执行
	 * 
	 * @param worker
	 *            任务对象
	 */
	public void singleExecute(Worker worker);

	/**
	 * 关闭线程池，该线程池是多通道共用的且可以重复使用不会多次创建，不能释放。
	 */
	@Deprecated
	public void shutdownThreadPool();
}
