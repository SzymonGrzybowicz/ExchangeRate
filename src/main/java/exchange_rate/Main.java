package exchange_rate;

import com.squareup.okhttp.OkHttpClient;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.NbpClient;

public class Main {

	public static void main(String[] args) {
		NbpClient nbpClient = new NbpClient(new OkHttpClient());
		nbpClient.requestExchangeRate(Currency.EURO)
			.subscribe( v -> {
				System.out.println(v);
				}
			);
	}
}