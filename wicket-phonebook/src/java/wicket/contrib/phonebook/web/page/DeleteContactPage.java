/*
 * $Id: DeleteContactPage.java 634 2006-03-26 18:28:10 -0800 (Sun, 26 Mar 2006) ivaynberg $
 * $Revision: 634 $
 * $Date: 2006-03-26 18:28:10 -0800 (Sun, 26 Mar 2006) $
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

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.collections.MicroMap;
import org.apache.wicket.util.string.interpolator.MapVariableInterpolator;

import wicket.contrib.phonebook.Contact;
import wicket.contrib.phonebook.ContactDao;

/**
 * Delete the Contact.
 *
 * @author igor
 */
public class DeleteContactPage<T extends Contact> extends BasePage<T> {
	private final Page<T> backPage;
	@SpringBean(name = "contactDao")
	private ContactDao contactDao;

	/**
	 * Constructor. Display the summary (names) before asking for confirmation.
	 * Note that if you don't need the page to be bookmarkable, you can use
	 * whatever constructor you need, such as is done here.
	 *
	 * @param backPage
	 *            The page that the user was on before coming here
	 * @param contact
	 *            Model that contains the contact to be deleted
	 */
	public DeleteContactPage(Page<T> backPage, IModel<T> contact) {
		this.backPage = backPage;
		setModel(contact);
		add(new Label<String>("name", getContact().getFullName()));
		addConfimButton();
		addCancelButton();
	}

	private void addConfimButton() {
		/*
		 * notice in mark-up this link is attached to <input type='button'/>
		 * tag, the link is smart enough to know to generate an onclick instead
		 * of href
		 */
		//TODO value attribute of 'input' tag is not set
		add(new Link<String>("confirm", new ResourceModel("confirm")) {
			private static final long serialVersionUID = 1L;
			@Override
			public void onClick() {
				final Contact deleted = getContact();
				contactDao.delete(deleted.getId());
				String msg = MapVariableInterpolator.interpolate(getLocalizer()
						.getString("status.deleted", this), new MicroMap<String, String>(
						"name", deleted.getFullName()));
				getSession().info(msg);
				setResponsePage(DeleteContactPage.this.backPage);
			}
		});
	}

	//TODO value attribute of 'input' tag is not set
	private void addCancelButton() {
		add(new Link<String>("cancel", new ResourceModel("cancel")) {
			private static final long serialVersionUID = 1L;
			@Override
			public void onClick() {
				String msg = MapVariableInterpolator.interpolate(getLocalizer()
						.getString("status.cancelled", this), new MicroMap<String, String>(
						"name", getContact().getFullName()));
				getSession().info(msg);
				setResponsePage(DeleteContactPage.this.backPage);
			}
		});
	}

	/**
	 * Type-safe way to retrieve the contact from the page's model
	 *
	 * @return <code>Contact</code> instance stored in model
	 */
	private T getContact() {
		return getModelObject();
	}
}
