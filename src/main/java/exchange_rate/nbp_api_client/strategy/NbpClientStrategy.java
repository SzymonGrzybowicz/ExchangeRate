package exchange_rate.nbp_api_client.strategy;

import java.util.Date;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.converter.RateConverter;
import exchange_rate.nbp_api_client.validator.Validator;
import exchange_rate.web_client.WebClient;

public interface NbpClientStrategy {
	WebClient getWebClient();

	RateConverter getRateConverter();

	Validator getValidator();

	String getActualCurrencyRateUrl(Currency currency);

	String getCurrencyRateUrl(Currency currency, Date date);
}
