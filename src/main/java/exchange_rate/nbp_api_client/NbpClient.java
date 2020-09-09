package exchange_rate.nbp_api_client;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import exchange_rate.nbp_api_client.cache.Cache;
import exchange_rate.nbp_api_client.downloader.Downloader;
import exchange_rate.nbp_api_client.dto.ExchangeRate;
import exchange_rate.nbp_api_client.exception.checked.NotFoundException;

public class NbpClient {

	private final Downloader downloader;
	private Cache cache = new Cache() {

		@Override
		public void saveOrUpdateIfExists(ExchangeRate exchangeRate) {
			// do nothing
		}

		@Override
		public ExchangeRate getOrNull(Currency currency, Date date) {
			return null;
		}
	};

	public NbpClient(Downloader downloader) {
		this.downloader = downloader;
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public ExchangeRate requestActualExchangeRate(Currency currency) {
		ExchangeRate result;
		result = cache.getOrNull(currency, new Date());
		if (result != null) {
			return result;
		}

		result = downloader.get(currency);
		cache.saveOrUpdateIfExists(result);
		return result;
	}

	public ExchangeRate requestExchangeRate(Currency currency, Date date) {

		ExchangeRate result;
		result = cache.getOrNull(currency, date);
		if (result != null) {
			return result;
		}

		LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

		int counter = 0;
		while (counter < 5) {
			try {
				Date requestDate = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
				result = downloader.get(currency, requestDate);
				cache.saveOrUpdateIfExists(result);
				return result;
			} catch (NotFoundException e) {
				counter++;
				dateTime = dateTime.minusDays(1);
			}
		}

		throw new NotFoundException("Cannot find exchange rate for that date! Make sure that data is correct.");
	}

	public List<ExchangeRate> requestActualExchangeRate(List<Currency> currencies) {

		List<ExchangeRate> result = new ArrayList<>();
		currencies.forEach(c -> result.add(requestActualExchangeRate(c)));
		return result;
	}

	public List<ExchangeRate> requestExchangeRate(List<Currency> currencies, Date date) {
		List<ExchangeRate> result = new ArrayList<>();
		currencies.forEach(c -> result.add(requestExchangeRate(c, date)));
		return result;
	}
}
