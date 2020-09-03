package exchange_rate.nbp_api_client.dto;

import java.math.BigDecimal;
import java.util.Date;

import exchange_rate.nbp_api_client.Currency;

public class ExchangeRate {

	private Date date;
	private Currency currency;
	private BigDecimal rate;

	public ExchangeRate(Date date, Currency currency, BigDecimal rate) {
		this.date = date;
		this.currency = currency;
		this.rate = rate;
	}

	public Date getDate() {
		return date;
	}

	public Currency getCurrency() {
		return currency;
	}

	public BigDecimal getRate() {
		return rate;
	}
}
