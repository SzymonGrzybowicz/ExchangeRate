package country.database;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.query.Query;

import country.CountryRepository;
import country.database.entity.CountryEntity;
import country.database.mapper.CountryMapper;
import country.dto.Country;
import database.Database;
import database.UnitOfWork;
import enums.CountryName;
import enums.Currency;
import exchange_rate.exception.checked.NotFoundException;
import exchange_rate.exception.unchecked.BadRequestException;

public class DatabaseCountryRepository implements CountryRepository {

	public DatabaseCountryRepository(Database database) {
		this.database = database;
	}

	private CountryMapper mapper = new CountryMapper();

	private final Database database;

	public void setMapper(CountryMapper countryMapper) {
		mapper = countryMapper;
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
