package exchange_rate.downloader.http.converter.json;

class CurrencyResponse {
	private String table;
	private String code;
	private CurrencyRate[] rates;

	public String getTable() {
		return table;
	}

	public String getCode() {
		return code;
	}

	public CurrencyRate[] getRates() {
		return rates;
	}

	public class CurrencyRate {

		private String no;
		private String effectiveDate;
		private String mid;

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
