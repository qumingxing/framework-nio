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
 * 增强的字节格式化接口用于区分类型不做其它用途，该接口继承了{@link MessageFormatToBytes}
 * 
 * <pre>
 * 示例
 * SR100000001S000101234567890<br/>
 * SE100000001S000101234567890<br/>
 * AS000101234567890<br/>
 * SRB000101234567890<br/>
 * SEB000101234567890<br/>
 * 同步传递类型S同步、(R请求E响应)、后跟随9位唯一消息编号、(S字符串B字节)、5位长度不足补0
 * 异步传递类型A异步、(S字符串B字节)、5位长度不足补0
 * </pre>
 * 
 * @author qmx 2015-2-26 上午11:47:49
 * 
 */
public interface EnhanceMessageFormatToBytes extends MessageFormatToBytes,
		EnhanceMessageFormat
{
	
}
