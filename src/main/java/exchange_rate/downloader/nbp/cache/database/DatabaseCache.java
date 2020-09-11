package exchange_rate.downloader.nbp.cache.database;

import java.time.LocalDate;

import exchange_rate.database.Database;
import exchange_rate.downloader.nbp.cache.Cache;
import exchange_rate.downloader.nbp.exception.unchecked.BadRequestException;
import exchange_rate.downloader.nbp.repository.ExchangeRateRepository;
import exchange_rate.dto.ExchangeRate;
import exchange_rate.enums.Currency;

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
