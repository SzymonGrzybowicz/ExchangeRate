package exchange_rate.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import exchange_rate.database.util.HibernateUtil;

public class Database {

	private Database() {
	}

	private static Database instance;

	public static Database getInstance() {
		if (instance == null) {
			instance = new Database();
		}
		return instance;
	}

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
