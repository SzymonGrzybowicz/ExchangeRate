package training.exchange_rate.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import training.database.Database;
import training.enums.Currency;
import training.exchange_rate.database.entity.ExchangeRateEntity;
import training.exchange_rate.database.mapper.ExchangeRateEntityMapper;
import training.exchange_rate.dto.ExchangeRate;
import training.exchange_rate.exception.checked.NotFoundException;
import training.exchange_rate.exception.unchecked.BadRequestException;

@Repository
public class ExchangeRateRepository {

	private Database database;
	private ExchangeRateEntityMapper mapper;

	public ExchangeRateRepository(Database database, ExchangeRateEntityMapper mapper) {
		this.database = database;
		this.mapper = mapper;
	}

	public void save(ExchangeRate exchangeRate) {
		database.execute((session) -> {

			read(exchangeRate.getCurrency(), exchangeRate.getDate(), session).ifPresent(e -> {
				throw new BadRequestException(
						"Cannot save into database, found data for that currency and date. If you want update use update() method.");
			});

			session.save(mapper.map(exchangeRate));
			return null;
		});
	}

	public ExchangeRate get(Currency currency, LocalDate date) {
		ExchangeRateEntity result = database.execute((session) -> {

			return read(currency, date, session).orElseThrow(() -> new NotFoundException(
					"Cannot find data for currency: " + currency + " and date: " + date + " in database"));
		});
		return mapper.map(result);
	}

	public void delete(ExchangeRate exchangeRate) {
		database.execute((session) -> {

			ExchangeRateEntity entity = read(exchangeRate.getCurrency(), exchangeRate.getDate(), session)
					.orElseThrow(() -> new NotFoundException("Cannot delete for currency: " + exchangeRate.getCurrency()
							+ " and date: " + exchangeRate.getDate() + " object not exist in database"));

			session.delete(entity);
			return null;
		});
	}

	public void update(ExchangeRate exchangeRate) {
		database.execute((session) -> {
			ExchangeRateEntity entity = read(exchangeRate.getCurrency(), exchangeRate.getDate(), session)
					.orElseThrow(() -> new BadRequestException(
							"Cannot update in database, not found data for that currency and date. If you want save use save() method."));

			entity.setRate(exchangeRate.getRate());
			session.update(entity);
			return null;
		});
	}

	public ExchangeRate getMaximumRateInPeriod(Currency currency, LocalDate startDate, LocalDate endDate) {
		ExchangeRateEntity entity = database.execute((session) -> {
			Query<ExchangeRateEntity> query = session.createNamedQuery(
					ExchangeRateEntity.QUERY_BY_CURRENCY_AND_DATE_SORTED_BY_RATE_DESC, ExchangeRateEntity.class);
			query.setParameter(ExchangeRateEntity.PARAMETER_CURRENCY, currency);
			query.setParameter(ExchangeRateEntity.PARAMETER_START_DATE, startDate);
			query.setParameter(ExchangeRateEntity.PARAMETER_END_DATE, endDate);
			query.setMaxResults(1);

			return query.uniqueResultOptional().orElseThrow(
					() -> new NotFoundException("Cannot find data for currency: " + currency + " in period from: "
							+ startDate + " to: " + endDate + " in database. Make sure peroid is correct."));
		});
		return mapper.map(entity);
	}

	public ExchangeRate getMinimumRateInPeriod(Currency currency, LocalDate startDate, LocalDate endDate) {
		ExchangeRateEntity entity = database.execute((session) -> {
			Query<ExchangeRateEntity> query = session.createNamedQuery(
					ExchangeRateEntity.QUERY_BY_CURRENCY_AND_DATE_SORTED_BY_RATE, ExchangeRateEntity.class);
			query.setParameter(ExchangeRateEntity.PARAMETER_CURRENCY, currency);
			query.setParameter(ExchangeRateEntity.PARAMETER_START_DATE, startDate);
			query.setParameter(ExchangeRateEntity.PARAMETER_END_DATE, endDate);
			query.setMaxResults(1);

			return query.uniqueResultOptional().orElseThrow(
					() -> new NotFoundException("Cannot find data for currency: " + currency + " in period from: "
							+ startDate + " to: " + endDate + " in database. Make sure peroid is correct."));
		});
		return mapper.map(entity);
	}

	public Currency getCurrencyWithHighestExchangeRateDifferenceInPeriod(LocalDate startDate, LocalDate endDate) {
		return database.execute((session) -> {
			Query<Currency> query = session.createNamedQuery(
					ExchangeRateEntity.QUERY_CURRENCIES_BY_PERIOD_SORTED_BY_HIGHEST_RATE_DIFFERENCE_DESC,
					Currency.class);
			query.setParameter(ExchangeRateEntity.PARAMETER_START_DATE, startDate);
			query.setParameter(ExchangeRateEntity.PARAMETER_END_DATE, endDate);
			query.setMaxResults(1);

			return query.uniqueResultOptional().orElseThrow(
					() -> new NotFoundException("Cannot find data for period from: " + startDate + " to: " + endDate));
		});
	}

	public List<ExchangeRate> getFiveMaximumExchangeRate(Currency currency) {
		List<ExchangeRateEntity> result = database.execute((session) -> {
			Query<ExchangeRateEntity> query = session.createNamedQuery(
					ExchangeRateEntity.QUERY_BY_CURRENCY_SORTED_BY_RATE_DESC, ExchangeRateEntity.class);
			query.setParameter(ExchangeRateEntity.PARAMETER_CURRENCY, currency);
			query.setMaxResults(5);

			return query.getResultList();
		});
		return result.stream().map(mapper::map).collect(Collectors.toList());
	}

	public List<ExchangeRate> getFiveMinimumExchangeRate(Currency currency) {
		List<ExchangeRateEntity> result = database.execute((session) -> {
			Query<ExchangeRateEntity> query = session
					.createNamedQuery(ExchangeRateEntity.QUERY_BY_CURRENCY_SORTED_BY_RATE, ExchangeRateEntity.class);
			query.setParameter(ExchangeRateEntity.PARAMETER_CURRENCY, currency);
			query.setMaxResults(5);

			return query.getResultList();
		});
		return result.stream().map(mapper::map).collect(Collectors.toList());
	}

	private Optional<ExchangeRateEntity> read(Currency currency, LocalDate date, Session session) {
		Query<ExchangeRateEntity> query = session.createNamedQuery(ExchangeRateEntity.QUERY_BY_CURRENCY_AND_DATE,
				ExchangeRateEntity.class);
		query.setParameter(ExchangeRateEntity.PARAMETER_CURRENCY, currency);
		query.setParameter(ExchangeRateEntity.PARAMETER_DATE, date);
		query.setMaxResults(1);

		return query.uniqueResultOptional();
	}
}
