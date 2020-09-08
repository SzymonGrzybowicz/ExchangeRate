package exchange_rate.nbp_api_client.country.database.entity;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;

import exchange_rate.nbp_api_client.CountryName;
import exchange_rate.nbp_api_client.Currency;

@Entity(name = "country")
public class CountryEntity {

	public CountryEntity() {
	}

	public CountryEntity(CountryName name, Set<Currency> currencies) {
		this.name = name;
		this.currencies = currencies;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "country_id")
	private int id;

	@Column(name = "country_name", unique = true)
	@Enumerated(EnumType.STRING)
	private CountryName name;

	@ElementCollection(targetClass = Currency.class, fetch = FetchType.EAGER)
	@JoinTable(name = "inner_country_currencies", joinColumns = @JoinColumn(referencedColumnName = "country_id"))
	@Column(name = "currency", nullable = false)
	@Enumerated(EnumType.STRING)
	private Set<Currency> currencies;

	public CountryName getName() {
		return name;
	}

	public Set<Currency> getCurrencies() {
		return currencies;
	}

	public void setCurrencies(Set<Currency> currencies) {
		this.currencies = currencies;
	}
}
