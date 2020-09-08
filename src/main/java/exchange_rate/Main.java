package exchange_rate;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import exchange_rate.nbp_api_client.CountryName;
import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.NbpClient;
import exchange_rate.nbp_api_client.converter.Converter;
import exchange_rate.nbp_api_client.converter.json.JsonConverter;
import exchange_rate.nbp_api_client.country.CountryRepository;
import exchange_rate.nbp_api_client.country.database.DatabaseCountryRepository;
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
		CountryRepository repo = new DatabaseCountryRepository();

		NbpClient client = new NbpClient(downloader, path, converter, repo);
		System.out.println(client.requestActualExchangeRate(Currency.AMERICAN_DOLAR).getRate());

		Calendar calendar = Calendar.getInstance();
		calendar.set(2020, 7, 30);
		ExchangeRate rate = client.requestExchangeRate(Currency.EURO, calendar.getTime());
		System.out.println(rate.getRate());
		System.out.println(rate.getDate());

		ExchangeRate rate2 = client.requestExchangeRate(Currency.EURO, calendar.getTime());

		System.out.println(rate.equals(rate2));

		Set<Currency> currencies = new HashSet<Currency>();
		currencies.add(Currency.EURO);
		currencies.add(Currency.AMERICAN_DOLAR);

		System.out.println(client.requestActualExchangeRate(CountryName.WAKANDA));

		System.out.println(client.requestExchangeRate(CountryName.WAKANDA, calendar.getTime()));
	}
}