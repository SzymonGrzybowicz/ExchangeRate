package training.exchange_rate.downloader.http.converter.json;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import training.enums.Currency;
import training.exchange_rate.downloader.http.converter.Converter;
import training.exchange_rate.downloader.http.converter.json.CurrencyResponse.CurrencyRate;
import training.exchange_rate.dto.ExchangeRate;
import training.exchange_rate.exception.unchecked.ConvertResponseException;
import training.exchange_rate.exception.unchecked.DataParseException;
import training.exchange_rate.exception.unchecked.ResponseSyntaxException;

@Component
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

			if (value.getRates() == null || value.getTable() == null || value.getCode() == null)
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

}