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
 * 当认证失败的时候会抛出此异常
 * 
 * @author qmx 2015-1-9 上午11:00:32
 * 
 */
public class CertificateAuthException extends Exception
{
	private static final long serialVersionUID = 4025513432978498368L;
	private static final String CODE = "certificate auth fail";

	public CertificateAuthException()
	{
		super(CODE);
	}

	public CertificateAuthException(String code)
	{
		super(code);
	}

	public CertificateAuthException(Throwable cause)
	{
		super(CODE, cause);
	}

	public CertificateAuthException(String code, Throwable cause)
	{
		super(code, cause);
	}
}
