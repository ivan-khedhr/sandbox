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
package org.apache.wicket.security.checks;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.security.actions.Render;
import org.apache.wicket.security.actions.WaspAction;
import org.apache.wicket.security.components.SecureComponentHelper;
import org.apache.wicket.security.models.ISecureModel;

/**
 * A security check designed for {@link Link}s. This check has 2 modes for the
 * {@link Render} action. In the regular mode the check behaves as a
 * {@link ClassSecurityCheck} meaning the user must have render rights for the
 * target class of the link, If not the link will not be rendered. In the
 * alternative mode the check behaves as a {@link ComponentSecurityCheck}
 * (render action only) allowing the link to be visible but disabled. Note that
 * for all other actions this check behaves a ClassSecurityCheck (with option to
 * check the model).
 * 
 * @author marrink
 */
public class LinkSecurityCheck extends ComponentSecurityCheck
{
	private static final long serialVersionUID = 1L;

	private final Class clickTarget;

	private boolean useAlternativeRenderCheck = false;

	/**
	 * Constructs a new check, the check uses the regular mode.
	 * 
	 * @param component
	 *            the link
	 * @param clickTarget
	 *            the {@link Page} redirected to when clicking on the link.
	 * @throws IllegalArgumentException
	 *             if clickTarget is not a Page class
	 */
	public LinkSecurityCheck(AbstractLink component, Class clickTarget)
	{
		super(component);
		this.clickTarget = clickTarget;
		if (!Page.class.isAssignableFrom(clickTarget))
		{
			throw new IllegalArgumentException("Class " + clickTarget
					+ " is not a subclass of Page");
		}
	}

	/**
	 * Constructs a new check, the check uses the regular mode.
	 * 
	 * @param component
	 *            the link
	 * @param clickTarget
	 *            the {@link Page} redirected to when clicking on the link.
	 * @param checkSecureModelIfExists
	 *            forces the model to be checked after this check is fired
	 * @throws IllegalArgumentException
	 *             if clickTarget is not a Page class
	 */
	public LinkSecurityCheck(AbstractLink component, Class clickTarget,
			boolean checkSecureModelIfExists)
	{
		super(component, checkSecureModelIfExists);
		this.clickTarget = clickTarget;
		if (!Page.class.isAssignableFrom(clickTarget))
		{
			throw new IllegalArgumentException("Class " + clickTarget
					+ " is not a subclass of Page");
		}
	}

	/**
	 * Returns the target page of the link.
	 * 
	 * @return Returns the clickTarget.
	 */
	public final Class getClickTarget()
	{
		return clickTarget;
	}

	/**
	 * @see org.apache.wicket.security.checks.ComponentSecurityCheck#isActionAuthorized(org.apache.wicket.security.actions.WaspAction)
	 */
	public boolean isActionAuthorized(WaspAction action)
	{
		if (isUseAlternativeRenderCheck()
				&& action.implies(getActionFactory().getAction(Render.class)))
			return super.isActionAuthorized(action);
		boolean result = getStrategy().isClassAuthorized(getClickTarget(), action);
		if (result && checkSecureModel() && SecureComponentHelper.hasSecureModel(getComponent()))
			return ((ISecureModel)getComponent().getModel()).isAuthorized(getComponent(), action);
		return result;

	}

	/**
	 * Flags which mode is active.
	 * 
	 * @return true if the alternative mode is enabled, false otherwise
	 */
	public final boolean isUseAlternativeRenderCheck()
	{
		return useAlternativeRenderCheck;
	}

	/**
	 * Sets which mode to use. In regular mode this check behaves as a
	 * {@link ClassSecurityCheck}, using the same check for render and enabled.
	 * In alternative mode this check behaves as a
	 * {@link ComponentSecurityCheck} for render actions and as a
	 * {@link ClassSecurityCheck} for enabled actions.
	 * 
	 * @param useAlternativeRenderCheck
	 *            true, if you want to use the alternative mode.
	 * @return this
	 */
	public final LinkSecurityCheck setUseAlternativeRenderCheck(boolean useAlternativeRenderCheck)
	{
		this.useAlternativeRenderCheck = useAlternativeRenderCheck;
		return this;
	}

}
