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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.wicketstuff.dojo.AbstractRequireDojoBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;

/**
 * Dojo drag container
 * <p>
 * 	A drag container is a HTML container used to define a Drag area.
 *  This area is associated with a pattern. This pattern is used to know 
 *  if a DojoDragContainer can be drag and drop on a {@link DojoDropContainer} which 
 *  contains a list of pattern
 * </p>
 * <p>
 * 	<b>Sample</b>
 *  <pre>
 * package org.wicketstuff.dojo.examples;
 * 
 * import org.apache.wicket.PageParameters;
 * import org.wicketstuff.dojo.dojodnd.DojoDragContainer;
 * import org.wicketstuff.dojo.dojodnd.DojoDropContainer;
 * import org.apache.wicket.markup.html.WebPage;
 * import org.apache.wicket.markup.html.image.Image;
 * 
 * public class DnDShower extends WebPage {
 * 	
 * 	public DnDShower(PageParameters parameters){
 * 		DojoDropContainer dropContainer = new DojoDropContainer("dropContainer"){
 * 		
 * 			public void onDrop(DojoDragContainer container, int position) {
 * 				System.out.println("position = " + position);
 * 				System.out.println("DojoDragContainer" + container.getId());
 * 				
 * 			}
 * 		
 * 		};
 * 		add(dropContainer);
 * 		
 * 		DojoDragContainer dragContainer1 = new DojoDragContainer("dragContainer1");
 * 		DojoDragContainer dragContainer2 = new DojoDragContainer("dragContainer2");
 * 		DojoDragContainer dragContainer3 = new DojoDragContainer("dragContainer3");
 * 		add(dragContainer1);
 * 		add(dragContainer2);
 * 		add(dragContainer3);
 * 		
 * 		
 * 		DojoDragContainer dragContainer4 = new DojoDragContainer("dragContainer4");
 * 		DojoDragContainer dragContainer5 = new DojoDragContainer("dragContainer5");
 * 		dropContainer.add(dragContainer4);
 * 		dropContainer.add(dragContainer5);
 * 		
 * 		dragContainer1.add(new Image("pic1"));
 * 		dragContainer2.add(new Image("pic2"));
 * 		dragContainer3.add(new Image("pic3"));
 * 		dragContainer4.add(new Image("pic4"));
 * 		dragContainer5.add(new Image("pic5"));
 * 	}
 * }
 *  </pre>
 * </p>
 * @author <a href="http://www.demay-fr.net/blog/index.php/en">Vincent Demay</a>
 *
 */

@SuppressWarnings("serial")
public class DojoDragContainer extends WebMarkupContainer
{

	private String dragId;
	
	/**
	 * Constructor of a drag container
	 * @param id widget id
	 */
	public DojoDragContainer(String id)
	{
		super(id);
		this.setOutputMarkupId(true);
		//all by default
		dragId = "*";
		add(createDragBehavior());
	}
	
	protected AbstractRequireDojoBehavior createDragBehavior() 
	{
		return new DojoDragContainerHandler();
	}
	 
	/**
	 * Drag Pattern
	 * @param pattern pattern use to allow to be dragged on a dropContainer with the same pattern
	 */
	public void setDragPattern(String pattern){
		this.dragId = pattern;
	}
	
	/**
	 * return pattern used to allow to be dragged on a dropContainer with the same pattern
	 * @return pattern used to allow to be dragged on a dropContainer with the same pattern
	 */
	public String getDragPattern(){
		return dragId;
	}

	/**
	 * This method is called when drag start
	 * @param target {@link AjaxRequestTarget}
	 */
	public void onDrag(AjaxRequestTarget target){}

}
