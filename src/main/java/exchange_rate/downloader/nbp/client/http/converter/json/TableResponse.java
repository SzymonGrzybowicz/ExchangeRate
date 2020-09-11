package exchange_rate.downloader.nbp.client.http.converter.json;

public class TableResponse {

	private final String table;
	private final String no;
	private final String effectiveDate;
	private final TableRate[] rates;

	public TableResponse(String table, String no, String effectiveDate, TableRate[] rates) {
		this.table = table;
		this.no = no;
		this.effectiveDate = effectiveDate;
		this.rates = rates;
	}

	public String getTable() {
		return table;
	}

	public String getNo() {
		return no;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public TableRate[] getRates() {
		return rates;
	}

	class TableRate {
		private final String currency;
		private final String code;
		private final String mid;

		public TableRate(String currency, String code, String mid) {
			this.currency = currency;
			this.code = code;
			this.mid = mid;
		}

		public String getCurrency() {
			return currency;
		}

		public String getCode() {
			return code;
		}

		public String getMid() {
			return mid;
		}
	}
}
