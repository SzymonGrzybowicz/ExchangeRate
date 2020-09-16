package training.country.database.entity;

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

import training.enums.CountryName;
import training.enums.Currency;

@NamedQueries({
		@NamedQuery(name = CountryEntity.QUERY_BY_COUNTRY_NAME, query = "FROM CountryEntity e INNER JOIN FETCH e.currencies c WHERE e.name = :"
				+ CountryEntity.PARAMETER_NAME),
		@NamedQuery(name = CountryEntity.QUERY_WHERE_HAS_MORE_THAN_TWO_CURRENCY, query = "FROM CountryEntity e WHERE SIZE(e.currencies) > 1") })
@Entity
@Table(name = "country")
public class CountryEntity {

	public static final String QUERY_BY_COUNTRY_NAME = "CountryEntity.get_byCountryName";
	public static final String QUERY_WHERE_HAS_MORE_THAN_TWO_CURRENCY = "CountryEntity.get_whereHasMoreThan_oneCurrency";
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

	@ElementCollection(targetClass = Currency.class, fetch = FetchType.LAZY)
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
