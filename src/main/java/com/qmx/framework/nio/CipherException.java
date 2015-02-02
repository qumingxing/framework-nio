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
 * 加密解密异常
 * 
 * @author qmx 2014-11-26 上午10:51:06
 * 
 */
public class CipherException extends Exception
{
	private static final long serialVersionUID = 1L;

	public CipherException()
	{
		super();
	}

	public CipherException(String message)
	{
		super(message);
	}

	public CipherException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public CipherException(Throwable cause)
	{
		super(cause);
	}
}
