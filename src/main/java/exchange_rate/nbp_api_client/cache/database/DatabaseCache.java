package exchange_rate.nbp_api_client.cache.database;

import java.util.Date;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.cache.Cache;
import exchange_rate.nbp_api_client.database.exception.DatabaseException;
import exchange_rate.nbp_api_client.database.exchange_rate.ExchangeRateRepository;
import exchange_rate.nbp_api_client.dto.ExchangeRate;

public class DatabaseCache implements Cache {

	private ExchangeRateRepository repository = new ExchangeRateRepository();

	@Override
	public ExchangeRate getOrNull(Currency currency, Date date) {
		return repository.read(currency, date);
	}

	@Override
	public void saveOrUpdateIfExists(ExchangeRate exchangeRate) {
		try {
			repository.save(exchangeRate);
		} catch (DatabaseException e) {
			repository.update(exchangeRate);
		}
	}
}
