package persistence;

import org.apache.log4j.Logger;
import persistence.domain.Message;

public class DaoTest extends TestSupport {

	private Logger log = Logger.getLogger(DaoTest.class);

	public void testIsSomethingWorking() {
		Message message = new Message("Hello world");
		generalDao.persist(message);

		assertEquals(1, generalDao.getMessages().size());
	}

}
