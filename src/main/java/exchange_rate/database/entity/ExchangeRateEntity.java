package exchange_rate.database.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ExchangeRateEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private long id;

	@Column(name = "rate")
	private double rate;

	@Column(name = "date")
	private Date date;

	@Column(name = "currencyAlphabeticalCode")
	private String currencyAlphabeticalCode;

	public ExchangeRateEntity() {
	}

	public ExchangeRateEntity(double rate, Date date, String currencyAlphabeticalCode) {
		super();
		this.rate = rate;
		this.date = date;
		this.currencyAlphabeticalCode = currencyAlphabeticalCode;
	}

	public long getId() {
		return id;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getCurrencyAlphabeticalCode() {
		return currencyAlphabeticalCode;
	}

	public void setCurrencyAlphabeticalCode(String currencyAlphabeticalCode) {
		this.currencyAlphabeticalCode = currencyAlphabeticalCode;
	}
}
