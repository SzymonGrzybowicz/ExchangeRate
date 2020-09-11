package exchange_rate.downloader;

import java.time.LocalDate;
import java.util.List;

import exchange_rate.dto.ExchangeRate;
import exchange_rate.enums.Currency;

public interface ExchangeRateDownloader {
	ExchangeRate getActualExchangeRate(Currency currency);

	ExchangeRate getExchangeRate(Currency currency, LocalDate date);

	List<ExchangeRate> getExchangeRatesForPeroid(Currency currency, LocalDate startDate, LocalDate endDate);
}
