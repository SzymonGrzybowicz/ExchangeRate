package country;

import java.util.List;

import country.dto.Country;
import enums.CountryName;
import enums.Currency;

public interface CountryRepository {

	Country get(CountryName countryName);

	void save(Country country);

	void addCurrency(CountryName countryName, Currency currency);

	void removeCurrency(CountryName countryName, Currency currency);

	void delete(CountryName countryName);

	List<Country> getCountriesHasMoreThanOneCurrency();
}
