/*
 * $Id$
 * $Revision$ $Date$
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
package wicket.contrib.database;

/**
 * Abstract base class for Database DAOs
 * 
 * @author Jonathan Locke
 */
public abstract class DatabaseDao
{
	private DatabaseSession session;
	
	/**
	 * Constructor
	 * @param session Database session for this DAO
	 */
	public DatabaseDao(DatabaseSession session)
	{
		setSession(session);
	}

	/**
	 * @param session
	 *            The database session that this DAO uses
	 */
	public void setSession(DatabaseSession session)
	{
		this.session = session;
	}
	
	/**
	 * @return Database session
	 */
	public DatabaseSession getSession()
	{
		return session;
	}
}
