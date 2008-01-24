/*
 * $Revision: 5004 $
 * $Date: 2006-03-17 20:47:08 -0800 (Fri, 17 Mar 2006) $
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
package org.wicketstuff.openlayers.event;

import org.apache.wicket.Request;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.wicketstuff.openlayers.api.LonLat;

/**
 * TODO should we put 'click' and 'dblclkick' together in this listener?
 */
public abstract class DblClickListener extends EventListenerBehavior
{

	@Override
	protected String getEvent() {
		return "dblclick";
	}

	@Override
	protected final void onEvent(AjaxRequestTarget target) {
		Request request = RequestCycle.get().getRequest();

		LonLat latLng = null;
		
		String latLngParameter = request.getParameter("latLng");
		if (latLngParameter != null) {
			latLng = LonLat.parse(latLngParameter);
		}

		onDblClick(target, latLng);
	}

	/**
	 * Override this method to provide handling of a click on the map.
	 * 
	 * @param latLng
	 *            the clicked GLatLng
	 * @param target
	 *            the target that initiated the click
	 */
	protected abstract void onDblClick(AjaxRequestTarget target, LonLat latLng);
}