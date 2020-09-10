package exchange_rate.nbp_api_client.cache;

import java.time.LocalDate;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.dto.ExchangeRate;

public interface Cache {

	ExchangeRate get(Currency currency, LocalDate date);

	void saveOrUpdateIfExists(ExchangeRate exchangeRate);
}
