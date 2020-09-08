package exchange_rate.nbp_api_client.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.converter.json.JsonConverter;
import exchange_rate.nbp_api_client.dto.ExchangeRate;
import exchange_rate.nbp_api_client.exception.unchecked.ConvertResponseException;
import exchange_rate.nbp_api_client.exception.unchecked.DateParseException;
import exchange_rate.nbp_api_client.exception.unchecked.ResponseSyntaxException;

public class JsonRateConverterTestSuite {

	private final Date testDate = new Date(1234);
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private final Currency testCurrency = Currency.EURO;
	private final BigDecimal testRate = new BigDecimal("4.4181");
	// @formatter:off
	private final String CORRECT_RESPONSE = 
		"{" +
			"\"table\":\"A\"," +
			"\"currency\":\"euro\"," +
			"\"code\":\"" + testCurrency.getAlphabeticCode() + "\"," +
				"\"rates\":" + 
				"[" +
					"{" +
						"\"no\":\"172/A/NBP/2020\"," +
						"\"effectiveDate\":\"" + dateFormat.format(testDate) + "\"," +
						"\"mid\":" + testRate + 
					"}" +
				"]" +
		"}";
	
	private final String WRONG_CURRENCY_CODE_RESPONSE = 
		"{" +
			"\"table\":\"A\"," +
			"\"currency\":\"euro\"," +
			"\"code\":\"" + "STH" + "\"," + //wrong
				"\"rates\":" +
				"[" +
					"{" +
						"\"no\":\"172/A/NBP/2020\"," +
						"\"effectiveDate\":\"" + dateFormat.format(testDate) + "\"," +
						"\"mid\":" + testRate + 
					"}" +
				"]" +
		"}";
	
	private final String NO_RATE_RESPONSE = 
		"{" +
			"\"table\":\"A\"," +
			"\"currency\":\"euro\"," +
			"\"code\":\"" + testCurrency.getAlphabeticCode() + "\"" +
		"}";
	
	private final String EMPTY_RATE_RESPONSE = 
		"{" +
			"\"table\":\"A\"," +
			"\"currency\":\"euro\"," +
			"\"code\":\"" + testCurrency.getAlphabeticCode() + "\"," +
			"\"rates\":[]" + //wrong 
		"}";
	
	private final String WRONG_DATE_FORMAT_RESPONSE = 
		"{" +
			"\"table\":\"A\"," +
			"\"currency\":\"euro\"," +
			"\"code\":\"" + testCurrency.getAlphabeticCode() + "\"," +
				"\"rates\":" +
				"[" +
					"{" +
						"\"no\":\"172/A/NBP/2020\"," +
						"\"effectiveDate\":\"2020 09 01\"," + //wrong
						"\"mid\":" + testRate + 
				"}" +
			"]" +
		"}";
	// @formatter:on

	@Test
	public void test_convert_correctResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		List<ExchangeRate> resultList = converter.convertCurrencyResponse(CORRECT_RESPONSE);

		// Then
		assertThat(resultList).hasSize(1);
		ExchangeRate resultRate = resultList.get(0);
		assertThat(resultRate.getCurrency()).isEqualTo(Currency.EURO);
		assertThat(DateUtils.isSameDay(resultRate.getDate(), testDate)).isTrue();
		assertThat(resultRate.getRate()).isEqualTo(testRate);
	}

	@Test
	public void test_convert_wrongCurrencyCode() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(ConvertResponseException.class,
				() -> converter.convertCurrencyResponse(WRONG_CURRENCY_CODE_RESPONSE));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response, cannot find currency by alphabetic code.");
	}

	@Test
	public void test_convert_noRateResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(ConvertResponseException.class, () -> converter.convertCurrencyResponse(NO_RATE_RESPONSE));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response, rates is null.");
	}

	@Test
	public void test_convert_emptyRateResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(ConvertResponseException.class,
				() -> converter.convertCurrencyResponse(EMPTY_RATE_RESPONSE));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response, rates is empty.");
	}

	@Test
	public void test_convert_emptyResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(ConvertResponseException.class, () -> converter.convertCurrencyResponse(""));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response, value is null.");
	}

	@Test
	public void test_convert_wrongResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(ResponseSyntaxException.class, () -> converter.convertCurrencyResponse("testWRONGstring"));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response. Wrong Json syntax.");
	}

	@Test
	public void test_convert_wrongDateFormat() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(DateParseException.class,
				() -> converter.convertCurrencyResponse(WRONG_DATE_FORMAT_RESPONSE));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response. Wrong data format.");
	}
}
