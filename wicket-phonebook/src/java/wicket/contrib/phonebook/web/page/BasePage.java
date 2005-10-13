/*
 * $Id$
 * $Revision$
 * $Date$
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
package wicket.contrib.phonebook.web.page;

import wicket.contrib.phonebook.ContactDao;
import wicket.contrib.phonebook.web.PhonebookApplication;
import wicket.markup.html.WebPage;

/**
 * Extends {@link WebPage} in order to provide the {@link #getContactDao}
 * method.
 *
 * @author igor
 */
public class BasePage extends WebPage {
	/**
	 * Gets the Contact DAO instance.
	 * @return The Contact DAO instance.
	 */
	protected ContactDao getContactDao() {
		return PhonebookApplication.getInstance().getContactDao();
	}
}
