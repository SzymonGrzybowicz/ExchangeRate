package exchange_rate.downloader.nbp.repository;

import java.time.LocalDate;

import javax.persistence.NoResultException;

import org.hibernate.Session;
import org.hibernate.query.Query;

import exchange_rate.database.Database;
import exchange_rate.database.UnitOfWork;
import exchange_rate.downloader.nbp.database.entity.ExchangeRateEntity;
import exchange_rate.downloader.nbp.database.mapper.ExchangeRateEntityMapper;
import exchange_rate.downloader.nbp.exception.checked.NotFoundException;
import exchange_rate.downloader.nbp.exception.unchecked.BadRequestException;
import exchange_rate.dto.ExchangeRate;
import exchange_rate.enums.Currency;

public class ExchangeRateRepository {

	public ExchangeRateRepository(Database database) {
		this.database = database;
	}

	private final Database database;
	private final ExchangeRateEntityMapper mapper = new ExchangeRateEntityMapper();

	public void save(ExchangeRate exchangeRate) {
		UnitOfWork<Void> unitOfWork = (Session session) -> {

			try {
				read(exchangeRate.getCurrency(), exchangeRate.getDate(), session);
				throw new BadRequestException(
						"Cannot save into database, found data for that currency and date. If you want update use update() method.");
			} catch (NoResultException e) {
				session.save(mapper.map(exchangeRate));
				return null;
			}
		};

		database.execute(unitOfWork);
	}

	public ExchangeRate get(Currency currency, LocalDate date) {
		UnitOfWork<ExchangeRateEntity> unitOfWork = (Session session) -> {

			try {
				return read(currency, date, session);
			} catch (NoResultException e) {
				throw new NotFoundException(
						"Cannot find data for currency: " + currency + " and date: " + date + " in database");
			}
		};

		ExchangeRateEntity result = database.execute(unitOfWork);
		return mapper.map(result);
	}

	public void delete(ExchangeRate exchangeRate) {
		UnitOfWork<Void> unitOfWork = (Session session) -> {

			try {
				ExchangeRateEntity entity = read(exchangeRate.getCurrency(), exchangeRate.getDate(), session);
				session.delete(entity);
				return null;
			} catch (NoResultException e) {
				throw new NotFoundException("Cannot delete for currency: " + exchangeRate.getCurrency() + " and date: "
						+ exchangeRate.getDate() + " object not exist in database");
			}
		};

		database.execute(unitOfWork);
	}

	public void update(ExchangeRate exchangeRate) {
		UnitOfWork<Void> unitOfWork = (Session session) -> {
			ExchangeRateEntity entity = read(exchangeRate.getCurrency(), exchangeRate.getDate(), session);
			entity.setRate(exchangeRate.getRate());
			session.update(entity);
			return null;
		};

		database.execute(unitOfWork);
	}

	private ExchangeRateEntity read(Currency currency, LocalDate date, Session session) {
		Query<ExchangeRateEntity> query = session
				.createNamedQuery(ExchangeRateEntity.QUERY_GET_BY_CURRENCY_AND_DATE, ExchangeRateEntity.class)
				.setParameter(ExchangeRateEntity.PARAMETER_CURRENCY, currency)
				.setParameter(ExchangeRateEntity.PARAMETER_DATE, date).setMaxResults(1);

		return query.getSingleResult();
	}
}
