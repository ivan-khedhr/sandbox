/*
 * $Id$ $Revision$ $Date$
 * 
 * ==================================================================== Licensed
 * under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.examples.cdapp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import wicket.IRequestCycleFactory;
import wicket.Page;
import wicket.Request;
import wicket.RequestCycle;
import wicket.Response;
import wicket.Session;
import wicket.examples.WicketExampleApplication;
import wicket.examples.cdapp.util.DatabaseUtil;
import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WebRequest;
import wicket.protocol.http.WebSession;

/**
 * Wicket test application.
 * 
 * @author Eelco Hillenius
 */
public class CdApplication extends WicketExampleApplication implements IRequestCycleFactory
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/** Logger. */
	private static Log log = LogFactory.getLog(CdApplication.class);

	private SessionFactory sessionFactory;

	/**
	 * Constructor
	 */
	public CdApplication()
	{
	}

	/**
	 * @see wicket.protocol.http.WebApplication#init()
	 */
	@Override
	protected void init()
	{
		getResourceSettings().setThrowExceptionOnMissingResource(false);

		setSessionFactory(this);

		try
		{
			final Configuration configuration = new Configuration();
			configuration.configure();
			sessionFactory = configuration.buildSessionFactory();
			new DatabaseUtil(configuration).createDatabase();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * @see wicket.Application#getHomePage()
	 */
	@Override
	public Class< ? extends Page> getHomePage()
	{
		return Home.class;
	}

	/**
	 * @see wicket.protocol.http.WebApplication#newSession()
	 */
	@Override
	public Session newSession()
	{
		return new CdAppSession(CdApplication.this);
	}

	/**
	 * Session object for cdapp.
	 */
	private static class CdAppSession extends WebSession
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * @param application
		 */
		public CdAppSession(WebApplication application)
		{
			super(application);
		}

		/**
		 * @see wicket.protocol.http.WebSession#getRequestCycleFactory()
		 */
		@Override
		protected IRequestCycleFactory getRequestCycleFactory()
		{
			return (CdApplication)getApplication();
		}
	}

	/**
	 * @see wicket.IRequestCycleFactory#newRequestCycle(wicket.Session,
	 *      wicket.Request, wicket.Response)
	 */
	public RequestCycle newRequestCycle(Session session, Request request, Response response)
	{
		return new CdAppRequestCycle((WebSession)session, (WebRequest)request, response,
				sessionFactory);
	}
}