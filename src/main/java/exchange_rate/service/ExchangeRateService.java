package exchange_rate.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import exchange_rate.country.CountryRepository;
import exchange_rate.country.database.DatabaseCountryRepository;
import exchange_rate.database.Database;
import exchange_rate.downloader.ExchangeRateDownloader;
import exchange_rate.downloader.nbp.NbpExchangeRateDownloader;
import exchange_rate.downloader.nbp.cache.Cache;
import exchange_rate.downloader.nbp.cache.database.DatabaseCache;
import exchange_rate.dto.Country;
import exchange_rate.dto.ExchangeRate;
import exchange_rate.enums.CountryName;
import exchange_rate.enums.Currency;

public class ExchangeRateService {

	public ExchangeRateService(Database database) {
		NbpExchangeRateDownloader nbpDownloader = new NbpExchangeRateDownloader();
		Cache cache = new DatabaseCache(database);
		nbpDownloader.setCache(cache);
		this.downloader = nbpDownloader;
		this.countryRepository = new DatabaseCountryRepository(database);
	}

	private final ExchangeRateDownloader downloader;
	private final CountryRepository countryRepository;

	public ExchangeRate getActualExchangeRate(Currency currency) {
		return downloader.getActualExchangeRate(currency);
	}

	public ExchangeRate getExchangeRate(Currency currency, LocalDate date) {
		return downloader.getExchangeRate(currency, date);
	}

	public List<ExchangeRate> getActualExchangeRates(CountryName countryName) {
		Country country = countryRepository.get(countryName);
		return country.getCurrencies().stream().map(c -> downloader.getActualExchangeRate(c))
				.collect(Collectors.toList());
	}

	public List<ExchangeRate> getExchangeRates(CountryName countryName, LocalDate date) {
		Country country = countryRepository.get(countryName);
		return country.getCurrencies().stream().map(c -> downloader.getExchangeRate(c, date))
				.collect(Collectors.toList());
	}

}
