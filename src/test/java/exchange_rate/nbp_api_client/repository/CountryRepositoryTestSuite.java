package exchange_rate.nbp_api_client.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import exchange_rate.nbp_api_client.CountryName;
import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.dto.Country;
import exchange_rate.nbp_api_client.exception.checked.NotFoundException;

public class CountryRepositoryTestSuite {

	@Test
	public void test_saveGet() {
		// Given
		CountryName countryName = CountryName.WAKANDA;
		Set<Currency> currencies = new HashSet<>();
		currencies.add(Currency.EURO);
		currencies.add(Currency.AMERICAN_DOLAR);
		Country country = new Country(countryName, currencies);

		CountryRepository repository = new CountryRepository();

		// When
		repository.save(country);
		Country result = repository.get(countryName);

		// Then
		assertThat(result).isEqualTo(country);

		// Clean up
		repository.delete(countryName);
	}

	@Test
	public void test_delete() {
		// Given
		CountryName countryName = CountryName.WAKANDA;
		Set<Currency> currencies = new HashSet<>();
		currencies.add(Currency.EURO);
		currencies.add(Currency.AMERICAN_DOLAR);
		Country country = new Country(countryName, currencies);

		CountryRepository repository = new CountryRepository();

		repository.save(country);

		// When
		repository.delete(countryName);
		Exception e = assertThrows(NotFoundException.class, () -> repository.get(countryName));

		// Then
		assertThat(e).hasMessageContaining("Cannot find data for country name");
	}

	@Test
	public void test_addCurrency() {
		// Given
		CountryName countryName = CountryName.WAKANDA;
		Set<Currency> currencies = new HashSet<>();
		currencies.add(Currency.EURO);
		currencies.add(Currency.AMERICAN_DOLAR);
		CountryRepository repository = new CountryRepository();
		repository.save(new Country(countryName, currencies));

		Currency addedCurrency = Currency.POUND_STERLING;

		// When
		repository.addCurrency(countryName, addedCurrency);
		Country result = repository.get(countryName);

		// Then
		assertThat(result.getCurrencies()).contains(addedCurrency);

		// Clean up
		repository.delete(countryName);
	}

	@Test
	public void test_removeCurrency() {
		// Given
		CountryName countryName = CountryName.WAKANDA;
		Set<Currency> currencies = new HashSet<>();

		Currency removedCurrency = Currency.POUND_STERLING;

		currencies.add(Currency.EURO);
		currencies.add(Currency.AMERICAN_DOLAR);
		currencies.add(removedCurrency);
		CountryRepository repository = new CountryRepository();
		repository.save(new Country(countryName, currencies));

		// When
		repository.removeCurrency(countryName, removedCurrency);
		Country result = repository.get(countryName);

		// Then
		assertThat(result.getCurrencies()).doesNotContain(removedCurrency);

		// Clean up
		repository.delete(countryName);
	}
}
