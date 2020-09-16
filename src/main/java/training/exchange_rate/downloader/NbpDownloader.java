package training.exchange_rate.downloader;

import java.time.LocalDate;

import training.enums.Currency;
import training.exchange_rate.dto.ExchangeRate;

public interface NbpDownloader {
	ExchangeRate get(Currency currency);

	ExchangeRate get(Currency currency, LocalDate date);
}
