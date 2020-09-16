package training.exchange_rate.cache;

import java.time.LocalDate;

import training.enums.Currency;
import training.exchange_rate.dto.ExchangeRate;

public interface Cache {

	ExchangeRate get(Currency currency, LocalDate date);

	void saveOrUpdateIfExists(ExchangeRate exchangeRate);
}
