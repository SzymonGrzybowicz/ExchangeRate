package exchange_rate.nbp_api_client.dto;

import java.util.HashSet;
import java.util.Set;

import exchange_rate.nbp_api_client.CountryName;
import exchange_rate.nbp_api_client.Currency;

public class Country {

	private final CountryName name;
	private final Set<Currency> currencies;

	public Country(CountryName name, Set<Currency> currencies) {
		this.name = name;
		this.currencies = currencies;
	}

	public CountryName getName() {
		return name;
	}

	public Set<Currency> getCurrencies() {
		return new HashSet<>(currencies);
	}
}
