package exchange_rate.database;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import exchange_rate.database.entity.ExchangeRateEntity;
import exchange_rate.database.util.HibernateUtil;

public class ExchangeRateRepository {
	private Logger log = Logger.getLogger(this.getClass().getName());

	@Override
	public void finalize() {
		HibernateUtil.shutdown();
	}

	public long save(ExchangeRateEntity entity) {
		log.info("Save(" + entity + ")");
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		session.save(entity);
		session.getTransaction().commit();
		session.close();
		return entity.getId();
	}

	public void delete(ExchangeRateEntity entity) {
		log.info("Delete(" + entity + ")");
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		session.delete(entity);
		session.getTransaction().commit();
		session.close();
	}

	public ExchangeRateEntity read(Long id) {
		log.info("Read(" + id + ")");
		Session session = HibernateUtil.getSessionFactory().openSession();
		ExchangeRateEntity entity = session.get(ExchangeRateEntity.class, id);
		session.close();
		return entity;
	}

	public long update(ExchangeRateEntity entity) {
		log.info("Update(" + entity + ")");
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		session.update(entity);
		session.getTransaction().commit();
		session.close();
		return entity.getId();
	}

	public List<ExchangeRateEntity> getAllRecords() {
		log.info("GetAllRecords()");
		Session session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		List<ExchangeRateEntity> result = session.createQuery("from ExchangeRate", ExchangeRateEntity.class).list();
		session.getTransaction().commit();
		session.close();
		return result;
	}
}
