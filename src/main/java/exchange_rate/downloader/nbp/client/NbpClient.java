package exchange_rate.downloader.nbp.client;

import java.time.LocalDate;
import java.util.List;

import exchange_rate.dto.ExchangeRate;
import exchange_rate.enums.Currency;

public interface NbpClient {
	ExchangeRate get(Currency currency);

	ExchangeRate get(Currency currency, LocalDate date);

	List<ExchangeRate> getForPeroid(Currency currency, LocalDate startDate, LocalDate endDate);
}
