/*
 * $Id: CAttrApplication.java 459189 2006-02-11 22:45:09Z ehillenius $
 * $Revision: 459189 $
 * $Date: 2006-02-11 14:45:09 -0800 (Sat, 11 Feb 2006) $
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
package wicket.spring.cattr.web;

import wicket.spring.injection.cattr.CommonsAttributeSpringWebApplication;

/**
 * Application class for our examples
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public class CAttrApplication extends CommonsAttributeSpringWebApplication {

	/**
	 * @see wicket.Application#getHomePage()
	 */
	public Class getHomePage() {
		return CommonsAttributePage.class;
	}

}
