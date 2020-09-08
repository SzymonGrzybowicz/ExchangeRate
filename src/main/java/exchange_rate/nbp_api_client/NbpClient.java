package exchange_rate.nbp_api_client;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import exchange_rate.nbp_api_client.cache.Cache;
import exchange_rate.nbp_api_client.converter.Converter;
import exchange_rate.nbp_api_client.country.CountryRepository;
import exchange_rate.nbp_api_client.downloader.Downloader;
import exchange_rate.nbp_api_client.downloader.DownloaderResponse;
import exchange_rate.nbp_api_client.downloader.Path;
import exchange_rate.nbp_api_client.dto.Country;
import exchange_rate.nbp_api_client.dto.ExchangeRate;
import exchange_rate.nbp_api_client.exception.checked.NotFoundException;
import exchange_rate.nbp_api_client.exception.unchecked.BadRequestException;
import exchange_rate.nbp_api_client.exception.unchecked.ConvertResponseException;
import exchange_rate.nbp_api_client.validator.Validator;

public class NbpClient {

	private final Downloader downloader;
	private final Path path;
	private final Converter converter;
	private final Validator validator = new Validator();
	private CountryRepository countryRepository;
	private Cache cache;

	public NbpClient(Downloader downloader, Path path, Converter converter, CountryRepository countryRepository,
			Cache cache) {
		this.downloader = downloader;
		this.path = path;
		this.converter = converter;
		this.cache = cache;
		this.countryRepository = countryRepository;
	}

	public NbpClient(Downloader downloader, Path path, Converter converter, CountryRepository countryRepository) {
		this.downloader = downloader;
		this.path = path;
		this.converter = converter;
		this.cache = null;
		this.countryRepository = countryRepository;
	}

	public NbpClient(Downloader downloader, Path path, Converter converter, Cache cache) {
		this.downloader = downloader;
		this.path = path;
		this.converter = converter;
		this.cache = cache;
		this.countryRepository = null;
	}

	public NbpClient(Downloader downloader, Path path, Converter converter) {
		this.downloader = downloader;
		this.path = path;
		this.converter = converter;
		this.cache = null;
		this.countryRepository = null;
	}

	public ExchangeRate requestActualExchangeRate(Currency currency) {
		ExchangeRate result;
		if (cache != null) {
			result = cache.getOrNull(currency, new Date());
			if (result != null) {
				return result;
			}
		}

		DownloaderResponse response = downloader.get(path.get(currency, converter.getDataFormat()));
		validator.validate(response);
		List<ExchangeRate> list = converter.convertCurrencyResponse(response.getBody());
		if (list.size() > 1) {
			throw new ConvertResponseException("Cannot convert response, numerous items.");
		}
		result = list.get(0);
		if (cache != null) {
			cache.saveOrUpdateIfExists(result);
		}
		return result;
	}

	public ExchangeRate requestExchangeRate(Currency currency, Date date) {

		ExchangeRate result;
		if (cache != null) {
			result = cache.getOrNull(currency, new Date());
			if (result != null) {
				return result;
			}
		}

		LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

		int counter = 0;
		DownloaderResponse response;
		while (counter < 5) {
			try {
				Date requestDate = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
				response = downloader.get(path.get(currency, requestDate, converter.getDataFormat()));
				validator.validate(response);
				List<ExchangeRate> list = converter.convertCurrencyResponse(response.getBody());
				result = list.get(0);
				if (cache != null) {
					cache.saveOrUpdateIfExists(result);
				}
				return result;
			} catch (NotFoundException e) {
				counter++;
				dateTime = dateTime.minusDays(1);
			}
		}

		throw new NotFoundException("Cannot find exchange rate for that date! Make sure that data is correct.");
	}

	public List<ExchangeRate> requestActualExchangeRate(CountryName countryName) {
		if (countryRepository == null) {
			throw new BadRequestException("Country repository didn't set, cannot request by country");
		}

		Country country = countryRepository.get(countryName);
		if (country == null) {
			throw new NotFoundException("Not Found country for that name: " + countryName);
		}

		DownloaderResponse response = downloader.get(path.getAll(converter.getDataFormat()));
		validator.validate(response);
		List<ExchangeRate> resultList = converter.convertTableResponse(response.getBody()).stream()
				.filter(e -> country.getCurrencies().contains(e.getCurrency())).collect(Collectors.toList());

		if (cache != null) {
			resultList.forEach(e -> cache.saveOrUpdateIfExists(e));
		}

		return resultList;
	}

	public List<ExchangeRate> requestExchangeRate(CountryName countryName, Date date) {

		if (countryRepository == null) {
			throw new BadRequestException("Country repository didn't set, cannot request by country");
		}

		Country country = countryRepository.get(countryName);
		if (country == null) {
			throw new NotFoundException("Not Found country for that name: " + countryName);
		}

		LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

		int counter = 0;
		DownloaderResponse response;
		while (counter < 5) {
			try {
				Date requestDate = Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
				response = downloader.get(path.getAll(requestDate, converter.getDataFormat()));
				validator.validate(response);
				List<ExchangeRate> resultList = converter.convertTableResponse(response.getBody()).stream()
						.filter(e -> country.getCurrencies().contains(e.getCurrency())).collect(Collectors.toList());

				if (cache != null) {
					resultList.forEach(e -> cache.saveOrUpdateIfExists(e));
				}
				return resultList;
			} catch (NotFoundException e) {
				counter++;
				dateTime = dateTime.minusDays(1);
			}
		}

		throw new NotFoundException("Cannot find exchange rate for that date! Make sure that data is correct.");
	}
}
