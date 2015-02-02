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
 * <p>
 * 用户自定义必须实现的消息事件核心接口，该接口对应了一系列方法<br/>
 * 1、当客户端通道连接上了服务端会会调用<code>conneced({@link MessageEvent} event)</code>
 * 方法，该方法执行完毕之后会调用 <code>write(MessageEvent event)</code>方法，可向远端写消息<br/>
 * 2、当服务端接与客户端接受连接后会调用<code>accept({@link MessageEvent} event)</code>
 * 方法，该方法执行完毕之后会调用 <code>write(MessageEvent event)</code>方法，可向远端写消息<br/>
 * 3、当任何一端收到消息后都会调用<code>read({@link MessageEvent} event)</code>方法<br/>
 * 4、当有异常发生时会调用<code>exception({@link MessageEvent} event)</code>方法<br/>
 * 5、当任何一端通道关闭时会调用<code>close({@link MessageEvent} event)</code>方法<br/>
 * 6、{@link MessageEvent}中包含了接收到的数据<code>getMessage()</code>
 * 、对应的通道对象、通道名称、向该通道中写的方法。当前通道中接受数据的类型，包括用户 可以自定义实现的 {@link MessageExecutor}
 * 接口注册方法，然后设置<code>messageType</code>后，框架会自动调度 {@link MessageExecutor}接口中实现的方法。
 * </p>
 * 
 * @author qmx 2014-12-1 上午11:12:04
 * @since 1.0
 */
public interface HandleListener
{
	public void conneced(MessageEvent event);

	public void close(MessageEvent event);

	public void read(MessageEvent event);

	public void write(MessageEvent event);

	public void exception(MessageEvent event, Exception e);

	public void accept(MessageEvent event);
}
