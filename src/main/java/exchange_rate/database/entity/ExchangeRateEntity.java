package exchange_rate.database.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import enums.Currency;

@NamedQueries({
		@NamedQuery(name = ExchangeRateEntity.QUERY_BY_CURRENCY_AND_DATE, query = "FROM ExchangeRateEntity e WHERE e.date = :"
				+ ExchangeRateEntity.PARAMETER_DATE + " AND e.currency = :" + ExchangeRateEntity.PARAMETER_CURRENCY),
		@NamedQuery(name = ExchangeRateEntity.QUERY_BY_CURRENCY_AND_DATE_SORTED_BY_RATE, query = "FROM ExchangeRateEntity e WHERE e.currency = :"
				+ ExchangeRateEntity.PARAMETER_CURRENCY + " AND e.date BETWEEN :"
				+ ExchangeRateEntity.PARAMETER_START_DATE + " AND :" + ExchangeRateEntity.PARAMETER_END_DATE
				+ " ORDER BY e.rate"),
		@NamedQuery(name = ExchangeRateEntity.QUERY_BY_CURRENCY_AND_DATE_SORTED_BY_RATE_DESC, query = "FROM ExchangeRateEntity e WHERE e.currency = :"
				+ ExchangeRateEntity.PARAMETER_CURRENCY + " AND e.date BETWEEN :"
				+ ExchangeRateEntity.PARAMETER_START_DATE + " AND :" + ExchangeRateEntity.PARAMETER_END_DATE
				+ " ORDER BY e.rate DESC"),
		@NamedQuery(name = ExchangeRateEntity.QUERY_CURRENCIES_BY_PERIOD_SORTED_BY_HIGHEST_RATE_DIFFERENCE_DESC, query = "SELECT e.currency FROM ExchangeRateEntity e WHERE e.date BETWEEN :"
				+ ExchangeRateEntity.PARAMETER_START_DATE + " AND : " + ExchangeRateEntity.PARAMETER_END_DATE
				+ " GROUP BY e.currency ORDER BY (MAX(e.rate)-MIN(e.rate)) DESC"),
		@NamedQuery(name = ExchangeRateEntity.QUERY_BY_CURRENCY_SORTED_BY_RATE, query = "FROM ExchangeRateEntity e WHERE e.currency = :"
				+ ExchangeRateEntity.PARAMETER_CURRENCY + " ORDER BY e.rate"),
		@NamedQuery(name = ExchangeRateEntity.QUERY_BY_CURRENCY_SORTED_BY_RATE_DESC, query = "FROM ExchangeRateEntity e WHERE e.currency = :"
				+ ExchangeRateEntity.PARAMETER_CURRENCY + " ORDER BY e.rate DESC") })
@Entity
@Table(name = "exchange_rate")
public class ExchangeRateEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String QUERY_BY_CURRENCY_AND_DATE = "ExchangeRate.get_byCurrencyAndDate";
	public static final String QUERY_BY_CURRENCY_AND_DATE_SORTED_BY_RATE = "ExchangeRate.get_byCurrencyAndDate_sortedByRate";
	public static final String QUERY_BY_CURRENCY_AND_DATE_SORTED_BY_RATE_DESC = "ExchangeRate.get_byCurrencyAndDate_sortedByRate_desc";
	public static final String QUERY_CURRENCIES_BY_PERIOD_SORTED_BY_HIGHEST_RATE_DIFFERENCE_DESC = "ExchangeRate.get_currencies_byPeriod_sortedByHighestRate_desc";
	public static final String QUERY_BY_CURRENCY_SORTED_BY_RATE = "ExchangeRate.get_byCurrency_sortedByRate";
	public static final String QUERY_BY_CURRENCY_SORTED_BY_RATE_DESC = "ExchangeRate.get_byCurrency_sortedByRate_desc";
	public static final String PARAMETER_DATE = "date";
	public static final String PARAMETER_START_DATE = "start_date";
	public static final String PARAMETER_END_DATE = "end_date";
	public static final String PARAMETER_CURRENCY = "currency";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private long id;

	@Column(precision = 12, scale = 6)
	private BigDecimal rate;

	@Column
	private LocalDate date;

	@Column
	@Enumerated(EnumType.STRING)
	private Currency currency;

	public ExchangeRateEntity() {
	}

	public ExchangeRateEntity(BigDecimal rate, LocalDate date, Currency currency) {
		super();
		this.rate = rate;
		this.date = date;
		this.currency = currency;
	}

	public long getId() {
		return id;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public LocalDate getDate() {
		return date;
	}

	public Currency getCurrency() {
		return currency;
	}

	@Override
	public String toString() {
		return "ExchangeRateEntity [id=" + id + ", rate=" + rate + ", date=" + date + ", currency=" + currency + "]";
	}

}
