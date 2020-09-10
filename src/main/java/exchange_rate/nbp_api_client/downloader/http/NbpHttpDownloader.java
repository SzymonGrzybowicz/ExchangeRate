package exchange_rate.nbp_api_client.downloader.http;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.downloader.Downloader;
import exchange_rate.nbp_api_client.downloader.http.converter.Converter;
import exchange_rate.nbp_api_client.downloader.http.converter.json.JsonConverter;
import exchange_rate.nbp_api_client.dto.ExchangeRate;
import exchange_rate.nbp_api_client.exception.checked.NotFoundException;
import exchange_rate.nbp_api_client.exception.unchecked.BadRequestException;
import exchange_rate.nbp_api_client.exception.unchecked.ConnectionException;
import exchange_rate.nbp_api_client.exception.unchecked.DataFormatException;

public class NbpHttpDownloader implements Downloader {

	private OkHttpClient client = new OkHttpClient();
	private Converter converter = new JsonConverter();
	private final static String CURRENCY_URL = "http://api.nbp.pl/api/exchangerates/rates/a";

	public void setConverter(Converter converter) {
		this.converter = converter;
	}

	public void setHttpClient(OkHttpClient client) {
		this.client = client;
	}

	@Override
	public ExchangeRate get(Currency currency) {
		Request request = new Request.Builder().url(getUrl(currency)).build();
		try {
			Response response = client.newCall(request).execute();

			switch (response.code()) {
			case 200:
				return converter.convertCurrencyResponse(response.body().string());
			case 404:
				throw new NotFoundException("Cannot find actual exchange rate for currency: " + currency
						+ "http response: " + response.toString());
			default:
				throw new BadRequestException("Nbp api response: " + response.toString());
			}

		} catch (IOException e) {
			throw new ConnectionException("Cannot connect to nbp api because of: " + e.toString());
		}
	}

	@Override
	public ExchangeRate get(Currency currency, Date date) {
		Request request = new Request.Builder().url(getUrl(currency, date)).build();
		try {
			Response response = client.newCall(request).execute();

			switch (response.code()) {
			case 200:
				return converter.convertCurrencyResponse(response.body().string());
			case 404:
				throw new NotFoundException("Cannot find actual exchange rate for currency: " + currency
						+ "http response: " + response.toString());
			default:
				throw new BadRequestException("Nbp api response: " + response.toString());
			}

		} catch (IOException e) {
			throw new ConnectionException("Cannot connect to nbp api because of: " + e.toString());
		}
	}

	private String getUrl(Currency currency) {
		return CURRENCY_URL + "/" + currency.getAlphabeticCode() + getUrlPostfix();
	}

	private String getUrl(Currency currency, Date date) {
		return CURRENCY_URL + "/" + currency.getAlphabeticCode() + "/" + new SimpleDateFormat("yyyy-MM-dd").format(date)
				+ getUrlPostfix();
	}

	private String getUrlPostfix() {
		switch (converter.getDataFormat()) {
		case JSON:
			return "?format=json";
		case XML:
			return "?format=xml";
		default:
			throw new DataFormatException(
					"Data format: " + converter.getDataFormat() + " is not supported in Http downloader");
		}
	}
}
