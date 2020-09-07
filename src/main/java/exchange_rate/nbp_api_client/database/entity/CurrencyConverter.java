package exchange_rate.nbp_api_client.database.entity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import exchange_rate.nbp_api_client.Currency;

@Converter(autoApply = true)
public class CurrencyConverter implements AttributeConverter<Currency, String> {

	@Override
	public String convertToDatabaseColumn(Currency currency) {
		return currency.getAlphabeticCode();
	}

	@Override
	public Currency convertToEntityAttribute(String alphabeticCode) {
		return Currency.byAlphabeticCode(alphabeticCode);
	}
}
