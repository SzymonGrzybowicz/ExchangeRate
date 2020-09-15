package exchange_rate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import enums.Currency;
import exchange_rate.cache.Cache;
import exchange_rate.downloader.NbpDownloader;
import exchange_rate.downloader.http.HttpNbpDownloader;
import exchange_rate.dto.ExchangeRate;
import exchange_rate.exception.checked.NotFoundException;
import exchange_rate.exception.unchecked.BadRequestException;

public class NbpExchangeRateClient {

	private final NbpDownloader downloader;
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

	public NbpExchangeRateClient(NbpDownloader downloader) {
		this.downloader = downloader;
	}

	public NbpExchangeRateClient() {
		this.downloader = new HttpNbpDownloader();
	}

	public void setCache(Cache cache) {
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

	public List<ExchangeRate> getExchangeRatesForPeroid(Currency currency, LocalDate startDate, LocalDate endDate) {
		if (ChronoUnit.DAYS.between(startDate, endDate) > 93) {
			throw new BadRequestException("Perriod cannot be longer than 93 days");
		}
		List<ExchangeRate> rates = downloader.getForPeriod(currency, startDate, endDate);
		rates.forEach(r -> cache.saveOrUpdateIfExists(r));

		return rates;

	}

	public List<ExchangeRate> getAllExchangeRate(Currency currency) {
		LocalDate firstPublicationDate = LocalDate.of(2002, 1, 2);
		LocalDate startFetchDate = firstPublicationDate;
		LocalDate endFetchDate = LocalDate.now();
		List<ExchangeRate> result = new ArrayList<ExchangeRate>();
		while (!startFetchDate.equals(LocalDate.now()) && !startFetchDate.isAfter(LocalDate.now())) {

			if (startFetchDate.plusDays(93).isAfter(LocalDate.now())) {
				endFetchDate = LocalDate.now();
			} else {
				endFetchDate = startFetchDate.plusDays(93);
			}
			result.addAll(getExchangeRatesForPeroid(currency, startFetchDate, endFetchDate));
			startFetchDate = endFetchDate.plusDays(1);

		}
		return result;
	}

	public List<ExchangeRate> getNumberOfExchangeRatesSinceFirstPublication(int numberOfRates, Currency currency) {
		LocalDate firstPublicationDate = LocalDate.of(2002, 1, 2);
		LocalDate startFetchDate = firstPublicationDate;
		LocalDate endFetchDate = LocalDate.now();
		List<ExchangeRate> result = new ArrayList<ExchangeRate>();
		while (!startFetchDate.equals(LocalDate.now()) && !startFetchDate.isAfter(LocalDate.now())) {

			if (startFetchDate.plusDays(93).isAfter(LocalDate.now())) {
				endFetchDate = LocalDate.now();
			} else {
				endFetchDate = startFetchDate.plusDays(93);
			}
			result.addAll(getExchangeRatesForPeroid(currency, startFetchDate, endFetchDate));
			startFetchDate = endFetchDate.plusDays(1);

		}
		return result;
	}
}
