package exchange_rate;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.NbpClient;
import exchange_rate.nbp_api_client.downloader.Downloader;
import exchange_rate.nbp_api_client.downloader.http.NbpHttpDownloader;

public class Main {

	public static void main(String[] args) {

		Downloader downloader = new NbpHttpDownloader();
		NbpClient client = new NbpClient(downloader);

		System.out.println(client.requestActualExchangeRate(Currency.EURO));
	}
}