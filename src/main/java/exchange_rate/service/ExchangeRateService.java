package exchange_rate.service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

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

	public List<ExchangeRate> getExchangeRatesForPeroid(Currency currency, LocalDate startDate, LocalDate endDate) {
		return downloader.getExchangeRatesForPeroid(currency, startDate, endDate);
	}

	public ExchangeRate getMaximumRateForPeroid(Currency currency, LocalDate startDate, LocalDate endDate) {
		return getMax(downloader.getExchangeRatesForPeroid(currency, startDate, endDate));
	}

	public ExchangeRate getMinimumRateForPeroid(Currency currency, LocalDate startDate, LocalDate endDate) {
		return getMin(downloader.getExchangeRatesForPeroid(currency, startDate, endDate));
	}

	public Pair<ExchangeRate, ExchangeRate> getBiggestRateDifferenceInPeroid(Currency currency, LocalDate startDate,
			LocalDate endDate) {
		return getBiggestRateDifference(downloader.getExchangeRatesForPeroid(currency, startDate, endDate));
	}

	public Currency getCurrencyWithBiggestRateDifferenceInPeroid(List<Currency> rates, LocalDate startDate,
			LocalDate endDate) {
		List<Pair<ExchangeRate, ExchangeRate>> pairList = rates.stream()
				.map(currency -> downloader.getExchangeRatesForPeroid(currency, startDate, endDate))
				.map(exchangeRateList -> getBiggestRateDifference(exchangeRateList)).collect(Collectors.toList());

		return Collections
				.max(pairList,
						Comparator.comparing(pair -> pair.getRight().getRate().subtract(pair.getLeft().getRate())))
				.getLeft().getCurrency();
	}

	private ExchangeRate getMax(List<ExchangeRate> rates) {
		return Collections.max(rates, Comparator.comparing(r -> r.getRate()));
	}

	private ExchangeRate getMin(List<ExchangeRate> rates) {
		return Collections.min(rates, Comparator.comparing(r -> r.getRate()));
	}

	private Pair<ExchangeRate, ExchangeRate> getBiggestRateDifference(List<ExchangeRate> rates) {
		ExchangeRate minimum = getMin(rates);
		ExchangeRate maximum = getMax(rates);
		return new ImmutablePair<ExchangeRate, ExchangeRate>(minimum, maximum);
	}
}
