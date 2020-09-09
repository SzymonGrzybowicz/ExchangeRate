package exchange_rate.nbp_api_client.database.exchange_rate.database;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.type.DateType;
import org.hibernate.type.StringType;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.database.exception.DatabaseException;
import exchange_rate.nbp_api_client.database.exchange_rate.entity.ExchangeRateEntity;
import exchange_rate.nbp_api_client.database.util.HibernateUtil;

public class ExchangeRateDatabase {

	private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

	public Session openNewSession() {
		return sessionFactory.openSession();
	}

	public void insert(ExchangeRateEntity entity, Session session) {
		if (checkExists(entity.getCurrency(), entity.getDate(), session)) {
			throw new DatabaseException(
					"Found data for that currency and date. If you want update use update() method.");
		}
		session.save(entity);
	}

	public void delete(ExchangeRateEntity entity, Session session) {
		if (!checkExists(entity.getCurrency(), entity.getDate(), session)) {
			throw new DatabaseException("Data not found. Cannot delete.");
		}
		session.delete(entity);
	}

	public ExchangeRateEntity read(Currency currency, Date date, Session session) {
		Query<ExchangeRateEntity> query = session.createQuery(
				"FROM exchange_rate WHERE date = :date AND currency = :currency", ExchangeRateEntity.class);
		query.setParameter("date", date, DateType.INSTANCE);
		query.setParameter("currency", currency.name(), StringType.INSTANCE);
		return query.getSingleResult();
	}

	public void update(ExchangeRateEntity entity, Session session) {

		if (checkExists(entity.getCurrency(), entity.getDate(), session)) {
			throw new DatabaseException("Data not found. Cannot update. If You want save data use save() method.");
		}

		session.update(entity);
	}

	public boolean checkExists(Currency currency, Date date, Session session) {
		Query<ExchangeRateEntity> query = session
				.createQuery("FROM exchange_rate WHERE date = :date AND currency = :currency", ExchangeRateEntity.class)
				.setMaxResults(1);
		query.setParameter("date", date, DateType.INSTANCE);
		query.setParameter("currency", currency.name(), StringType.INSTANCE);

		return query.uniqueResult() != null;
	}
}
