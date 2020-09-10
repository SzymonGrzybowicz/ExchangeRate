package exchange_rate.nbp_api_client.repository;

import java.time.LocalDate;

import javax.persistence.NoResultException;

import org.hibernate.Session;
import org.hibernate.query.Query;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.database.Database;
import exchange_rate.nbp_api_client.database.UnitOfWork;
import exchange_rate.nbp_api_client.database.entity.ExchangeRateEntity;
import exchange_rate.nbp_api_client.database.mapper.ExchangeRateEntityMapper;
import exchange_rate.nbp_api_client.dto.ExchangeRate;
import exchange_rate.nbp_api_client.exception.checked.NotFoundException;
import exchange_rate.nbp_api_client.exception.unchecked.BadRequestException;

public class ExchangeRateRepository {

	private ExchangeRateEntityMapper mapper = new ExchangeRateEntityMapper();

	private Database database = new Database();

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
		try {
			UnitOfWork<ExchangeRateEntity> unitOfWork = (Session session) -> {

				Query<ExchangeRateEntity> query = session
						.createNamedQuery(ExchangeRateEntity.QUERY_GET_BY_CURRENCY_AND_DATE, ExchangeRateEntity.class)
						.setParameter(ExchangeRateEntity.PARAMETER_CURRENCY, currency)
						.setParameter(ExchangeRateEntity.PARAMETER_DATE, date);

				return query.getSingleResult();
			};

			ExchangeRateEntity result = database.execute(unitOfWork);
			return mapper.map(result);
		} catch (NoResultException e) {
			throw new NotFoundException(
					"Cannot find data for currency: " + currency + " and date: " + date + " in database");
		}
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
