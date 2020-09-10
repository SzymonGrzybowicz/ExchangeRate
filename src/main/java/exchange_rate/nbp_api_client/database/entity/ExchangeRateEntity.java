package exchange_rate.nbp_api_client.database.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import exchange_rate.nbp_api_client.Currency;

@NamedQueries({
		@NamedQuery(name = ExchangeRateEntity.QUERY_GET_BY_CURRENCY_AND_DATE, query = "FROM ExchangeRateEntity e WHERE e.date = :"
				+ ExchangeRateEntity.PARAMETER_DATE + " AND e.currency = :" + ExchangeRateEntity.PARAMETER_CURRENCY) })
@Entity
@Table(name = "exchange_rate")
public class ExchangeRateEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String QUERY_GET_BY_CURRENCY_AND_DATE = "ExchangeRate.get_byCurrencyAndDate";
	public static final String PARAMETER_DATE = "date";
	public static final String PARAMETER_CURRENCY = "currency";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column
	private long id;

	@Column(precision = 12, scale = 6)
	private BigDecimal rate;

	@Column
	@Temporal(TemporalType.DATE)
	private Date date;

	@Column
	@Enumerated(EnumType.STRING)
	private Currency currency;

	public ExchangeRateEntity() {
	}

	public ExchangeRateEntity(BigDecimal rate, Date date, Currency currency) {
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

	public Date getDate() {
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
