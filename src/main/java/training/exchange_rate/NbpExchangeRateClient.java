package training.exchange_rate;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import training.enums.Currency;
import training.exchange_rate.cache.Cache;
import training.exchange_rate.downloader.NbpDownloader;
import training.exchange_rate.dto.ExchangeRate;
import training.exchange_rate.exception.checked.NotFoundException;

@Service
public class NbpExchangeRateClient {

	private NbpDownloader downloader;
	private Cache cache;

	@Autowired
	public NbpExchangeRateClient(NbpDownloader downloader, Cache cache) {
		this.downloader = downloader;
		this.cache = cache;
	}

	public ExchangeRate getActualExchangeRate(Currency currency) {
		try {
			return cache.get(currency, LocalDate.now());
		} catch (NotFoundException e) {
			ExchangeRate exchangeRate = downloader.get(currency);
			cache.saveOrUpdateIfExists(exchangeRate);
			return exchangeRate;
		}
	}

	public ExchangeRate getExchangeRate(Currency currency, LocalDate date) {

		LocalDate requestDate = date;
		int counter = 0;
		while (counter < 5) {
			try {
				return cache.get(currency, date);
			} catch (NotFoundException ex) {
				try {
					ExchangeRate result;
					result = downloader.get(currency, requestDate);
					cache.saveOrUpdateIfExists(result);
					return result;
				} catch (NotFoundException e) {
					counter++;
					requestDate = requestDate.minusDays(1);
				}
			}
		}

		throw new NotFoundException("Cannot find exchange rate for that date! Make sure that data is correct.");
	}
}
