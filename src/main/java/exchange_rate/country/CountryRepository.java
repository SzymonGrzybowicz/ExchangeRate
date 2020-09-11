package exchange_rate.country;

import exchange_rate.dto.Country;
import exchange_rate.enums.CountryName;
import exchange_rate.enums.Currency;

public interface CountryRepository {

	Country get(CountryName countryName);

	void save(Country country);

	void addCurrency(CountryName countryName, Currency currency);

	void removeCurrency(CountryName countryName, Currency currency);

	void delete(CountryName countryName);
}
