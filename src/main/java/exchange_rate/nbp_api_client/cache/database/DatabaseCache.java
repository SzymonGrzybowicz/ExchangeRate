package exchange_rate.nbp_api_client.cache.database;

import java.util.Date;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.cache.Cache;
import exchange_rate.nbp_api_client.database.ExchangeRateRepository;
import exchange_rate.nbp_api_client.dto.ExchangeRate;

public class DatabaseCache implements Cache {

	private ExchangeRateRepository repository = new ExchangeRateRepository();

	@Override
	public ExchangeRate getOrNull(Currency currency, Date date) {
		return repository.read(currency, date);
	}

	@Override
	public void save(ExchangeRate exchangeRate) {
		repository.save(exchangeRate);
	}

}
