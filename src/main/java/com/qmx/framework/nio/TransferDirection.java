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
 * 同步消息的传输方向请求、响应 {@link MessageFormatEnhanceBytesToBytes}、
 * {@link MessageFormatEnhanceStringToString}、{@link ComplexSplitChannelBuffer}
 * 
 * @author qmx 2015-2-27 下午1:33:21
 * 
 */
public enum TransferDirection
{
	REQUEST, RESPONSE
}
