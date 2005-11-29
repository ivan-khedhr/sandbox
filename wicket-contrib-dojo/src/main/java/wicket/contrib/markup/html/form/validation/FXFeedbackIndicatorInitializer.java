/*
 * $Id$ $Revision$ $Date$
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
package wicket.contrib.markup.html.form.validation;

import wicket.Application;
import wicket.IInitializer;
import wicket.markup.html.PackageResource;

/**
 * Innitializer class for FXFeedbackIndicator
 * 
 * @author Marco van de Haar
 */
public class FXFeedbackIndicatorInitializer implements IInitializer
{

	/**
	 * @see wicket.IInitializer#init(wicket.Application)
	 */
	public void init(Application application)
	{
		PackageResource.bind(application, FXFeedbackIndicator.class, "alert.gif");
	}

}
