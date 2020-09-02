package exchange_rate.nbp_api_client;

public enum Currency {

	EURO("EUR"), AMERICAN_DOLAR("USD"), POUND_STERLING("GBP");

	private String alphabeticCode;

	private Currency(String alphabeticCode) {
		this.alphabeticCode = alphabeticCode;
	}

	public String getAlphabeticCode() {
		return alphabeticCode;
	}

}
