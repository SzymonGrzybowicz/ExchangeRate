package exchange_rate.downloader;

import java.time.LocalDate;

import exchange_rate.dto.ExchangeRate;
import exchange_rate.enums.Currency;

public interface ExchangeRateDownloader {
	ExchangeRate getActualExchangeRate(Currency currency);

	ExchangeRate getExchangeRate(Currency currency, LocalDate date);
}
