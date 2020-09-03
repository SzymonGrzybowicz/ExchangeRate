package exchange_rate;

import java.util.Calendar;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.NbpClient;
import exchange_rate.nbp_api_client.dto.ExchangeRate;
import exchange_rate.nbp_api_client.exception.NbpWebApiException;
import exchange_rate.nbp_api_client.strategy.http.HttpJsonNbpClientStrategy;

public class Main {

	public static void main(String[] args) throws NbpWebApiException {

		NbpClient client = new NbpClient(new HttpJsonNbpClientStrategy());
		Calendar calendar = Calendar.getInstance();
		calendar.set(2001, 7, 30);
		ExchangeRate rate = client.requestExchangeRate(Currency.EURO, calendar.getTime());
		System.out.println(rate.getRate());
	}
}