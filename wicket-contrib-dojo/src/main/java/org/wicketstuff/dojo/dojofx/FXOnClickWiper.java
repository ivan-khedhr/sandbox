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
package org.wicketstuff.dojo.dojofx;


import java.util.StringTokenizer;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.model.Model;

/**
 * This classacts as an AjaxHandler an can be added to components. It adds a
 * dojo.lfx.html wiper to the class which reactes to a target component's ONCLICK
 * method.
 * 
 * TODO: We wanted to make a generic wiper that could also react to
 * ONMOUSEOVER/ONMOUSEOUT but the current dojoimplementation made our version
 * very unstable. If you want to help with this, or hear the problems we ran
 * into please give a yell on the mailing list. TODO: dojo.lfx.html currently
 * only supports top-down wiping, so down-top wiping and horizontal wiping is
 * currently not supported. We have however, requested this on the Dojo
 * animation wishlist. TODO: streamlining javascript handling: see
 * renderHeadContribution(HtmlHeaderContainer container). TODO: Due to problems
 * with unique javascript function/variable naming (can't use componentpath for
 * vars and function names) wiper can theoretically not work (yet untested...)
 * with ListItems. The only thing unique about ListItems is their component
 * path....
 * 
 * @author Marco van de Haar
 * @author Ruud Booltink
 * 
 */
public class FXOnClickWiper extends DojoFXHandler
{

	/**
	 * true if wiping component should be displaid from beginning.
	 */
	private final boolean startDisplay;
	private String HTMLID;
	private String componentId;

	/**
	 * Constructor with custom startDisplay
	 * 
	 * @param trigger
	 *            Component which'ONCLICK triggers teh wiping effect
	 * @param duration
	 *            duration for the animation.
	 * @param startDisplay
	 *            whether the wiping component starts wiped in or wiped
	 *            out(wiped in means vissible)
	 */
	public FXOnClickWiper(int duration, Component trigger, boolean startDisplay)
	{
		super("onclick", duration, trigger);
		this.startDisplay = startDisplay;

	}
	
	/**
	 * @return The html id
	 */
	public String getHTMLID()
	{
		//only returns valid value after handler is bound.
		return this.HTMLID;
	}

	/**
	 * Constructor with default startDisplay
	 * 
	 * @param trigger
	 *            Omponent which'ONCLICK triggers teh wiping effect
	 * @param duration
	 *            duration for the animation.
	 */
	public FXOnClickWiper(int duration, Component trigger)
	{
		super("onclick", duration, trigger);
		this.startDisplay = false;
	}


	/* (non-Javadoc)
	 * @see wicket.contrib.dojo.DojoAjaxHandler#renderHead(wicket.markup.html.IHeaderResponse)
	 */
	public void renderHead(IHeaderResponse response)
	{
		super.renderHead(response);
		String s;
		if (startDisplay)
		{
			s = "\t<script language=\"JavaScript\" type=\"text/javascript\">\n" + "\t"
					+ getHTMLID() + "_wipedOut = 0; \n";
		}
		else
		{
			s = "\t<script language=\"JavaScript\" type=\"text/javascript\">\n" + "\t"
					+ getHTMLID() + "_wipedOut = 1; \n";
		}

		s = s + "\t" + getHTMLID() + "_wiping = 0; \n" + "\tfunction " + getHTMLID()
				+ "_wipe(id, duration) {\n" + "\t\tif(" + getHTMLID() + "_wiping==0){\n"
				+ "\t\t\tnode = document.getElementById(id);\n" + "\t\t\tif(" + getHTMLID()
				+ "_wipedOut == 1) \n" + "\t\t\t{\n" + "\t\t\t\t" + getHTMLID() + "_wiping = 1;\n"
				+ "\t\t\t\t" + getHTMLID() + "_wipedOut = 0;\n"
				+ "\t\t\t\tdojo.lfx.html.wipeIn(node.id, duration,null, function(){" + getHTMLID()
				+ "_wiping=0; node.style.height='auto';}).play();\n" + "\t\t\t} else {\n" + "\t\t\t\t"
				+ getHTMLID() + "_wiping = 1;\n" + "\t\t\t\t" + getHTMLID() + "_wipedOut = 1;\n"
				+ "\t\t\t\tdojo.lfx.html.wipeOut(node.id, duration,null,  function(){" + getHTMLID()
				+ "_wiping=0; node.style.height='auto';}).play();\n" + "\t\t\t}\n" + "\t\t}\n" + "\t}\n"
				+ "\t</script>\n";


		response.renderString(s);
	}

	/*
	 * @see wicket.AjaxHandler#getBodyOnloadContribution()
	 */
	protected String getBodyOnloadContribution()
	{
		// You might want to add dojo.event.connect calls here to bind
		// animations to functions.
		// * currently however, this is done by Attributemodifiers in onBind();

		// return "init();";
		return null;
	}

	/**
	 * method to remove colons from string s.
	 * @param s string to parse
	 * @return string wichtout colons
	 */
	public String removeColon(String s) {
		  StringTokenizer st = new StringTokenizer(s,":",false);
		  String t="";
		  while (st.hasMoreElements()) t += st.nextElement();
		  return t;
	  }
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see wicket.AjaxHandler#onBind()
	 */
	protected void onBind()
	{
		Component c = getComponent();
		this.component = (Component)c;
		this.componentId = c.getId();

		// create a unique HTML for the wipe component
		String componentpath = removeColon(component.getPath());
		this.HTMLID = "f_" + this.component.getId() + "_" + componentpath;
		// Add ID to component, and bind effect to trigger
		this.component.add(new AttributeModifier("id", true, new Model(getHTMLID())));
		if (!startDisplay)
		{
			this.component
					.add(new AppendAttributeModifier("style", true, new Model("display:none")));
		}
		this.getTrigger().add(
				new AppendAttributeModifier(getEventName(), true, new Model(getHTMLID() + "_wipe('"
						+ HTMLID + "', " + getDuration() + ");")));

	}



}
