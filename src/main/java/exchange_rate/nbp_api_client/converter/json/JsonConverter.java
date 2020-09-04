package exchange_rate.nbp_api_client.converter.json;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.converter.Converter;
import exchange_rate.nbp_api_client.converter.json.RateResponse.Rate;
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
	public ExchangeRate convert(String response) {
		Gson gson = new Gson();
		try {
			RateResponse value = gson.fromJson(response, RateResponse.class);

			if (value == null)
				throw new ConvertResponseException("Cannot convert response, value is null. Response: " + response);

			if (value.getRates() == null)
				throw new ConvertResponseException("Cannot convert response, rates is null. Response: " + response);

			if (value.getRates().length == 0)
				throw new ConvertResponseException("Cannot convert response, rates is empty. Response: " + response);

			Rate rate = value.getRates()[0];
			Currency currency = Currency.byAlphabeticCode(value.getCode());

			if (rate == null)
				throw new ConvertResponseException("Cannot convert response, rates[0] is null. Response: " + response);
			if (currency == null)
				throw new ConvertResponseException(
						"Cannot convert response, cannot find currency by alphabetic code. Response: " + response);

			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(rate.getEffectiveDate());
			return new ExchangeRate(date, currency, new BigDecimal(rate.getMid()));
		} catch (JsonSyntaxException e) {
			throw new ResponseSyntaxException();
		} catch (ParseException e) {
			throw new DateParseException();
		}
	}
}