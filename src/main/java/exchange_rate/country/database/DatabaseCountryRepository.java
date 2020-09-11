package exchange_rate.country.database;

import javax.persistence.NoResultException;

import org.hibernate.Session;
import org.hibernate.query.Query;

import exchange_rate.country.CountryRepository;
import exchange_rate.country.database.entity.CountryEntity;
import exchange_rate.country.database.mapper.CountryMapper;
import exchange_rate.database.Database;
import exchange_rate.database.UnitOfWork;
import exchange_rate.downloader.nbp.exception.checked.NotFoundException;
import exchange_rate.downloader.nbp.exception.unchecked.BadRequestException;
import exchange_rate.dto.Country;
import exchange_rate.enums.CountryName;
import exchange_rate.enums.Currency;

public class DatabaseCountryRepository implements CountryRepository {

	public DatabaseCountryRepository(Database database) {
		this.database = database;
	}

	private final CountryMapper mapper = new CountryMapper();

	private final Database database;

	@Override
	public Country get(CountryName countryName) {
		UnitOfWork<CountryEntity> unitOfWork = (Session session) -> {
			try {
				return read(countryName, session);
			} catch (NoResultException e) {
				throw new NotFoundException("Cannot find data for country name: " + countryName + " in database");
			}
		};
		CountryEntity entity = database.execute(unitOfWork);
		return mapper.map(entity);
	}

	@Override
	public void save(Country country) {
		UnitOfWork<Void> unitOfWork = (Session session) -> {
			try {
				read(country.getName(), session);
				throw new BadRequestException(
						"Cannot save into database, found data for that country name. If you want update use update() method.");
			} catch (NoResultException e) {
				session.save(mapper.map(country));
				return null;
			}
		};

		database.execute(unitOfWork);
	}

	@Override
	public void addCurrency(CountryName countryName, Currency currency) {
		UnitOfWork<Void> unitOfWork = (Session session) -> {
			try {
				CountryEntity entity = read(countryName, session);
				if (entity.getCurrencies().contains(currency)) {
					throw new BadRequestException("Cannot add currency: " + currency + "to country with name: "
							+ countryName + " country already have this currency.");
				}

				entity.addCurrency(currency);
				session.update(entity);
				return null;

			} catch (NoResultException e) {
				throw new BadRequestException("Cannot add currency: " + currency + "to country with name: "
						+ countryName + " cannot find country.");
			}
		};

		database.execute(unitOfWork);
	}

	@Override
	public void removeCurrency(CountryName countryName, Currency currency) {
		UnitOfWork<Void> unitOfWork = (Session session) -> {
			try {
				CountryEntity entity = read(countryName, session);
				if (!entity.getCurrencies().contains(currency)) {
					throw new BadRequestException("Cannot remove currency: " + currency + "from country with name: "
							+ countryName + " country do not have this currency.");
				}

				entity.removeCurrency(currency);
				session.update(entity);
				return null;

			} catch (NoResultException e) {
				throw new BadRequestException("Cannot remove currency: " + currency + "from country with name: "
						+ countryName + " cannot find country.");
			}
		};

		database.execute(unitOfWork);
	}

	@Override
	public void delete(CountryName countryName) {

		UnitOfWork<Void> unitOfWork = (Session session) -> {
			try {
				CountryEntity entity = read(countryName, session);
				session.remove(entity);
				return null;
			} catch (NoResultException e) {
				throw new BadRequestException(
						"Cannot remove country with name: " + countryName + " cannot find country.");
			}
		};

		database.execute(unitOfWork);
	}

	private CountryEntity read(CountryName countryName, Session session) {
		Query<CountryEntity> query = session
				.createNamedQuery(CountryEntity.QUERY_GET_BY_COUNTRY_NAME, CountryEntity.class)
				.setParameter(CountryEntity.PARAMETER_NAME, countryName).setMaxResults(1);

		return query.getSingleResult();

	}
}
