package exchange_rate;

import java.util.Calendar;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.NbpClient;
import exchange_rate.nbp_api_client.converter.Converter;
import exchange_rate.nbp_api_client.converter.json.JsonConverter;
import exchange_rate.nbp_api_client.downloader.Downloader;
import exchange_rate.nbp_api_client.downloader.Path;
import exchange_rate.nbp_api_client.downloader.http.HttpDownloader;
import exchange_rate.nbp_api_client.downloader.http.HttpPath;
import exchange_rate.nbp_api_client.dto.ExchangeRate;

public class Main {

	public static void main(String[] args) {

		Downloader downloader = new HttpDownloader();
		Path path = new HttpPath();
		Converter converter = new JsonConverter();

		NbpClient client = new NbpClient(downloader, path, converter);
		System.out.println(client.requestActualExchangeRate(Currency.AMERICAN_DOLAR).getRate());

		Calendar calendar = Calendar.getInstance();
		calendar.set(2020, 7, 30);
		ExchangeRate rate = client.requestExchangeRate(Currency.EURO, calendar.getTime());
		System.out.println(rate.getRate());
		System.out.println(rate.getDate());
	}
}