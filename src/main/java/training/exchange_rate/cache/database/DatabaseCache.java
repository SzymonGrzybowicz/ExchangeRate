package training.exchange_rate.cache.database;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import training.enums.Currency;
import training.exchange_rate.cache.Cache;
import training.exchange_rate.dto.ExchangeRate;
import training.exchange_rate.exception.unchecked.BadRequestException;
import training.exchange_rate.repository.ExchangeRateRepository;

@Component
public class DatabaseCache implements Cache {

	public DatabaseCache(ExchangeRateRepository repository) {
		this.repository = repository;
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
