package exchange_rate.nbp_api_client.database.exchange_rate.entity;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import exchange_rate.nbp_api_client.Currency;

@Entity(name = "exchange_rate")
public class ExchangeRateEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;

	@Column(name = "rate", precision = 12, scale = 6)
	private BigDecimal rate;

	@Column(name = "date")
	@Temporal(TemporalType.DATE)
	private Date date;

	@Column(name = "currency")
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
