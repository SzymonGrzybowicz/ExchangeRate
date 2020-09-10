package exchange_rate.nbp_api_client.downloader;

import java.time.LocalDate;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.dto.ExchangeRate;

public interface Downloader {
	ExchangeRate get(Currency currency);

	ExchangeRate get(Currency currency, LocalDate date);
}
