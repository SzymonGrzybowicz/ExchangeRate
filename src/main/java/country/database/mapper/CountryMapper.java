package country.database.mapper;

import country.database.entity.CountryEntity;
import country.dto.Country;

public class CountryMapper {

	public CountryEntity map(Country country) {
		return new CountryEntity(country.getName(), country.getCurrencies());
	}

	public Country map(CountryEntity entity) {
		return new Country(entity.getName(), entity.getCurrencies());
	}
}
