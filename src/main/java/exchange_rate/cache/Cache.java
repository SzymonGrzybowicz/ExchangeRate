package exchange_rate.cache;

import java.time.LocalDate;

import enums.Currency;
import exchange_rate.dto.ExchangeRate;

public interface Cache {

	ExchangeRate get(Currency currency, LocalDate date);

	void saveOrUpdateIfExists(ExchangeRate exchangeRate);
}
