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
package org.apache.wicket.security.pages.secure;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.security.checks.ComponentSecurityCheck;
import org.apache.wicket.security.components.SecureComponentHelper;
import org.apache.wicket.security.pages.SecureTestPage;


/**
 * @author marrink
 * 
 */
public class PageB extends SecureTestPage
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public PageB()
	{
		add(new Label("welcome", "Welcome Only logged in users can see this page"));
		TextField textField = new TextField("secure", new Model("secure textfield"));
		add(SecureComponentHelper
				.setSecurityCheck(textField, new ComponentSecurityCheck(textField)));
	}

}