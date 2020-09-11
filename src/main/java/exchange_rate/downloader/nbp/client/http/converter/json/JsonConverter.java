package exchange_rate.downloader.nbp.client.http.converter.json;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import exchange_rate.downloader.nbp.client.http.converter.Converter;
import exchange_rate.downloader.nbp.client.http.converter.json.CurrencyResponse.CurrencyRate;
import exchange_rate.downloader.nbp.client.http.converter.json.TableResponse.TableRate;
import exchange_rate.downloader.nbp.exception.unchecked.ConvertResponseException;
import exchange_rate.downloader.nbp.exception.unchecked.DataParseException;
import exchange_rate.downloader.nbp.exception.unchecked.ResponseSyntaxException;
import exchange_rate.dto.ExchangeRate;
import exchange_rate.enums.Currency;

public class JsonConverter implements Converter {

	@Override
	public DataFormat getDataFormat() {
		return DataFormat.JSON;
	}

	@Override
	public ExchangeRate convertCurrencyResponse(String response) {
		Gson gson = new Gson();
		try {
			CurrencyResponse value = gson.fromJson(response, CurrencyResponse.class);

			if (value == null)
				throw new ConvertResponseException("Cannot convert response, response is null. Response: " + response);

			if (value.getRates() == null || value.getTable() == null || value.getCode() == null
					|| value.getCurrency() == null)
				throw new ConvertResponseException(
						"Cannot convert response, response values are null. Response: " + response);

			if (value.getRates().length == 0)
				throw new ConvertResponseException("Cannot convert response, rates is empty. Response: " + response);

			if (value.getRates().length > 1) {
				throw new ConvertResponseException("Cannot convert response, numerous items. Response: " + response);
			}

			CurrencyRate rate = value.getRates()[0];

			if (rate.getNo() == null || rate.getEffectiveDate() == null || rate.getMid() == null) {
				throw new ConvertResponseException(
						"Cannot convert response, rate values are null. Response: " + response);
			}

			Currency currency = Currency.byAlphabeticCode(value.getCode());

			if (currency == null)
				throw new ConvertResponseException(
						"Cannot convert response, cannot find currency by alphabetic code. Response: " + response);

			LocalDate date = LocalDate.parse(rate.getEffectiveDate());

			return new ExchangeRate(date, currency, new BigDecimal(rate.getMid()));
		} catch (JsonSyntaxException e) {
			throw new ResponseSyntaxException("Cannot convert response. Wrong Json syntax. Response: " + response
					+ "gson exception: " + e.toString());
		} catch (DateTimeParseException e) {
			throw new DataParseException("Cannot convert response. Wrong data format. Response: " + response);
		}
	}

	@Override
	public List<ExchangeRate> convertTableResponse(String response) {
		List<ExchangeRate> result = new ArrayList<ExchangeRate>();
		Gson gson = new Gson();
		try {
			TableResponse[] arrayValue = gson.fromJson(response, TableResponse[].class);

			if (arrayValue == null)
				throw new ConvertResponseException("Cannot convert response, response is null. Response: " + response);

			if (arrayValue.length == 0)
				throw new ConvertResponseException(
						"Cannot convert response, response is empty array. Response: " + response);

			if (arrayValue.length > 1)
				throw new ConvertResponseException("Cannot convert response, numerous tables. Response: " + response);

			TableResponse value = arrayValue[0];

			if (value.getRates() == null || value.getEffectiveDate() == null || value.getNo() == null
					|| value.getTable() == null)
				throw new ConvertResponseException(
						"Cannot convert response, response values are null. Response: " + response);

			if (value.getRates().length == 0)
				throw new ConvertResponseException("Cannot convert response, rates is empty. Response: " + response);

			for (int i = 0; i < value.getRates().length; i++) {
				TableRate rate = value.getRates()[i];

				Currency currency = Currency.byAlphabeticCode(rate.getCode());
				if (currency == null)
					continue;

				LocalDate date = LocalDate.parse(value.getEffectiveDate());
				result.add(new ExchangeRate(date, currency, new BigDecimal(rate.getMid())));
			}

			if (result.size() == 0) {
				throw new ConvertResponseException("Cannot convert response, there is not one known currency.");
			}
			return result;
		} catch (JsonSyntaxException e) {
			throw new ResponseSyntaxException("Cannot convert response. Wrong Json syntax. Response: " + response
					+ "gson exception: " + e.toString());
		} catch (DateTimeParseException e) {
			throw new DataParseException("Cannot convert response. Wrong data format. Response: " + response);
		}
	}
}