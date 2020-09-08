package exchange_rate.nbp_api_client.country;

import exchange_rate.nbp_api_client.CountryName;
import exchange_rate.nbp_api_client.dto.Country;

public interface CountryRepository {

	Country get(CountryName countryName);

	void save(Country country);

	void update(Country country);

	void delete(Country country);
}
