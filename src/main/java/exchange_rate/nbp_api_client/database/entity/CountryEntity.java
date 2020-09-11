package exchange_rate.nbp_api_client.database.entity;

import java.util.HashSet;
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
import javax.persistence.Table;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import exchange_rate.nbp_api_client.CountryName;
import exchange_rate.nbp_api_client.Currency;

@NamedQueries({ @NamedQuery(name = CountryEntity.QUERY_GET_BY_COUNTRY_NAME, query = "FROM CountryEntity WHERE name = :"
		+ CountryEntity.PARAMETER_NAME) })
@Entity
@Table(name = "country")
public class CountryEntity {

	public static final String QUERY_GET_BY_COUNTRY_NAME = "CountryEntity.get_byCountryName";
	public static final String PARAMETER_NAME = "name";

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
		return new HashSet<>(currencies);
	}

	public void addCurrency(Currency currency) {
		currencies.add(currency);
	}

	public void removeCurrency(Currency currency) {
		currencies.remove(currency);
	}
}
