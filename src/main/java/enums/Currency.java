package enums;

public enum Currency {

	EURO("EUR"), AMERICAN_DOLAR("USD"), POUND_STERLING("GBP");

	private String alphabeticCode;

	private Currency(String alphabeticCode) {
		this.alphabeticCode = alphabeticCode;
	}

	public String getAlphabeticCode() {
		return alphabeticCode;
	}

	public static Currency byAlphabeticCode(String alphabeticCode) {
		for (int i = 0; i < values().length; i++) {
			Currency value = values()[i];
			if (value.alphabeticCode.equals(alphabeticCode)) {
				return value;
			}
		}
		return null;
	}

}
