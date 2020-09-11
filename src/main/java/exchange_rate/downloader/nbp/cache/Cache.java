package exchange_rate.downloader.nbp.cache;

import java.time.LocalDate;

import exchange_rate.dto.ExchangeRate;
import exchange_rate.enums.Currency;

public interface Cache {

	ExchangeRate get(Currency currency, LocalDate date);

	void saveOrUpdateIfExists(ExchangeRate exchangeRate);
}
