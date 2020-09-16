package training.country;

import java.util.List;

import training.country.dto.Country;
import training.enums.CountryName;
import training.enums.Currency;

public interface CountryRepository {

	Country get(CountryName countryName);

	void save(Country country);

	void addCurrency(CountryName countryName, Currency currency);

	void removeCurrency(CountryName countryName, Currency currency);

	void delete(CountryName countryName);

	List<Country> getCountriesHasMoreThanOneCurrency();
}
