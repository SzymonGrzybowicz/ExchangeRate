package exchange_rate.nbp_api_client.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import org.junit.Assert;
import org.junit.Test;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.converter.json.JsonConverter;
import exchange_rate.nbp_api_client.dto.ExchangeRate;
import exchange_rate.nbp_api_client.exception.unchecked.ConvertResponseException;
import exchange_rate.nbp_api_client.exception.unchecked.DateParseException;
import exchange_rate.nbp_api_client.exception.unchecked.ResponseSyntaxException;

public class JsonRateConverterTestSuite {

	private final String CORRECT_RESPONSE = "{\"table\":\"A\",\"currency\":\"euro\",\"code\":\"EUR\",\"rates\":[{\"no\":\"172/A/NBP/2020\",\"effectiveDate\":\"2020-09-03\",\"mid\":4.4181}]}";
	private final String WRONG_CURRENCY_CODE_RESPONSE = "{\"table\":\"A\",\"currency\":\"euro\",\"code\":\"STH\",\"rates\":[{\"no\":\"172/A/NBP/2020\",\"effectiveDate\":\"2020-09-03\",\"mid\":4.4181}]}";
	private final String NO_RATE_RESPONSE = "{\"table\":\"A\",\"currency\":\"euro\",\"code\":\"EUR\"}";
	private final String EMPTY_RATE_RESPONSE = "{\"table\":\"A\",\"currency\":\"euro\",\"code\":\"EUR\",\"rates\":[]}";
	private final String WRONG_DATE_FORMAT_RESPONSE = "{\"table\":\"A\",\"currency\":\"euro\",\"code\":\"EUR\",\"rates\":[{\"no\":\"172/A/NBP/2020\",\"effectiveDate\":\"2020 09 03\",\"mid\":4.4181}]}";

	@Test
	public void test_convert_correctResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		ExchangeRate exchangeRate = converter.convert(CORRECT_RESPONSE);

		// Then
		Assert.assertEquals(Currency.EURO, exchangeRate.getCurrency());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Assert.assertEquals("2020-09-03", df.format(exchangeRate.getDate()));
		Assert.assertEquals(new BigDecimal("4.4181"), exchangeRate.getRate());
	}

	@Test
	public void test_convert_wrongCurrencyCode() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(ConvertResponseException.class,
				() -> converter.convert(WRONG_CURRENCY_CODE_RESPONSE));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response, cannot find currency by alphabetic code.");
	}

	@Test
	public void test_convert_noRateResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(ConvertResponseException.class, () -> converter.convert(NO_RATE_RESPONSE));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response, rates is null.");
	}

	@Test
	public void test_convert_emptyRateResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(ConvertResponseException.class,
				() -> converter.convert(EMPTY_RATE_RESPONSE));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response, rates is empty.");
	}

	@Test
	public void test_convert_emptyResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(ConvertResponseException.class, () -> converter.convert(""));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response, value is null.");
	}

	@Test
	public void test_convert_wrongResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(ResponseSyntaxException.class, () -> converter.convert("testWRONGstring"));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response. Wrong Json syntax.");
	}

	@Test
	public void test_convert_wrongDateFormat() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(DateParseException.class,
				() -> converter.convert(WRONG_DATE_FORMAT_RESPONSE));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response. Wrong data format.");
	}
}
