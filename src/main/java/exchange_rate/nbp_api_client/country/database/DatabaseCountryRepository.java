package exchange_rate.nbp_api_client.country.database;

import javax.persistence.NoResultException;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.hibernate.type.StringType;

import exchange_rate.nbp_api_client.CountryName;
import exchange_rate.nbp_api_client.country.CountryRepository;
import exchange_rate.nbp_api_client.country.database.entity.CountryEntity;
import exchange_rate.nbp_api_client.country.database.mapper.DatabaseCountryMapper;
import exchange_rate.nbp_api_client.database.exception.DatabaseException;
import exchange_rate.nbp_api_client.database.util.HibernateUtil;
import exchange_rate.nbp_api_client.dto.Country;

public class DatabaseCountryRepository implements CountryRepository {

	private DatabaseCountryMapper mapper = new DatabaseCountryMapper();

	@Override
	public void finalize() {
		HibernateUtil.shutdown();
	}

	@Override
	public Country get(CountryName countryName) {
		CountryEntity entity = readEntity(countryName);
		if (entity != null) {
			return mapper.map(entity);
		}
		return null;
	}

	@Override
	public void save(Country country) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		CountryEntity entity = readEntity(country.getName());
		if (entity != null) {
			throw new DatabaseException("Found data for country name. If you want update use update() method.");
		}
		session.beginTransaction();
		entity = mapper.map(country);
		session.save(entity);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public void update(Country country) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		CountryEntity entity = readEntity(country.getName());
		if (entity == null) {
			throw new DatabaseException("Data not found. Cannot update. If You want save data use save() method.");
		}
		entity.setCurrencies(country.getCurrencies());
		session.beginTransaction();
		session.update(entity);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public void delete(Country country) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		CountryEntity entity = readEntity(country.getName());
		if (entity == null) {
			throw new DatabaseException("Data not found. Cannot delete.");
		}
		session.beginTransaction();
		session.delete(entity);
		session.getTransaction().commit();
		session.close();
	}

	private CountryEntity readEntity(CountryName countryName) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query<CountryEntity> query = session.createQuery("FROM country WHERE country_name = :country_name",
				CountryEntity.class);
		query.setParameter("country_name", countryName.name(), StringType.INSTANCE);
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		} finally {
			session.close();
		}
	}
}
