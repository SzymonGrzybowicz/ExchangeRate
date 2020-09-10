package exchange_rate.nbp_api_client.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import exchange_rate.nbp_api_client.database.util.HibernateUtil;

public class Database {

	@Override
	public void finalize() {
		HibernateUtil.shutdown();
	}

	public <T> T execute(UnitOfWork<T> unitOfWork) {
		Session session = null;
		try {
			session = openNewSession();
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

	private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

	private Session openNewSession() {
		return sessionFactory.openSession();
	}
}
