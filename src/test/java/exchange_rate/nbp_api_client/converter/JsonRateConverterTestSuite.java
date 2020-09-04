package exchange_rate.nbp_api_client.converter;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import org.junit.Assert;
import org.junit.Test;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.converter.json.JsonConverter;
import exchange_rate.nbp_api_client.dto.ExchangeRate;

public class JsonRateConverterTestSuite {

	private final String CORRECT_RESPONSE = "{\"table\":\"A\",\"currency\":\"euro\",\"code\":\"EUR\",\"rates\":[{\"no\":\"172/A/NBP/2020\",\"effectiveDate\":\"2020-09-03\",\"mid\":4.4181}]}";
	private final String WRONG_CURRENCY_CODE_RESPONSE = "{\"table\":\"A\",\"currency\":\"euro\",\"code\":\"STH\",\"rates\":[{\"no\":\"172/A/NBP/2020\",\"effectiveDate\":\"2020-09-03\",\"mid\":4.4181}]}";
	private final String NO_RATE_RESPONSE = "{\"table\":\"A\",\"currency\":\"euro\",\"code\":\"EUR\"}";
	private final String EMPTY_RATE_RESPONSE = "{\"table\":\"A\",\"currency\":\"euro\",\"code\":\"EUR\",\"rates\":[]}";

	@Test
	public void test_convert_correct_response() {
		// Given
		Converter converter = new JsonConverter();

		// When
		ExchangeRate exchangeRate = converter.convertResponse(CORRECT_RESPONSE);

		// Then
		Assert.assertEquals(Currency.EURO, exchangeRate.getCurrency());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Assert.assertEquals("2020-09-03", df.format(exchangeRate.getDate()));
		Assert.assertEquals(new BigDecimal("4.4181"), exchangeRate.getRate());
	}

	@Test
	public void test_convert_wrong_currency_code() {
		// Given
		Converter converter = new JsonConverter();

		// When
		ExchangeRate exchangeRate = converter.convertResponse(WRONG_CURRENCY_CODE_RESPONSE);

		// Then
		Assert.assertEquals(null, exchangeRate);
	}

	@Test
	public void test_convert_no_rate_response() {
		// Given
		Converter converter = new JsonConverter();

		// When
		ExchangeRate exchangeRate = converter.convertResponse(NO_RATE_RESPONSE);

		// Then
		Assert.assertEquals(null, exchangeRate);
	}

	@Test
	public void test_convert_empty_rate_response() {
		// Given
		Converter converter = new JsonConverter();

		// When
		ExchangeRate exchangeRate = converter.convertResponse(EMPTY_RATE_RESPONSE);

		// Then
		Assert.assertEquals(null, exchangeRate);
	}

	@Test
	public void test_convert_empty_string() {
		// Given
		Converter converter = new JsonConverter();

		// When
		ExchangeRate exchangeRate = converter.convertResponse("");

		// Then
		Assert.assertEquals(null, exchangeRate);
	}

	@Test
	public void test_convert_wrong_string() {
		// Given
		Converter converter = new JsonConverter();

		// When
		ExchangeRate exchangeRate = converter.convertResponse("testWRONGstring");

		// Then
		Assert.assertEquals(null, exchangeRate);

	}
}
