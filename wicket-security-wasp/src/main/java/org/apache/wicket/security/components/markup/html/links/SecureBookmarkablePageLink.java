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
package org.apache.wicket.security.components.markup.html.links;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.security.actions.AbstractWaspAction;
import org.apache.wicket.security.checks.ISecurityCheck;
import org.apache.wicket.security.checks.LinkSecurityCheck;
import org.apache.wicket.security.components.ISecureComponent;
import org.apache.wicket.security.components.SecureComponentHelper;

/**
 * BookmarkablePagelink with visibility / clickability based on user rights.
 * Requires render rights to be visible, and enable rights to be clickable. This
 * class is by default outfitted with a {@link LinkSecurityCheck}, please see
 * its documentation on how to enable the alternative security check
 * 
 * @author marrink
 * @see LinkSecurityCheck
 */
public class SecureBookmarkablePageLink extends BookmarkablePageLink implements ISecureComponent
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * @param id
	 * @param pageClass
	 */
	public SecureBookmarkablePageLink(String id, Class pageClass)
	{
		super(id, pageClass);
		setSecurityCheck(new LinkSecurityCheck(this, pageClass));
	}

	/**
	 * @param id
	 * @param pageClass
	 * @param parameters
	 */
	public SecureBookmarkablePageLink(String id, Class pageClass, PageParameters parameters)
	{
		super(id, pageClass, parameters);
		setSecurityCheck(new LinkSecurityCheck(this, pageClass));
	}

	/**
	 * @see org.apache.wicket.security.components.ISecureComponent#getSecurityCheck()
	 */
	public ISecurityCheck getSecurityCheck()
	{
		return SecureComponentHelper.getSecurityCheck(this);
	}

	/**
	 * @see org.apache.wicket.security.components.ISecureComponent#isActionAuthorized(java.lang.String)
	 */
	public boolean isActionAuthorized(String waspAction)
	{
		return SecureComponentHelper.isActionAuthorized(this, waspAction);
	}

	/**
	 * @see org.apache.wicket.security.components.ISecureComponent#isActionAuthorized(org.apache.wicket.security.actions.AbstractWaspAction)
	 */
	public boolean isActionAuthorized(AbstractWaspAction action)
	{
		return SecureComponentHelper.isActionAuthorized(this, action);
	}

	/**
	 * @see org.apache.wicket.security.components.ISecureComponent#isAuthenticated()
	 */
	public boolean isAuthenticated()
	{
		return SecureComponentHelper.isAuthenticated(this);
	}

	/**
	 * @see org.apache.wicket.security.components.ISecureComponent#setSecurityCheck(org.apache.wicket.security.checks.ISecurityCheck)
	 */
	public void setSecurityCheck(ISecurityCheck check)
	{
		SecureComponentHelper.setSecurityCheck(this, check);
	}

}