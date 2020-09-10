package exchange_rate.nbp_api_client.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import exchange_rate.nbp_api_client.Currency;

public class ExchangeRate {

	private LocalDate date;
	private Currency currency;
	private BigDecimal rate;

	public ExchangeRate(LocalDate date, Currency currency, BigDecimal rate) {
		this.date = date;
		this.currency = currency;
		this.rate = rate;
	}

	public LocalDate getDate() {
		return date;
	}

	public Currency getCurrency() {
		return currency;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currency == null) ? 0 : currency.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((rate == null) ? 0 : rate.hashCode());
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
		ExchangeRate other = (ExchangeRate) obj;
		if (currency != other.currency)
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.isEqual(other.date))
			return false;
		if (rate == null) {
			if (other.rate != null)
				return false;
		} else if (rate.compareTo(other.rate) != 0)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ExchangeRate [date=" + date + ", currency=" + currency + ", rate=" + rate + "]";
	}

}
