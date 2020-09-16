package training;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import training.enums.Currency;
import training.exchange_rate.NbpExchangeRateClient;

@ComponentScan
@Configuration
public class Application {

	@Autowired
	private NbpExchangeRateClient client;

	public void start() {
		System.out.println(client.getActualExchangeRate(Currency.AMERICAN_DOLAR));
	}
}
