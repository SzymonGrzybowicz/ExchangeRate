package exchange_rate.nbp_api_client.database.exchange_rate;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.NoResultException;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.type.DateType;
import org.hibernate.type.StringType;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.database.exception.DatabaseException;
import exchange_rate.nbp_api_client.database.exchange_rate.entity.ExchangeRateEntity;
import exchange_rate.nbp_api_client.database.exchange_rate.mapper.ExchangeRateEntityMapper;
import exchange_rate.nbp_api_client.database.util.HibernateUtil;
import exchange_rate.nbp_api_client.dto.ExchangeRate;

public class ExchangeRateRepository {

	private ExchangeRateEntityMapper mapper = new ExchangeRateEntityMapper();

	@Override
	public void finalize() {
		HibernateUtil.shutdown();
	}

	public void save(ExchangeRate exchangeRate) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		ExchangeRateEntity entity = readEntity(exchangeRate.getCurrency(), exchangeRate.getDate());
		if (entity != null) {
			throw new DatabaseException(
					"Found data for that currency and date. If you want update use update() method.");
		}
		session.beginTransaction();
		entity = mapper.map(exchangeRate);
		session.save(entity);
		session.getTransaction().commit();
		session.close();
	}

	public void delete(ExchangeRate exchangeRate) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		ExchangeRateEntity entity = readEntity(exchangeRate.getCurrency(), exchangeRate.getDate());
		if (entity == null) {
			throw new DatabaseException("Data not found. Cannot delete.");
		}
		session.beginTransaction();
		session.delete(entity);
		session.getTransaction().commit();
		session.close();
	}

	public ExchangeRate read(Currency currency, Date date) {
		ExchangeRateEntity entity = readEntity(currency, date);
		if (entity != null) {
			return mapper.map(entity);
		}
		return null;
	}

	public void update(ExchangeRate exchangeRate) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		ExchangeRateEntity entity = readEntity(exchangeRate.getCurrency(), exchangeRate.getDate());
		if (entity == null) {
			throw new DatabaseException("Data not found. Cannot update. If You want save data use save() method.");
		}
		entity.setRate(exchangeRate.getRate());
		session.beginTransaction();
		session.update(entity);
		session.getTransaction().commit();
		session.close();
	}

	public List<ExchangeRate> readAllRecords() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<ExchangeRateEntity> entityList = session.createQuery("from exchange_rate", ExchangeRateEntity.class)
				.list();
		session.close();
		return entityList.stream().map(e -> mapper.map(e)).collect(Collectors.toList());
	}

	private ExchangeRateEntity readEntity(Currency currency, Date date) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query<ExchangeRateEntity> query = session.createQuery(
				"FROM exchange_rate WHERE date = :date AND currency = :currency", ExchangeRateEntity.class);
		query.setParameter("date", date, DateType.INSTANCE);
		query.setParameter("currency", currency.name(), StringType.INSTANCE);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} finally {
			session.close();
		}
	}
}
