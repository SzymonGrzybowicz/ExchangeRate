package training.country.database.mapper;

import org.springframework.stereotype.Component;

import training.country.database.entity.CountryEntity;
import training.country.dto.Country;

@Component
public class CountryMapper {

	public CountryEntity map(Country country) {
		return new CountryEntity(country.getName(), country.getCurrencies());
	}

	public Country map(CountryEntity entity) {
		return new Country(entity.getName(), entity.getCurrencies());
	}
}
