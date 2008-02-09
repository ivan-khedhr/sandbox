/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.wicket.security.examples.httplogin.basic.authentication;

import org.apache.wicket.security.examples.authorization.MyPrincipal;
import org.apache.wicket.security.hive.authentication.DefaultSubject;
import org.apache.wicket.security.hive.authentication.Subject;
import org.apache.wicket.security.hive.authentication.UsernamePasswordContext;
import org.apache.wicket.security.authentication.LoginException;
import org.apache.wicket.util.lang.Objects;

/**
 * Standard context.
 * 
 * @author marrink
 */
public class MyLoginContext extends UsernamePasswordContext
{

	/**
	 * Construct.
	 */
	public MyLoginContext()
	{
	}

	/**
	 * Construct.
	 * 
	 * @param username
	 * @param password
	 */
	public MyLoginContext(String username, String password)
	{
		super(username, password);
	}

	/**
	 * @see org.apache.wicket.security.hive.authentication.UsernamePasswordContext#getSubject(java.lang.String,
	 *      java.lang.String)
	 */
	protected Subject getSubject(String username, String password) throws LoginException
	{
		// irrelevant check
		if (username != null && Objects.equal(username, password))
		{
			DefaultSubject subject = new DefaultSubject();
			subject.addPrincipal(new MyPrincipal("basic"));
			return subject;
		}
		throw new LoginException("Username and password do not match any known user.");
	}

}
