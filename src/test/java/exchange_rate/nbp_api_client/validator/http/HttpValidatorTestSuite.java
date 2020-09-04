package exchange_rate.nbp_api_client.validator.http;

import java.math.BigDecimal;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.dto.ExchangeRate;
import exchange_rate.nbp_api_client.exception.NbpWebApiException;
import exchange_rate.nbp_api_client.validator.Validator;
import exchange_rate.web_client.WebResponse;

public class HttpValidatorTestSuite {

	@Test
	public void test_validateWebResponse_correctData() {
		// Given
		String responseBody = "test body";
		Integer responseCode = 200;
		WebResponse webResponse = new WebResponse(responseBody, responseCode);
		Validator validator = new HttpValidator();

		// When
		NbpWebApiException exception = null;
		try {
			validator.validateWebResponse(webResponse);
		} catch (NbpWebApiException e) {
			exception = e;
		}

		// Then
		Assert.assertNull(exception);
	}

	@Test
	public void test_validateWebResponse_incorrectResponseCode() {
		// Given
		String responseBody = "test body";
		Integer responseCode = 418;
		WebResponse webResponse = new WebResponse(responseBody, responseCode);
		Validator validator = new HttpValidator();

		// When
		NbpWebApiException exception = Assert.assertThrows(NbpWebApiException.class,
				() -> validator.validateWebResponse(webResponse));

		// Then
		Assert.assertEquals(responseCode, exception.getResponseCode());
		Assert.assertEquals(responseBody, exception.getResponseBody());
		Assert.assertTrue(exception.getMessage().contains("Response code not equal to 200!"));
	}

	@Test
	public void test_validateWebResponse_bodyEqualsNull() {
		// Given
		String responseBody = null;
		Integer responseCode = 200;
		WebResponse webResponse = new WebResponse(responseBody, responseCode);
		Validator validator = new HttpValidator();

		// When
		NbpWebApiException exception = Assert.assertThrows(NbpWebApiException.class,
				() -> validator.validateWebResponse(webResponse));

		// Then
		Assert.assertEquals(responseCode, exception.getResponseCode());
		Assert.assertEquals(responseBody, exception.getResponseBody());
		Assert.assertTrue(exception.getMessage().contains("Response body is null!"));
	}

	@Test
	public void test_validateWebResponse_responseEqualsNull() {
		// Given
		Validator validator = new HttpValidator();

		// When
		NbpWebApiException exception = Assert.assertThrows(NbpWebApiException.class,
				() -> validator.validateWebResponse(null));

		// Then
		Assert.assertTrue(exception.getMessage().contains("Web response has wrong format!"));
	}

	@Test
	public void test_validateExchangeRate_correctData() {
		// Given
		Validator validator = new HttpValidator();
		String responseBody = "test body";
		Integer responseCode = 200;
		WebResponse webResponse = new WebResponse(responseBody, responseCode);
		ExchangeRate exchangeRate = new ExchangeRate(new Date(), Currency.EURO, new BigDecimal(2.15));

		// When
		NbpWebApiException exception = null;
		try {
			validator.validateExchangeRate(exchangeRate, webResponse);
		} catch (NbpWebApiException e) {
			exception = e;
		}

		// Then
		Assert.assertNull(exception);
	}

	@Test
	public void test_validateExchangeRate_rateEqualsNull() {
		// Given
		Validator validator = new HttpValidator();
		String responseBody = "test body";
		Integer responseCode = 200;
		WebResponse webResponse = new WebResponse(responseBody, responseCode);

		// When
		NbpWebApiException exception = Assert.assertThrows(NbpWebApiException.class,
				() -> validator.validateExchangeRate(null, webResponse));

		// Then
		Assert.assertTrue(exception.getMessage().contains("Wrong response body format!"));
	}

	@Test
	public void test_isNoDataStatus_correct() {
		// Given
		Validator validator = new HttpValidator();
		String responseBody = "404 NotFound - Not Found - Brak danych";
		Integer responseCode = 404;
		WebResponse webResponse = new WebResponse(responseBody, responseCode);

		// When
		boolean result = validator.isNoDataStatus(webResponse);

		// Then
		Assert.assertTrue(result);
	}

	@Test
	public void test_isNoDataStatus_incorrectStatus() {
		// Given
		Validator validator = new HttpValidator();
		String responseBody = "404 NotFound - Not Found - Brak danych";
		Integer responseCode = 200;
		WebResponse webResponse = new WebResponse(responseBody, responseCode);

		// When
		boolean result = validator.isNoDataStatus(webResponse);

		// Then
		Assert.assertFalse(result);
	}

	@Test
	public void test_isNoDataStatus_incorrectBody() {
		// Given
		Validator validator = new HttpValidator();
		String responseBody = "test body";
		Integer responseCode = 404;
		WebResponse webResponse = new WebResponse(responseBody, responseCode);

		// When
		boolean result = validator.isNoDataStatus(webResponse);

		// Then
		Assert.assertFalse(result);
	}

}
