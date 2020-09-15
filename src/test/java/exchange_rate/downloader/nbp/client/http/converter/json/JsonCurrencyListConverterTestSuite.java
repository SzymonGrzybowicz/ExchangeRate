package exchange_rate.downloader.nbp.client.http.converter.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.Test;

import enums.Currency;
import exchange_rate.downloader.http.converter.Converter;
import exchange_rate.downloader.http.converter.json.JsonConverter;
import exchange_rate.dto.ExchangeRate;
import exchange_rate.exception.unchecked.ConvertResponseException;
import exchange_rate.exception.unchecked.DataParseException;
import exchange_rate.exception.unchecked.ResponseSyntaxException;

public class JsonCurrencyListConverterTestSuite {

	private final LocalDate testDate = LocalDate.of(1, 1, 1);
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
							"\"effectiveDate\":\"" + testDate + "\"," +
							"\"mid\":" + testRate + 
						"}," +
						"{" +
							"\"no\":\"172/A/NBP/2020\"," +
							"\"effectiveDate\":\"" + LocalDate.now() + "\"," +
							"\"mid\":2.1234" +
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
						"\"effectiveDate\":\"" + testDate + "\"," +
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
	
	private final String NULL_RATE_RESPONSE = 
		"{" +
			"\"table\":\"A\"," +
			"\"currency\":\"euro\"," +
			"\"code\":\"" + testCurrency.getAlphabeticCode() + "\"," +
				"\"rates\":" + 
				"[" +
					"{" +
						"\"no\":null," +
						"\"effectiveDate\":null," +
						"\"mid\":null" + 
					"}" +
				"]" +
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
	public void test_correct_response() {
		// Given
		Converter converter = new JsonConverter();

		// When
		List<ExchangeRate> result = converter.convertCurrencyListResponse(CORRECT_RESPONSE);

		// Then
		assertThat(result).hasSize(2);
		ExchangeRate exchangeRate = result.get(0);
		assertThat(exchangeRate.getCurrency()).isEqualTo(Currency.EURO);
		assertThat(exchangeRate.getDate()).isEqualTo(testDate);
		assertThat(exchangeRate.getRate()).isEqualTo(testRate);
	}

	@Test
	public void test_convert_wrongCurrencyCode() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(ConvertResponseException.class,
				() -> converter.convertCurrencyListResponse(WRONG_CURRENCY_CODE_RESPONSE));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response, cannot find currency by alphabetic code.");
	}

	@Test
	public void test_convert_noRateResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(ConvertResponseException.class,
				() -> converter.convertCurrencyListResponse(NO_RATE_RESPONSE));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response, response values are null.");
	}

	@Test
	public void test_convert_emptyRateResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(ConvertResponseException.class,
				() -> converter.convertCurrencyListResponse(EMPTY_RATE_RESPONSE));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response, rates is empty.");
	}

	@Test
	public void test_convert_nullRateResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(ConvertResponseException.class,
				() -> converter.convertCurrencyListResponse(NULL_RATE_RESPONSE));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response, rate values are null.");
	}

	@Test
	public void test_convert_emptyResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(ConvertResponseException.class,
				() -> converter.convertCurrencyListResponse(""));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response, response is null.");
	}

	@Test
	public void test_convert_wrongResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(ResponseSyntaxException.class,
				() -> converter.convertCurrencyListResponse("testWRONGstring"));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response. Wrong Json syntax.");
	}

	@Test
	public void test_convert_wrongDateFormat() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(DataParseException.class,
				() -> converter.convertCurrencyListResponse(WRONG_DATE_FORMAT_RESPONSE));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response. Wrong data format.");
	}
}
