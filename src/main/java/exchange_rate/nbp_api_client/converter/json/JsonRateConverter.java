package exchange_rate.nbp_api_client.converter.json;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.converter.RateConverter;
import exchange_rate.nbp_api_client.converter.json.RateResponse.Rate;
import exchange_rate.nbp_api_client.dto.ExchangeRate;

public class JsonRateConverter implements RateConverter {

	@Override
	public ExchangeRate convertResponse(String response) {
		Gson gson = new Gson();
		try {
			RateResponse value = gson.fromJson(response, RateResponse.class);
			if (value == null || value.getRates() == null || value.getRates().length == 0) {
				return null;
			}

			Rate rate = value.getRates()[0];
			Currency currency = Currency.byAlphabeticCode(value.getCode());

			if (rate == null || currency == null) {
				return null;
			}

			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(rate.getEffectiveDate());
			return new ExchangeRate(date, currency, new BigDecimal(rate.getMid()));
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			return null;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
}