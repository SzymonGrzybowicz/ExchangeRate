package exchange_rate.nbp_api_client.strategy.http;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.converter.RateConverter;
import exchange_rate.nbp_api_client.converter.json.JsonRateConverter;
import exchange_rate.nbp_api_client.strategy.NbpClientStrategy;
import exchange_rate.nbp_api_client.validator.Validator;
import exchange_rate.nbp_api_client.validator.http.HttpValidator;
import exchange_rate.web_client.WebClient;
import exchange_rate.web_client.http.HttpWebClient;

public class HttpJsonNbpClientStrategy implements NbpClientStrategy {

	private WebClient webClient = new HttpWebClient();
	private RateConverter rateConverter = new JsonRateConverter();
	private Validator validator = new HttpValidator();

	@Override
	public WebClient getWebClient() {
		return webClient;
	}

	@Override
	public RateConverter getRateConverter() {
		return rateConverter;
	}

	@Override
	public Validator getValidator() {
		return validator;
	}

	@Override
	public String getActualCurrencyRateUrl(Currency currency) {
		return "http://api.nbp.pl/api/exchangerates/rates/a/" + currency.getAlphabeticCode() + "/?format=json";
	}

	@Override
	public String getCurrencyRateUrl(Currency currency, Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return "http://api.nbp.pl/api/exchangerates/rates/a/" + currency.getAlphabeticCode() + "/" + df.format(date)
				+ "/?format=json";
	}

}
