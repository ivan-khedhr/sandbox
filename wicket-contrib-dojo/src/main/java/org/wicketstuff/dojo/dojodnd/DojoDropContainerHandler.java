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
package org.wicketstuff.dojo.dojodnd;

import org.apache.wicket.IRequestTarget;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.wicketstuff.dojo.templates.DojoPackagedTextTemplate;
import org.apache.wicket.markup.html.IHeaderResponse;

/**
 * Handler for a {@link DojoDropContainer}
 * @author <a href="http://www.demay-fr.net/blog">Vincent Demay</a>
 *
 */

@SuppressWarnings("serial")
class DojoDropContainerHandler extends AbstractDojoDropContainerHandler
{
	/**
	 * 
	 */
	public void renderHead(IHeaderResponse response)
	{
		super.renderHead(response);

		DojoPackagedTextTemplate template = new DojoPackagedTextTemplate(this.getClass(), "DojoDropContainerHandlerTemplate.js");

		response.renderJavascript(template.asString(), template.getStaticKey());
	
		IRequestTarget target = RequestCycle.get().getRequestTarget();
		if(!(target instanceof AjaxRequestTarget)){
			DojoDropContainer container = getDojoDropContainer();
			response.renderJavascript("dojo.event.connect(dojo, \"loaded\", function() {initDrop('" + container.getMarkupId() + "', " + acceptIdsToJavaScriptArray() + ", '" + getCallbackUrl() + "'); });" , container.getMarkupId() + "onLoad");
		}
	}
	
	public void onComponentReRendered(AjaxRequestTarget ajaxTarget)
	{
		super.onComponentReRendered(ajaxTarget);
		
		DojoDropContainer container = getDojoDropContainer();
		ajaxTarget.appendJavascript("initDrop('" + container.getMarkupId() + "', " + acceptIdsToJavaScriptArray() + ", '" + getCallbackUrl() + "');\n");
	}
}
