package exchange_rate.database;

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
		session.save(entity);
		return entity.getId();
	}

	public void delete(ExchangeRateEntity entity) {
		log.info("Delete(" + entity + ")");
		session.delete(entity);
	}

	public ExchangeRateEntity read(Long id) {
		log.info("Read(" + id + ")");
		return session.get(ExchangeRateEntity.class, id);
	}

	public long update(ExchangeRateEntity entity) {
		log.info("Update(" + entity + ")");
		session.update(entity);
		return entity.getId();
	}
}
