package nbp_web_api_client;

public enum Currency {
	
	EURO("EUR"),
	AMERICAN_DOLAR("USD"), 
	POUND_STERLING("GBP");
	
	String alphabeticCode;
	
	private Currency(String alphabeticCode) {
		this.alphabeticCode = alphabeticCode;
	}
}
