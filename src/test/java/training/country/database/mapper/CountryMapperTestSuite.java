package training.country.database.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.assertj.core.util.Sets;
import org.junit.Test;

import training.country.database.entity.CountryEntity;
import training.country.dto.Country;
import training.enums.CountryName;
import training.enums.Currency;

public class CountryMapperTestSuite {

	@Test
	public void test_map_entity() {
		// Given
		CountryName name = CountryName.DEUTSCHLAND;
		Set<Currency> currencies = Sets.newLinkedHashSet(Currency.AMERICAN_DOLAR, Currency.EURO);
		CountryEntity entity = new CountryEntity(name, currencies);

		CountryMapper mapper = new CountryMapper();

		// When
		Country result = mapper.map(entity);

		// Then
		assertThat(result.getName()).isEqualTo(name);
		assertThat(result.getCurrencies()).isEqualTo(currencies);
	}

	@Test
	public void test_map_country() {
		// Given
		CountryName name = CountryName.DEUTSCHLAND;
		Set<Currency> currencies = Sets.newLinkedHashSet(Currency.AMERICAN_DOLAR, Currency.EURO);
		Country country = new Country(name, currencies);

		CountryMapper mapper = new CountryMapper();

		// When
		CountryEntity result = mapper.map(country);

		// Then
		assertThat(result.getName()).isEqualTo(name);
		assertThat(result.getCurrencies()).isEqualTo(currencies);
	}

}
