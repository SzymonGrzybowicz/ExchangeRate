package country.dto;

import java.util.HashSet;
import java.util.Set;

import enums.CountryName;
import enums.Currency;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currencies == null) ? 0 : currencies.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Country other = (Country) obj;
		if (currencies == null) {
			if (other.currencies != null)
				return false;
		} else if (!currencies.equals(other.currencies))
			return false;
		if (name != other.name)
			return false;
		return true;
	}

}
