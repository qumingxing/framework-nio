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
 * 提供给客户端实现，主要对认证过程中用户自定义的用户名和密码简单验证
 * 
 * @author qmx 2015-1-9 上午11:08:43
 * 
 */
public interface CertificateInterface
{
	/**
	 * 自定义实现用户名密码认证
	 * 
	 * @param authModel
	 *            认证实体
	 * @return <code>true</code>认证成功
	 */
	public boolean certificateUser(AuthModel authModel);
}
