package unit.training.exchange_rate.downloader.http.converter.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.Test;

import training.enums.Currency;
import training.exchange_rate.downloader.http.converter.Converter;
import training.exchange_rate.downloader.http.converter.json.JsonConverter;
import training.exchange_rate.dto.ExchangeRate;
import training.exchange_rate.exception.unchecked.ConvertResponseException;
import training.exchange_rate.exception.unchecked.DataParseException;
import training.exchange_rate.exception.unchecked.ResponseSyntaxException;

public class JsonCurrencyConverterTest {

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
	
	private final String NUMEROUS_RESPONSE = 
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
							"\"effectiveDate\":\"" + testDate + "\"," +
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
		ExchangeRate resultRate = converter.convertCurrencyResponse(CORRECT_RESPONSE);

		// Then
		assertThat(resultRate.getCurrency()).isEqualTo(Currency.EURO);
		assertThat(resultRate.getDate()).isEqualTo(testDate);
		assertThat(resultRate.getRate()).isEqualTo(testRate);
	}

	@Test
	public void test_convert_numerousRates() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(ConvertResponseException.class,
				() -> converter.convertCurrencyResponse(NUMEROUS_RESPONSE));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response, numerous items.");
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
		Exception exception = assertThrows(ConvertResponseException.class,
				() -> converter.convertCurrencyResponse(NO_RATE_RESPONSE));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response, response values are null.");
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
	public void test_convert_nullRateResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(ConvertResponseException.class,
				() -> converter.convertCurrencyResponse(NULL_RATE_RESPONSE));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response, rate values are null.");
	}

	@Test
	public void test_convert_emptyResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(ConvertResponseException.class, () -> converter.convertCurrencyResponse(""));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response, response is null.");
	}

	@Test
	public void test_convert_wrongResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(ResponseSyntaxException.class,
				() -> converter.convertCurrencyResponse("testWRONGstring"));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response. Wrong Json syntax.");
	}

	@Test
	public void test_convert_wrongDateFormat() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception exception = assertThrows(DataParseException.class,
				() -> converter.convertCurrencyResponse(WRONG_DATE_FORMAT_RESPONSE));

		// Then
		assertThat(exception).hasMessageContaining("Cannot convert response. Wrong data format.");
	}
}
