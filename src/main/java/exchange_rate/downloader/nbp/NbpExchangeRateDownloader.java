package exchange_rate.downloader.nbp;

import java.time.LocalDate;

import exchange_rate.downloader.ExchangeRateDownloader;
import exchange_rate.downloader.nbp.cache.Cache;
import exchange_rate.downloader.nbp.client.NbpClient;
import exchange_rate.downloader.nbp.client.http.HttpNbpClient;
import exchange_rate.downloader.nbp.exception.checked.NotFoundException;
import exchange_rate.dto.ExchangeRate;
import exchange_rate.enums.Currency;

public class NbpExchangeRateDownloader implements ExchangeRateDownloader {

	private final NbpClient client;
	private Cache cache = new Cache() {

		@Override
		public void saveOrUpdateIfExists(ExchangeRate exchangeRate) {
			// do nothing
		}

		@Override
		public ExchangeRate get(Currency currency, LocalDate date) {
			throw new NotFoundException();
		}
	};

	public NbpExchangeRateDownloader(NbpClient downloader) {
		this.client = downloader;
	}

	public NbpExchangeRateDownloader() {
		this.client = new HttpNbpClient();
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	@Override
	public ExchangeRate getActualExchangeRate(Currency currency) {
		try {
			return cache.get(currency, LocalDate.now());
		} catch (NotFoundException e) {
			ExchangeRate exchangeRate = client.get(currency);
			cache.saveOrUpdateIfExists(exchangeRate);
			return exchangeRate;
		}
	}

	@Override
	public ExchangeRate getExchangeRate(Currency currency, LocalDate date) {

		LocalDate requestDate = date;
		int counter = 0;
		while (counter < 5) {
			try {
				return cache.get(currency, date);
			} catch (NotFoundException ex) {
				try {
					ExchangeRate result;
					result = client.get(currency, requestDate);
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
