package exchange_rate;

import java.time.LocalDate;
import java.util.List;

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
}
