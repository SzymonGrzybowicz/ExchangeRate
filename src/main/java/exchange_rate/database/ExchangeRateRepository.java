package exchange_rate.database;

import org.hibernate.Session;

import exchange_rate.database.entity.ExchangeRateEntity;
import exchange_rate.database.util.HibernateUtil;

public class ExchangeRateRepository {

	private Session session = HibernateUtil.getSessionFactory().openSession();

	@Override
	public void finalize() throws Throwable {
		session.close();
		super.finalize();
	}

	public long save(ExchangeRateEntity entity) {
		session.save(entity);
		return entity.getId();
	}

	public void delete(ExchangeRateEntity entity) {
		session.delete(entity);
	}

	public ExchangeRateEntity read(Long id) {
		return session.get(ExchangeRateEntity.class, id);
	}

	public long update(ExchangeRateEntity entity) {
		session.update(entity);
		return entity.getId();
	}
}
