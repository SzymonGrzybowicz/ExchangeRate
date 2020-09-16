package training.exchange_rate.downloader.http;

import java.io.IOException;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import training.enums.Currency;
import training.exchange_rate.downloader.NbpDownloader;
import training.exchange_rate.downloader.http.converter.Converter;
import training.exchange_rate.dto.ExchangeRate;
import training.exchange_rate.exception.checked.NotFoundException;
import training.exchange_rate.exception.unchecked.BadRequestException;
import training.exchange_rate.exception.unchecked.ConnectionException;
import training.exchange_rate.exception.unchecked.DataFormatException;

@Component
public class HttpNbpDownloader implements NbpDownloader {

	private OkHttpClient client;
	private Converter converter;

	@Autowired
	public HttpNbpDownloader(OkHttpClient client, Converter converter) {
		super();
		this.client = client;
		this.converter = converter;
	}

	private final static String CURRENCY_URL = "http://api.nbp.pl/api/exchangerates/rates/a";

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
