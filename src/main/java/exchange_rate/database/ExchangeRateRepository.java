package exchange_rate.database;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import exchange_rate.database.entity.ExchangeRateEntity;
import exchange_rate.database.util.HibernateUtil;

public class ExchangeRateRepository {

	private Session session = HibernateUtil.getSessionFactory().openSession();
	private Logger log = Logger.getLogger(this.getClass().getName());

	@Override
	public void finalize() throws Throwable {
		session.close();
		super.finalize();
	}

	public long save(ExchangeRateEntity entity) {
		log.info("Save(" + entity + ")");
		session.beginTransaction();
		session.save(entity);
		session.getTransaction().commit();
		return entity.getId();
	}

	public void delete(ExchangeRateEntity entity) {
		log.info("Delete(" + entity + ")");
		session.beginTransaction();
		session.delete(entity);
		session.getTransaction().commit();
	}

	public ExchangeRateEntity read(Long id) {
		log.info("Read(" + id + ")");
		return session.get(ExchangeRateEntity.class, id);
	}

	public long update(ExchangeRateEntity entity) {
		log.info("Update(" + entity + ")");
		session.beginTransaction();
		session.update(entity);
		session.getTransaction().commit();
		return entity.getId();
	}

	public List<ExchangeRateEntity> getAllRecords() {
		log.info("GetAllRecords()");
		session.beginTransaction();
		List<ExchangeRateEntity> result = session.createQuery("from ExchangeRate", ExchangeRateEntity.class).list();
		session.getTransaction().commit();
		return result;
	}
}
