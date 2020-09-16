package training.country.database;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import training.country.CountryRepository;
import training.country.database.entity.CountryEntity;
import training.country.database.mapper.CountryMapper;
import training.country.dto.Country;
import training.database.Database;
import training.database.UnitOfWork;
import training.enums.CountryName;
import training.enums.Currency;
import training.exchange_rate.exception.checked.NotFoundException;
import training.exchange_rate.exception.unchecked.BadRequestException;

public class DatabaseCountryRepository implements CountryRepository {

	private CountryMapper mapper;
	private Database database;

	@Autowired
	public DatabaseCountryRepository(CountryMapper mapper, Database database) {
		this.mapper = mapper;
		this.database = database;
	}

	@Override
	public Country get(CountryName countryName) {
		UnitOfWork<CountryEntity> unitOfWork = (Session session) -> {

			return read(countryName, session).orElseThrow(
					() -> new NotFoundException("Cannot find data for country name: " + countryName + " in database"));
		};

		CountryEntity entity = database.execute(unitOfWork);
		return mapper.map(entity);
	}

	@Override
	public void save(Country country) {
		UnitOfWork<Void> unitOfWork = (Session session) -> {

			read(country.getName(), session).ifPresent(c -> {
				throw new BadRequestException(
						"Cannot save into database, found data for that country name. If you want update use update() method.");
			});

			session.save(mapper.map(country));
			return null;
		};

		database.execute(unitOfWork);
	}

	@Override
	public void addCurrency(CountryName countryName, Currency currency) {
		UnitOfWork<Void> unitOfWork = (Session session) -> {

			CountryEntity entity = read(countryName, session)
					.orElseThrow(() -> new BadRequestException("Cannot add currency: " + currency
							+ "to country with name: " + countryName + " cannot find country."));

			if (entity.getCurrencies().contains(currency)) {
				throw new BadRequestException("Cannot add currency: " + currency + "to country with name: "
						+ countryName + " country already have this currency.");
			}

			entity.addCurrency(currency);
			session.update(entity);
			return null;
		};

		database.execute(unitOfWork);
	}

	@Override
	public void removeCurrency(CountryName countryName, Currency currency) {
		UnitOfWork<Void> unitOfWork = (Session session) -> {
			CountryEntity entity = read(countryName, session)
					.orElseThrow(() -> new BadRequestException("Cannot remove currency: " + currency
							+ "from country with name: " + countryName + " cannot find country."));

			if (!entity.getCurrencies().contains(currency)) {
				throw new BadRequestException("Cannot remove currency: " + currency + "from country with name: "
						+ countryName + " country do not have this currency.");
			}

			entity.removeCurrency(currency);
			session.update(entity);
			return null;
		};

		database.execute(unitOfWork);
	}

	@Override
	public void delete(CountryName countryName) {

		UnitOfWork<Void> unitOfWork = (Session session) -> {

			CountryEntity entity = read(countryName, session).orElseThrow(() -> new BadRequestException(
					"Cannot remove country with name: " + countryName + " cannot find country."));

			session.remove(entity);
			return null;
		};

		database.execute(unitOfWork);
	}

	@Override
	public List<Country> getCountriesHasMoreThanOneCurrency() {
		UnitOfWork<List<Country>> unitOfWork = (Session session) -> {
			Query<CountryEntity> query = session.createNamedQuery(CountryEntity.QUERY_WHERE_HAS_MORE_THAN_TWO_CURRENCY,
					CountryEntity.class);

			return query.getResultList().stream().map(mapper::map).collect(Collectors.toList());
		};

		return database.execute(unitOfWork);
	}

	private Optional<CountryEntity> read(CountryName countryName, Session session) {
		Query<CountryEntity> query = session.createNamedQuery(CountryEntity.QUERY_BY_COUNTRY_NAME, CountryEntity.class);
		query.setParameter(CountryEntity.PARAMETER_NAME, countryName);
		query.setMaxResults(1);

		return query.uniqueResultOptional();
	}
}
