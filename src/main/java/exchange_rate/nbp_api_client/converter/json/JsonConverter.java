package exchange_rate.nbp_api_client.converter.json;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.converter.Converter;
import exchange_rate.nbp_api_client.converter.json.CurrencyResponse.CurrencyRate;
import exchange_rate.nbp_api_client.converter.json.TableResponse.TableRate;
import exchange_rate.nbp_api_client.downloader.DataFormat;
import exchange_rate.nbp_api_client.dto.ExchangeRate;
import exchange_rate.nbp_api_client.exception.unchecked.ConvertResponseException;
import exchange_rate.nbp_api_client.exception.unchecked.DateParseException;
import exchange_rate.nbp_api_client.exception.unchecked.ResponseSyntaxException;

public class JsonConverter implements Converter {

	@Override
	public DataFormat getDataFormat() {
		return DataFormat.JSON;
	}

	@Override
	public List<ExchangeRate> convertCurrencyResponse(String response) {
		List<ExchangeRate> result = new ArrayList();
		Gson gson = new Gson();
		try {
			CurrencyResponse value = gson.fromJson(response, CurrencyResponse.class);

			if (value == null)
				throw new ConvertResponseException("Cannot convert response, response is null. Response: " + response);

			if (value.getRates() == null)
				throw new ConvertResponseException("Cannot convert response, rates is null. Response: " + response);

			if (value.getRates().length == 0)
				throw new ConvertResponseException("Cannot convert response, rates is empty. Response: " + response);

			if (value.getRates().length > 1) {
				throw new ConvertResponseException("Cannot convert response, numerous items.");
			}

			for (int i = 0; i < value.getRates().length; i++) {
				CurrencyRate rate = value.getRates()[i];
				Currency currency = Currency.byAlphabeticCode(value.getCode());

				if (rate == null)
					throw new ConvertResponseException(
							"Cannot convert response, rates[" + i + "] is null. Response: " + response);
				if (currency == null)
					throw new ConvertResponseException(
							"Cannot convert response, cannot find currency by alphabetic code. Response: " + response);

				Date date = new SimpleDateFormat("yyyy-MM-dd").parse(rate.getEffectiveDate());
				result.add(new ExchangeRate(date, currency, new BigDecimal(rate.getMid())));
			}
			return result;
		} catch (JsonSyntaxException e) {
			throw new ResponseSyntaxException("Cannot convert response. Wrong Json syntax. Response: " + response
					+ "gson exception: " + e.toString());
		} catch (ParseException e) {
			throw new DateParseException("Cannot convert response. Wrong data format. Response: " + response);
		}
	}

	@Override
	public List<ExchangeRate> convertTableResponse(String response) {
		List<ExchangeRate> result = new ArrayList();
		Gson gson = new Gson();
		try {
			TableResponse[] arrayValue = gson.fromJson(response, TableResponse[].class);

			if (arrayValue.length == 0) {
				throw new ConvertResponseException("Cannot convert response, response is empty. Response: " + response);
			}

			TableResponse value = arrayValue[0];

			if (value == null)
				throw new ConvertResponseException("Cannot convert response, response is null. Response: " + response);

			if (value.getRates() == null)
				throw new ConvertResponseException("Cannot convert response, rates is null. Response: " + response);

			if (value.getRates().length == 0)
				throw new ConvertResponseException("Cannot convert response, rates is empty. Response: " + response);

			for (int i = 0; i < value.getRates().length; i++) {
				TableRate rate = value.getRates()[i];

				if (rate == null)
					throw new ConvertResponseException(
							"Cannot convert response, rates[" + i + "] is null. Response: " + response);

				Currency currency = Currency.byAlphabeticCode(rate.getCode());
				if (currency == null)
					continue;

				Date date = new SimpleDateFormat("yyyy-MM-dd").parse(value.getEffectiveDate());
				result.add(new ExchangeRate(date, currency, new BigDecimal(rate.getMid())));
			}

			if (result.size() == 0) {
				throw new ConvertResponseException("Cannot convert response, there is not one known currency");
			}
			return result;
		} catch (JsonSyntaxException e) {
			throw new ResponseSyntaxException("Cannot convert response. Wrong Json syntax. Response: " + response
					+ "gson exception: " + e.toString());
		} catch (ParseException e) {
			throw new DateParseException("Cannot convert response. Wrong data format. Response: " + response);
		}
	}
}