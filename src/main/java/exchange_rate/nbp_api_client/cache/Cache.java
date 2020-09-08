package exchange_rate.nbp_api_client.cache;

import java.util.Date;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.dto.ExchangeRate;

public interface Cache {

	ExchangeRate getOrNull(Currency currency, Date date);

	void saveOrUpdateIfExists(ExchangeRate exchangeRate);
}
