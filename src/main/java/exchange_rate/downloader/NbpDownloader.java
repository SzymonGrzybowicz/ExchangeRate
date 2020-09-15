package exchange_rate.downloader;

import java.time.LocalDate;
import java.util.List;

import enums.Currency;
import exchange_rate.dto.ExchangeRate;

public interface NbpDownloader {
	ExchangeRate get(Currency currency);

	ExchangeRate get(Currency currency, LocalDate date);

	List<ExchangeRate> getForPeriod(Currency currency, LocalDate startDate, LocalDate endDate);
}
