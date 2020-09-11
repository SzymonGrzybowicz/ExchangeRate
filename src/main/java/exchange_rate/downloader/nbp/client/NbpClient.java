package exchange_rate.downloader.nbp.client;

import java.time.LocalDate;

import exchange_rate.dto.ExchangeRate;
import exchange_rate.enums.Currency;

public interface NbpClient {
	ExchangeRate get(Currency currency);

	ExchangeRate get(Currency currency, LocalDate date);
}
