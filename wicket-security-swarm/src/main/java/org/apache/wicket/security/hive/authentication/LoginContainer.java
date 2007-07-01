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
package org.apache.wicket.security.hive.authentication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.model.IModel;
import org.apache.wicket.security.strategies.LoginException;

/**
 * Container class for multiple {@link LoginContext}s. Allows the concept of
 * multi-level login. Note this class is not thread safe.
 * 
 * @author marrink
 */
public final class LoginContainer implements Serializable
{
	private static final long serialVersionUID = 1L;

	private List logins = new ArrayList();

	private Map subjects = new HashMap();

	private Subject subject = null;

	private HashKey preventsLogin = null;

	/**
	 * Attempts to login through the context, if successful the subject and all
	 * its rights are included in the overall user rights. When successful this
	 * binds the session if not already bound and marks it dirty to support
	 * cluster replication.
	 * 
	 * @param context
	 * @throws LoginException
	 *             if the login fails
	 * @see LoginContext#login()
	 */
	public void login(LoginContext context) throws LoginException
	{
		if (preventsLogin != null)
			throw new LoginException("Additional Logins are not allowed");
		if (context == null)
			throw new LoginException("Context is required to login.");
		HashKey key = new HashKey(context);
		if (subjects.containsKey(key))
			throw new LoginException("Already logged in through this context ")
					.setLoginContext(context);
		Subject mySubject = context.login();
		if (mySubject == null)
			throw new LoginException("Login failed ").setLoginContext(context);
		mySubject.setReadOnly();
		if (key.preventsAdditionalLogins())
			preventsLogin = key;
		subjects.put(key, mySubject);
		logins.add(key);
		Collections.sort(logins);
		this.subject = new MultiSubject(logins, subjects);
		Session session = Session.get();
		if (session.isTemporary())
			session.bind();
		else
			session.dirty();
	}

	/**
	 * Removes the subject and all its rights associated with a certain context
	 * from this container.
	 * 
	 * @param context
	 * @return true if the logoff was successful, false otherwise
	 */
	public boolean logoff(LoginContext context)
	{
		if (context == null)
			return false;
		HashKey key = new HashKey(context);
		if (subjects.remove(key) != null)
		{
			if (preventsLogin != null && preventsLogin.equals(key))
				preventsLogin = null;
			logins.remove(key);
			if (logins.isEmpty())
				subject = null;
			else
				subject = new MultiSubject(logins, subjects);
			Session.get().dirty();
			return true;
		}
		return false;
	}

	/**
	 * returns an immutable Subject which Contains all the principals of the
	 * subjects in the loginContexts. Note that the Subject is replaced by a new
	 * one if a login or logoff is performed, so don't keep a reference to this
	 * subject any longer then required.
	 * 
	 * @return a subject
	 */
	public Subject getSubject()
	{
		return subject;
	}

	/**
	 * Returns the number of {@link Subject}s contained here.
	 * 
	 * @return the size
	 */
	public int size()
	{
		return logins.size();
	}

	/**
	 * Queries all available subjects (descending sort order) for the
	 * authentication of a model.
	 * 
	 * @param model
	 *            the model
	 * @param component
	 *            the component holding the model
	 * @return true if at least one of the registered subjects authenticates the
	 *         model, false otherwise.
	 * @see Subject#isModelAuthenticated(IModel, Component)
	 */
	public boolean isModelAuthenticated(IModel model, Component component)
	{
		return subject == null ? false : subject.isModelAuthenticated(model, component);
	}

	/**
	 * Queries all available subjects (descending sort order) for the
	 * authentication of a component.
	 * 
	 * @param component
	 *            the component
	 * @return true if at least one of the registered subjects authenticates the
	 *         component, false otherwise.
	 * @see Subject#isComponentAuthenticated(Component)
	 */
	public boolean isComponentAuthenticated(Component component)
	{
		return subject == null ? false : subject.isComponentAuthenticated(component);
	}

	/**
	 * Queries all available subjects (descending sort order) for the
	 * authentication of a class.
	 * 
	 * @param clazz
	 *            the (component) class
	 * @return true if at least one of the registered subjects authenticates the
	 *         class, false otherwise.
	 * @see Subject#isClassAuthenticated(Class)
	 */
	public boolean isClassAuthenticated(Class clazz)
	{
		return subject == null ? false : subject.isClassAuthenticated(clazz);
	}

	/**
	 * Subject merging all the subjects from the logincontainer into one. This
	 * subject is not fully backed by the logincontainer.
	 * 
	 * @author marrink
	 */
	private static final class MultiSubject implements Subject
	{
		private static final long serialVersionUID = 1L;

		private Set principals;

		private Set readOnlyPrincipals;

		private final List keys;

		private final Map mySubjects;

		/**
		 * Creates a new MultiSubject containing only the principals of the
		 * subjects at this time.
		 * 
		 * @param keys
		 *            keys of the logincontexts
		 * @param values
		 *            subjects mapped to keys
		 * 
		 */
		public MultiSubject(List keys, Map values)
		{
			super();
			// TODO make this subject fully backed by the logincontainer or not
			// at all and then copy the list and map. because right now the
			// principals don't change
			this.keys = keys;
			this.mySubjects = values;
			principals = new HashSet(100);
			Iterator subjects = values.values().iterator();
			while (subjects.hasNext())
			{
				principals.addAll(((Subject)subjects.next()).getPrincipals());
			}
			readOnlyPrincipals = Collections.unmodifiableSet(principals);
		}

		/**
		 * 
		 * @see org.apache.wicket.security.hive.authentication.Subject#getPrincipals()
		 */
		public Set getPrincipals()
		{
			return readOnlyPrincipals;
		}

		/**
		 * This subject is read only.
		 * 
		 * @return true
		 * @see org.apache.wicket.security.hive.authentication.Subject#isReadOnly()
		 */
		public boolean isReadOnly()
		{
			return true;
		}

		/**
		 * Dummy operation, the subject is already read only.
		 * 
		 * @see org.apache.wicket.security.hive.authentication.Subject#setReadOnly()
		 */
		public void setReadOnly()
		{
			// noop
		}

		/**
		 * Queries all available subjects (higher levels first) for the
		 * authentication of a model.
		 * 
		 * @param model
		 *            the model
		 * @param component
		 *            the component holding the model
		 * @return true if at least one of the registered subjects authenticates
		 *         the model, false otherwise.
		 * @see Subject#isModelAuthenticated(IModel, Component)
		 */
		public boolean isModelAuthenticated(IModel model, Component component)
		{
			HashKey ctx = null;
			for (int i = 0; i < keys.size(); i++)
			{
				ctx = (HashKey)keys.get(i);
				if (((Subject)mySubjects.get(ctx)).isModelAuthenticated(model, component))
					return true;
			}
			return false;
		}

		/**
		 * Queries all available subjects (higher levels first) for the
		 * authentication of a component.
		 * 
		 * @param component
		 *            the component
		 * @return true if at least one of the registered subjects authenticates
		 *         the component, false otherwise.
		 * @see Subject#isComponentAuthenticated(Component)
		 */
		public boolean isComponentAuthenticated(Component component)
		{
			HashKey ctx = null;
			for (int i = 0; i < keys.size(); i++)
			{
				ctx = (HashKey)keys.get(i);
				if (((Subject)mySubjects.get(ctx)).isComponentAuthenticated(component))
					return true;
			}
			return false;
		}

		/**
		 * Queries all available subjects (higher levels first) for the
		 * authentication of a class.
		 * 
		 * @param clazz
		 *            the (component) class
		 * @return true if at least one of the registered subjects authenticates
		 *         the class, false otherwise.
		 * @see Subject#isClassAuthenticated(Class)
		 */
		public boolean isClassAuthenticated(Class clazz)
		{
			HashKey ctx = null;
			for (int i = 0; i < keys.size(); i++)
			{
				ctx = (HashKey)keys.get(i);
				if (((Subject)mySubjects.get(ctx)).isClassAuthenticated(clazz))
					return true;
			}
			return false;
		}
	}
	/**
	 * Simple key for storing the hashcode, sort order and
	 * preventsAdditionalLogins flag of a {@link LoginContext} As it is better
	 * to not keep a reference to the context especially when it contains user
	 * credentials.
	 * 
	 * @author marrink
	 */
	private static final class HashKey implements Serializable, Comparable
	{
		private static final long serialVersionUID = 1L;

		private final int contextHash;
		private final boolean preventsAdditionalLogin;
		private int sortOrder;

		/**
		 * Construct. a new key based on the context
		 * 
		 * @param context
		 *            the logincontext
		 */
		public HashKey(LoginContext context)
		{
			contextHash = context.hashCode();
			preventsAdditionalLogin = context.preventsAdditionalLogins();
			this.sortOrder = context.getSortOrder();
		}

		/**
		 * Compares contexts by level.
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		public int compareTo(Object arg0)
		{
			if (arg0 instanceof HashKey)
			{
				HashKey lc0 = (HashKey)arg0;
				return sortOrder - lc0.sortOrder;
			}
			throw new IllegalArgumentException("Can only compare with " + LoginContext.class
					+ " not with " + arg0);
		}

		/**
		 * Gets contextHash.
		 * 
		 * @return contextHash
		 */
		public int getContextHash()
		{
			return contextHash;
		}

		/**
		 * Gets preventsLogin.
		 * 
		 * @return preventsLogin
		 */
		public boolean preventsAdditionalLogins()
		{
			return preventsAdditionalLogin;
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode()
		{
			final int PRIME = 31;
			int result = 1;
			result = PRIME * result + contextHash;
			result = PRIME * result + (preventsAdditionalLogin ? 1231 : 1237);
			return result;
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final HashKey other = (HashKey)obj;
			if (contextHash != other.contextHash)
				return false;
			if (preventsAdditionalLogin != other.preventsAdditionalLogin)
				return false;
			return true;
		}
	}
}
