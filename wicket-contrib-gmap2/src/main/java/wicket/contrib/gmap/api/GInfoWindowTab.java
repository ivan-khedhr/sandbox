/*
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.contrib.gmap.api;

import org.apache.wicket.Component;

/**
 * Represents an Google Maps API's GInfoWindowTab. <a
 * href="http://www.google.com/apis/maps/documentation/reference.html#GInfoWindowTab">GInfoWindowTab</a>
 * 
 */
public class GInfoWindowTab implements GMapApi 
{

	/**
	 * Default serialVersionUID.
	 */
	private static final long serialVersionUID = 1L;
	
	private final String title;
	private final Component content;

	/**
	 * Construct.
	 * 
	 * @param lat
	 * @param lng
	 */
	public GInfoWindowTab(Component content)
	{
		this(content.getId(), content);
	}

	/**
	 * Construct.
	 * 
	 * @param lat
	 * @param lng
	 */
	public GInfoWindowTab(String title, Component content)
	{
		this.title = title;
		this.content = content;
		
		content.setOutputMarkupId(true);
	}

	public String getTitle()
	{
		return title;
	}

	public Component getContent()
	{
		return content;
	}
	
	/**
	 * @return A JavaScript constructor that represents this element.
	 */
	public String getJSConstructor()
	{
		return "new GInfoWindowTab('" + title + "', document.getElementById('" + content.getMarkupId() + "'))";
	}
}
