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
package org.wicketstuff.table.cell.components;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

/**
 * Simple TextField extension that don't require an input tag and the type
 * attribute on the template markup.
 * 
 * @author Pedro Henrique Oliveira dos Santos
 * 
 */
public class LenientTextField extends TextField
{
	public LenientTextField(String id)
	{
		super(id);
	}


	public LenientTextField(String id, Class type)
	{
		super(id, type);
	}


	public LenientTextField(String id, IModel model, Class type)
	{
		super(id, model, type);
	}


	public LenientTextField(String id, IModel model)
	{
		super(id, model);
	}


	@Override
	protected void onComponentTag(final ComponentTag tag)
	{
		tag.setName("input");
		tag.put("type", "text");
		super.onComponentTag(tag);
	}
}