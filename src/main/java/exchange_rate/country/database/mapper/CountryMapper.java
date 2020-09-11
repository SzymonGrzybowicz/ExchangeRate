package exchange_rate.country.database.mapper;

import exchange_rate.country.database.entity.CountryEntity;
import exchange_rate.dto.Country;

public class CountryMapper {

	public CountryEntity map(Country country) {
		return new CountryEntity(country.getName(), country.getCurrencies());
	}

	public Country map(CountryEntity entity) {
		return new Country(entity.getName(), entity.getCurrencies());
	}
}
