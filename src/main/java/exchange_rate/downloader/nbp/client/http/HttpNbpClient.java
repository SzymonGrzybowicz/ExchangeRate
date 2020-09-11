package exchange_rate.downloader.nbp.client.http;

import java.io.IOException;
import java.time.LocalDate;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import exchange_rate.downloader.nbp.client.NbpClient;
import exchange_rate.downloader.nbp.client.http.converter.Converter;
import exchange_rate.downloader.nbp.client.http.converter.json.JsonConverter;
import exchange_rate.downloader.nbp.exception.checked.NotFoundException;
import exchange_rate.downloader.nbp.exception.unchecked.BadRequestException;
import exchange_rate.downloader.nbp.exception.unchecked.ConnectionException;
import exchange_rate.downloader.nbp.exception.unchecked.DataFormatException;
import exchange_rate.dto.ExchangeRate;
import exchange_rate.enums.Currency;

public class HttpNbpClient implements NbpClient {

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
	public ExchangeRate get(Currency currency, LocalDate date) {
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

	private String getUrl(Currency currency, LocalDate date) {
		return CURRENCY_URL + "/" + currency.getAlphabeticCode() + "/" + date + getUrlPostfix();
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
