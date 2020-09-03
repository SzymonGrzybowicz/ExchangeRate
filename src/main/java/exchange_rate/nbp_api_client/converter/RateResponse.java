package exchange_rate.nbp_api_client.converter;

public class RateResponse {
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

	public RateResponse(String table, String currency, String code) {
		super();
		this.table = table;
		this.currency = currency;
		this.code = code;
	}

	public class Rate {

		private String no;
		private String effectiveDate;
		private String mid;

		public Rate(String no, String effectiveDate, String mid) {
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

		public String getMid() {
			return mid;
		}
	}
}
