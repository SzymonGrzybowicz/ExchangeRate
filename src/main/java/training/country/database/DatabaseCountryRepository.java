package training.country.database;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import training.country.CountryRepository;
import training.country.database.entity.CountryEntity;
import training.country.database.mapper.CountryMapper;
import training.country.dto.Country;
import training.database.Database;
import training.enums.CountryName;
import training.enums.Currency;
import training.exchange_rate.exception.checked.NotFoundException;
import training.exchange_rate.exception.unchecked.BadRequestException;

@Repository
public class DatabaseCountryRepository implements CountryRepository {

	private CountryMapper mapper;
	private Database database;

	public DatabaseCountryRepository(CountryMapper mapper, Database database) {
		this.mapper = mapper;
		this.database = database;
	}

	@Override
	public Country get(CountryName countryName) {
		CountryEntity entity = database.execute((session) -> {
			return read(countryName, session).orElseThrow(
					() -> new NotFoundException("Cannot find data for country name: " + countryName + " in database"));
		});
		return mapper.map(entity);
	}

	@Override
	public void save(Country country) {
		database.execute((session) -> {

			read(country.getName(), session).ifPresent(c -> {
				throw new BadRequestException(
						"Cannot save into database, found data for that country name. If you want update use update() method.");
			});

			session.save(mapper.map(country));
			return null;
		});
	}

	@Override
	public void addCurrency(CountryName countryName, Currency currency) {
		database.execute((session) -> {

			CountryEntity entity = read(countryName, session)
					.orElseThrow(() -> new NotFoundException("Cannot add currency: " + currency
							+ "to country with name: " + countryName + " cannot find country."));

			if (entity.getCurrencies().contains(currency)) {
				throw new BadRequestException("Cannot add currency: " + currency + "to country with name: "
						+ countryName + " country already have this currency.");
			}

			entity.addCurrency(currency);
			session.update(entity);
			return null;
		});
	}

	@Override
	public void removeCurrency(CountryName countryName, Currency currency) {
		database.execute((session) -> {
			CountryEntity entity = read(countryName, session)
					.orElseThrow(() -> new NotFoundException("Cannot remove currency: " + currency
							+ "from country with name: " + countryName + " cannot find country."));

			if (!entity.getCurrencies().contains(currency)) {
				throw new BadRequestException("Cannot remove currency: " + currency + "from country with name: "
						+ countryName + " country do not have this currency.");
			}

			entity.removeCurrency(currency);
			session.update(entity);
			return null;
		});
	}

	@Override
	public void delete(CountryName countryName) {
		database.execute((session) -> {
			CountryEntity entity = read(countryName, session).orElseThrow(() -> new NotFoundException(
					"Cannot remove country with name: " + countryName + " cannot find country."));

			session.remove(entity);
			return null;
		});
	}

	@Override
	public List<Country> getCountriesHasMoreThanOneCurrency() {
		List<CountryEntity> result = database.execute((session) -> {
			Query<CountryEntity> query = session.createNamedQuery(CountryEntity.QUERY_WHERE_HAS_MORE_THAN_TWO_CURRENCY,
					CountryEntity.class);

			return query.getResultList();
		});
		return result.stream().map(mapper::map).collect(Collectors.toList());
	}

	private Optional<CountryEntity> read(CountryName countryName, Session session) {
		Query<CountryEntity> query = session.createNamedQuery(CountryEntity.QUERY_BY_COUNTRY_NAME, CountryEntity.class);
		query.setParameter(CountryEntity.PARAMETER_NAME, countryName);
		query.setMaxResults(1);

		return query.uniqueResultOptional();
	}
}
