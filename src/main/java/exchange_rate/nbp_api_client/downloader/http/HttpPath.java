package exchange_rate.nbp_api_client.downloader.http;

import java.text.SimpleDateFormat;
import java.util.Date;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.downloader.DataFormat;
import exchange_rate.nbp_api_client.downloader.Path;

public class HttpPath implements Path {

	private final static String CURRENCY_URL = "http://api.nbp.pl/api/exchangerates/rates/a";
	private final static String TABLE_URL = "http://api.nbp.pl/api/exchangerates/tables/a";

	@Override
	public String get(Currency currency, DataFormat dataFormat) {
		return CURRENCY_URL + "/" + currency.getAlphabeticCode() + dataFormat.getHttpUrlPostfix();
	}

	@Override
	public String get(Currency currency, Date date, DataFormat dataFormat) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return CURRENCY_URL + "/" + currency.getAlphabeticCode() + "/" + df.format(date)
				+ dataFormat.getHttpUrlPostfix();
	}

	@Override
	public String getAll(DataFormat dataFormat) {
		return TABLE_URL + dataFormat.getHttpUrlPostfix();
	}

	@Override
	public String getAll(Date date, DataFormat dataFormat) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return TABLE_URL + "/" + df.format(date) + dataFormat.getHttpUrlPostfix();
	}

}
