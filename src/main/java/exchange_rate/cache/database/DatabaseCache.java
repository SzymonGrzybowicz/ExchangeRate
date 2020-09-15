package exchange_rate.cache.database;

import java.time.LocalDate;

import database.Database;
import enums.Currency;
import exchange_rate.cache.Cache;
import exchange_rate.dto.ExchangeRate;
import exchange_rate.exception.unchecked.BadRequestException;
import exchange_rate.repository.ExchangeRateRepository;

public class DatabaseCache implements Cache {

	public DatabaseCache(Database database) {
		this.repository = new ExchangeRateRepository(database);
	}

	private final ExchangeRateRepository repository;

	@Override
	public ExchangeRate get(Currency currency, LocalDate date) {
		return repository.get(currency, date);
	}

	@Override
	public void saveOrUpdateIfExists(ExchangeRate exchangeRate) {
		try {
			repository.save(exchangeRate);
		} catch (BadRequestException e) {
			repository.update(exchangeRate);
		}
	}
}
