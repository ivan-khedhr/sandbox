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
package org.apache.wicket.security.examples.multilogin.pages;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.security.examples.components.navigation.ButtonContainer;
import org.apache.wicket.security.examples.multilogin.MySession;
import org.apache.wicket.security.examples.multilogin.entities.Entry;
import org.apache.wicket.security.examples.pages.MySecurePage;

/**
 * Page for starting some money transfers. Note that you will need to
 * commit these transfers on a 2nd page. which is more secured.
 * 
 * @author marrink
 */
public class InitiateTransferMoneyPage extends MySecurePage
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Construct.
	 */
	public InitiateTransferMoneyPage()
	{
		add(new ButtonContainer("buttoncontainer", ButtonContainer.BUTTON_TRANSACTION));
		final Form form = new Form("form", new CompoundPropertyModel(new Entry()));
		add(form);
		form.add(new Button("transfer")
		{
			private static final long serialVersionUID = 1L;

			/**
			 * @see org.apache.wicket.markup.html.form.Button#onSubmit()
			 */
			public void onSubmit()
			{
				// usually we would check to see if all required fieds are
				// entered etc
				MySession.getSessesion().getMoneyTransfers().add(form.getModelObject());
				form.setModelObject(new Entry());
			}
		});
		form.add(new Button("commit")
		{
			private static final long serialVersionUID = 1L;

			/**
			 * @see org.apache.wicket.markup.html.form.Button#onSubmit()
			 */
			public void onSubmit()
			{
				// usually we would check to see if all required fieds are
				// entered etc
				// we need to store the data temporarily in the session because
				// if we create the page now and pass the data to the
				// constructor we will be redirected back to the form after the
				// login. or we could use pageparams.
				MySession.getSessesion().getMoneyTransfers().add(form.getModelObject());
				setResponsePage(CommitTransferMoneyPage.class);
			}
		});
		form.add(new TextField("amount"));
		form.add(new TextField("to"));
		form.add(new TextField("owner"));
		form.add(new TextField("description"));
		form.add(new TextField("bank"));
		List accounts = new ArrayList(2);
		accounts.add("123456789");
		accounts.add("987654321");
		form.add(new DropDownChoice("from", new Model((Serializable)accounts)));
	}
}
