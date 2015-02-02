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
 * 发送消息格式化顶层接口，该接口包含了两个子接口 {@link MessageFormatToString}
 * {@link MessageFormatToBytes}，分别为字符串格式化和二进制格式化，这两个接口主要用于区分格式化的类别。
 * 
 * @author qmx 2014-11-28 下午1:35:05
 * 
 */
public interface MessageFormat
{
	public byte[] format(byte[] bytes);
}
