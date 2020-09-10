package exchange_rate.nbp_api_client.cache.database;

import java.util.Date;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.cache.Cache;
import exchange_rate.nbp_api_client.dto.ExchangeRate;
import exchange_rate.nbp_api_client.exception.unchecked.BadRequestException;
import exchange_rate.nbp_api_client.repository.ExchangeRateRepository;

public class DatabaseCache implements Cache {

	private ExchangeRateRepository repository = new ExchangeRateRepository();

	@Override
	public ExchangeRate get(Currency currency, Date date) {
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
