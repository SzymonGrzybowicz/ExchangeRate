package exchange_rate.nbp_api_client.converter.json;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.downloader.http.converter.Converter;
import exchange_rate.nbp_api_client.downloader.http.converter.json.JsonConverter;
import exchange_rate.nbp_api_client.dto.ExchangeRate;
import exchange_rate.nbp_api_client.exception.unchecked.ConvertResponseException;
import exchange_rate.nbp_api_client.exception.unchecked.DataParseException;
import exchange_rate.nbp_api_client.exception.unchecked.ResponseSyntaxException;

public class JsonTableConverterTestSuite {

	private final Date testDate = new Date(1234);
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private final Currency testCurrency = Currency.EURO;
	private final BigDecimal testRate = new BigDecimal("4.4181");
	//@formatter:off
		private final String CORRECT_RESPONSE = 
				"[" +
				   "{" +
				      "\"table\":\"A\"," +
				      "\"no\":\"174/A/NBP/2020\"," +
				      "\"effectiveDate\":\"" + dateFormat.format(testDate) + "\"," +
				      "\"rates\":" +
			    	  "[" +
						"{" +
						    "\"currency\":\"dolar amerykañski\"," +
						    "\"code\":\"USD\"," +
						    "\"mid\":3.7666" +
						"}," +
						"{" +
					            "\"currency\":\"" + testCurrency.name() + "\"," +
					            "\"code\":\"" + testCurrency.getAlphabeticCode() + "\"," +
					            "\"mid\":" + testRate +
			            "}," +
						"{" +
				            "\"currency\":\"frank szwajcarski\"," +
				            "\"code\":\"CHF\"," +
				            "\"mid\":4.1253" +
			            "}," +
			            "{" +
				            "\"currency\":\"funt szterling\"," +
				            "\"code\":\"GBP\"," +
				            "\"mid\":4.9688" +
				         "}" +
				       "]" +
				   "}" +
				 "]"; 
		
		private final String NUMEROUS_TABLE_RESPONSE = 
				"[" +
				   "{" +
				      "\"table\":\"A\"," +
				      "\"no\":\"174/A/NBP/2020\"," +
				      "\"effectiveDate\":\"" + dateFormat.format(testDate) + "\"," +
				      "\"rates\":" +
			    	  "[" +
						"{" +
						    "\"currency\":\"dolar amerykañski\"," +
						    "\"code\":\"USD\"," +
						    "\"mid\":3.7666" +
						"}" +
				       "]" +
				   "}," +
				   "{" +
				      "\"table\":\"A\"," +
				      "\"no\":\"174/A/NBP/2020\"," +
				      "\"effectiveDate\":\"" + dateFormat.format(testDate) + "\"," +
				      "\"rates\":" +
			    	  "[" +
						"{" +
						    "\"currency\":\"dolar amerykañski\"," +
						    "\"code\":\"USD\"," +
						    "\"mid\":3.7666" +
						"}" +
				       "]" +
				   "}" +
				 "]"; 
		
		private final String NO_RATES_RESPONSE = 
				"[" +
					"{" +
					      "\"table\":\"A\"," +
					      "\"no\":\"174/A/NBP/2020\"," +
					      "\"effectiveDate\":\"" + dateFormat.format(testDate) + "\"" +
				     "}" +
				 "]"; 
		
		private final String EMPTY_RATES_RESPONSE = 
				"[" +
					"{" +
					      "\"table\":\"A\"," +
					      "\"no\":\"174/A/NBP/2020\"," +
					      "\"effectiveDate\":\"" + dateFormat.format(testDate) + "\"," +
					      "\"rates\":[]" + //wrong
				     "}" +
				 "]"; 
		
		private final String NULL_VALUES_RESPONSE = 
				"[" +
				   "{" +
				      "\"table\":null," +
				      "\"no\":null," +
				      "\"effectiveDate\":null," +
				      "\"rates\":" +
			    	  "[" +
						"{" +
						    "\"currency\":\"dolar amerykañski\"," +
						    "\"code\":\"USD\"," +
						    "\"mid\":3.7666" +
						"}" +
 				       "]" +
				   "}" +
				 "]"; 
		
		private final String WRONG_CURRENCY_CODES_RESPONSE = 
				"[" +
				   "{" +
				      "\"table\":\"A\"," +
				      "\"no\":\"174/A/NBP/2020\"," +
				      "\"effectiveDate\":\"" + dateFormat.format(testDate) + "\"," +
				      "\"rates\":" +
			    	  "[" +
						"{" +
						    "\"currency\":\"dolar amerykañski\"," +
						    "\"code\":\"ABBA\"," + //wrong
						    "\"mid\":3.7666" +
						"}," +
						"{" +
					            "\"currency\":\"" + testCurrency.name() + "\"," +
					            "\"code\":\"CCC\"," + //wrong
					            "\"mid\":" + testRate +
			            "}," +
						"{" +
				            "\"currency\":\"frank szwajcarski\"," +
				            "\"code\":\"BBB\"," + //wrong
				            "\"mid\":4.1253" +
			            "}" +
				       "]" +
				   "}" +
				 "]"; 
		
		private final String WRONG_DATE_FORMAT_RESPONSE = 
				"[" +
				   "{" +
				      "\"table\":\"A\"," +
				      "\"no\":\"174/A/NBP/2020\"," +
				      "\"effectiveDate\":\"2020 02 03\"," + //wrong
				      "\"rates\":" +
			    	  "[" +
						"{" +
						    "\"currency\":\"dolar amerykañski\"," +
						    "\"code\":\"USD\"," +
						    "\"mid\":3.7666" +
						"}" +
				       "]" +
				   "}" +
				 "]"; 
		
		//@formatter:on

	@Test
	public void test_convert_correctResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		List<ExchangeRate> result = converter.convertTableResponse(CORRECT_RESPONSE);

		// Then
		assertThat(result).hasSize(3);
		assertThat(result).allMatch(e -> DateUtils.isSameDay(e.getDate(), testDate));

		List<ExchangeRate> testCurrencyRates = result.stream().filter(e -> e.getCurrency().equals(testCurrency))
				.collect(Collectors.toList());
		assertThat(testCurrencyRates).hasSize(1);

		ExchangeRate resultTestCurrency = testCurrencyRates.get(0);
		assertThat(resultTestCurrency.getRate()).isEqualTo(testRate);
	}

	@Test
	public void test_convert_emptyResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception e = assertThrows(ConvertResponseException.class, () -> converter.convertTableResponse(""));

		// Then
		assertThat(e).hasMessageContaining("Cannot convert response, response is null.");
	}

	@Test
	public void test_convert_emptyArrayResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception e = assertThrows(ConvertResponseException.class, () -> converter.convertTableResponse("[]"));

		// Then
		assertThat(e).hasMessageContaining("Cannot convert response, response is empty array.");
	}

	@Test
	public void test_convert_numerousTable() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception e = assertThrows(ConvertResponseException.class,
				() -> converter.convertTableResponse(NUMEROUS_TABLE_RESPONSE));

		// Then
		assertThat(e).hasMessageContaining("Cannot convert response, numerous tables.");
	}

	@Test
	public void test_convert_noRatesResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception e = assertThrows(ConvertResponseException.class,
				() -> converter.convertTableResponse(NO_RATES_RESPONSE));

		// Then
		assertThat(e).hasMessageContaining("Cannot convert response, response values are null.");
	}

	@Test
	public void test_convert_emptyRatesResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception e = assertThrows(ConvertResponseException.class,
				() -> converter.convertTableResponse(EMPTY_RATES_RESPONSE));

		// Then
		assertThat(e).hasMessageContaining("Cannot convert response, rates is empty.");
	}

	@Test
	public void test_convert_nullValuesResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception e = assertThrows(ConvertResponseException.class,
				() -> converter.convertTableResponse(NULL_VALUES_RESPONSE));

		// Then
		assertThat(e).hasMessageContaining("Cannot convert response, response values are null.");
	}

	@Test
	public void test_convert_wrongCurrencyCodesResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception e = assertThrows(ConvertResponseException.class,
				() -> converter.convertTableResponse(WRONG_CURRENCY_CODES_RESPONSE));

		// Then
		assertThat(e).hasMessageContaining("Cannot convert response, there is not one known currency.");
	}

	@Test
	public void test_convert_wrongDateFormatResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception e = assertThrows(DataParseException.class,
				() -> converter.convertTableResponse(WRONG_DATE_FORMAT_RESPONSE));

		// Then
		assertThat(e).hasMessageContaining("Cannot convert response. Wrong data format.");
	}

	@Test
	public void test_convert_wrongStringResponse() {
		// Given
		Converter converter = new JsonConverter();

		// When
		Exception e = assertThrows(ResponseSyntaxException.class,
				() -> converter.convertTableResponse("someWRONGstring"));

		// Then
		assertThat(e).hasMessageContaining("Cannot convert response. Wrong Json syntax.");
	}

}
