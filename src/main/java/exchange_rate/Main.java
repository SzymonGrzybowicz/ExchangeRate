package exchange_rate;

import java.time.LocalDate;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.repository.ExchangeRateRepository;

public class Main {

	public static void main(String[] args) {
		ExchangeRateRepository repo = new ExchangeRateRepository();
		repo.get(Currency.AMERICAN_DOLAR, LocalDate.now());
	}

}