package exchange_rate.nbp_api_client.downloader.http;

import java.text.SimpleDateFormat;
import java.util.Date;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.downloader.DataFormat;
import exchange_rate.nbp_api_client.downloader.Path;

public class HttpPath implements Path {

	private final static String URL = "http://api.nbp.pl/api/exchangerates/rates/a";

	@Override
	public String get(Currency currency, DataFormat dataFormat) {
		return URL + "/" + currency.getAlphabeticCode() + "/" + dataFormat.getHttpUrlPostfix();
	}

	@Override
	public String get(Currency currency, Date date, DataFormat dataFormat) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return URL + "/" + currency.getAlphabeticCode() + "/" + df.format(date) + "/" + dataFormat.getHttpUrlPostfix();
	}

}
