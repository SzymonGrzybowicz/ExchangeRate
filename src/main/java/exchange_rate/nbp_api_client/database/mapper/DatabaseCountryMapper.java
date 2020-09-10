package exchange_rate.nbp_api_client.database.mapper;

import exchange_rate.nbp_api_client.database.entity.CountryEntity;
import exchange_rate.nbp_api_client.dto.Country;

public class DatabaseCountryMapper {

	public CountryEntity map(Country country) {
		return new CountryEntity(country.getName(), country.getCurrencies());
	}

	public Country map(CountryEntity entity) {
		return new Country(entity.getName(), entity.getCurrencies());
	}
}
