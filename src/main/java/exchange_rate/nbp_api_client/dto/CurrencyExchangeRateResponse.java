package exchange_rate.nbp_api_client.dto;

public class CurrencyExchangeRateResponse {
	private String table;
	private String currency;
	private String code;
	private Rate[] rates;
	
	public String getTable() {
		return table;
	}

	public String getCurrency() {
		return currency;
	}

	public String getCode() {
		return code;
	}

	public Rate[] getRates() {
		return rates;
	}

	public CurrencyExchangeRateResponse(String table, String currency, String code) {
		super();
		this.table = table;
		this.currency = currency;
		this.code = code;
	}

	
	public class Rate {
		
		private String no;
		private String effectiveDate;
		private double mid;
		
		public Rate(String no, String effectiveDate, double mid) {
			super();
			this.no = no;
			this.effectiveDate = effectiveDate;
			this.mid = mid;
		}

		public String getNo() {
			return no;
		}

		public String getEffectiveDate() {
			return effectiveDate;
		}

		public double getMid() {
			return mid;
		}
	}
}
