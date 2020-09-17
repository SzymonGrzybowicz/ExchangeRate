package training.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

@Component
public class Database {

	private SessionFactory sessionFactory;

	public Database(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public <T> T execute(UnitOfWork<T> unitOfWork) {
		Session session = null;
		try {
			session = sessionFactory.openSession();
			session.beginTransaction();
			T result = unitOfWork.run(session);
			session.getTransaction().commit();
			session.close();
			return result;
		} catch (Exception e) {
			if (session != null) {
				Transaction transaction = session.getTransaction();
				if (transaction != null) {
					transaction.rollback();
				}
				session.close();
			}
			throw e;
		}
	}
}
