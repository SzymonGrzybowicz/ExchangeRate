package exchange_rate;

import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import exchange_rate.database.Database;
import exchange_rate.dto.ExchangeRate;
import exchange_rate.enums.CountryName;
import exchange_rate.enums.Currency;
import exchange_rate.service.ExchangeRateService;

public class ExchangeRateFasade {

	private final ExchangeRateService service = new ExchangeRateService(Database.getInstance());

	public ExchangeRate getExchangeRate(Currency currency) {
		return service.getActualExchangeRate(currency);
	}

	public ExchangeRate getExchangeRate(Currency currency, LocalDate date) {
		return service.getExchangeRate(currency, date);
	}

	public List<ExchangeRate> getExchangeRates(CountryName countryName) {
		return service.getActualExchangeRates(countryName);
	}

	public List<ExchangeRate> getExchangeRates(CountryName countryName, LocalDate date) {
		return service.getExchangeRates(countryName, date);
	}

	public List<ExchangeRate> getExchangeRatesForPeroid(Currency currency, LocalDate startDate, LocalDate endDate) {
		return service.getExchangeRatesForPeroid(currency, startDate, endDate);
	}

	public ExchangeRate getMaximumRateForPeroid(Currency currency, LocalDate startDate, LocalDate endDate) {
		return service.getMaximumRateForPeroid(currency, startDate, endDate);
	}

	public ExchangeRate getMinimumRateForPeroid(Currency currency, LocalDate startDate, LocalDate endDate) {
		return service.getMinimumRateForPeroid(currency, startDate, endDate);
	}

	public Pair<ExchangeRate, ExchangeRate> getBiggestRateDifferenceInPeroid(Currency currency, LocalDate startDate,
			LocalDate endDate) {
		return service.getBiggestRateDifferenceInPeroid(currency, startDate, endDate);
	}

	public Currency getCurrencyWithBiggestRateDifferenceInPeroid(List<Currency> rates, LocalDate startDate,
			LocalDate endDate) {
		return service.getCurrencyWithBiggestRateDifferenceInPeroid(rates, startDate, endDate);
	}
}
