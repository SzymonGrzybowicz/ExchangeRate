package exchange_rate.nbp_api_client;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import exchange_rate.nbp_api_client.converter.RateConverter;
import exchange_rate.nbp_api_client.dto.ExchangeRate;
import exchange_rate.nbp_api_client.exception.NbpWebApiException;
import exchange_rate.nbp_api_client.strategy.NbpClientStrategy;
import exchange_rate.web_client.WebClient;
import exchange_rate.web_client.WebResponse;

public class NbpClientTestSuite {

	private NbpClientStrategy strategyMock = Mockito.mock(NbpClientStrategy.class);
	private RateConverter converterMock = Mockito.mock(RateConverter.class);
	private WebClient webClientMock = Mockito.mock(WebClient.class);
	private String testUrl = "test url";

	@Before
	public void init() {
		Mockito.when(strategyMock.getActualCurrencyRateUrl(Mockito.any())).thenReturn(testUrl);
		Mockito.when(strategyMock.getCurrencyRateUrl(Mockito.any(), Mockito.any())).thenReturn(testUrl);
		Mockito.when(strategyMock.getRateConverter()).thenReturn(converterMock);
		Mockito.when(strategyMock.getWebClient()).thenReturn(webClientMock);
	}

	@Test
	public void test_request_actual_exchange_rate_correct_response() throws NbpWebApiException {
		// Given
		String responseBody = "test body";
		int responseCode = 200;
		Date date = new Date();
		BigDecimal rate = new BigDecimal(2.15);
		NbpClient client = prepareNbpClient(responseBody, responseCode, date, rate,
				new ExchangeRate(date, Currency.EURO, rate));

		// When
		ExchangeRate result = client.requestActualExchangeRate(Currency.EURO);

		// Then
		Assert.assertEquals(rate, result.getRate());
		Assert.assertEquals(date, result.getDate());
		Assert.assertEquals(Currency.EURO, result.getCurrency());
	}

	@Test
	public void test_request_actual_exchange_rate_wrong_response_code() throws NbpWebApiException {
		// Given
		String responseBody = "test body";
		int responseCode = 418;
		Date date = new Date();
		BigDecimal rate = new BigDecimal(2.15);
		NbpClient client = prepareNbpClient(responseBody, responseCode, date, rate,
				new ExchangeRate(date, Currency.EURO, rate));

		// When
		NbpWebApiException exception = Assert.assertThrows(NbpWebApiException.class,
				() -> client.requestActualExchangeRate(Currency.EURO));

		// Then
		Assert.assertEquals(responseCode, exception.getResponseCode());
		Assert.assertEquals(responseBody, exception.getResponseBody());
		Assert.assertTrue(exception.getMessage().contains("Response code not equal to 200!"));
	}

	@Test
	public void test_request_actual_exchange_rate_body_is_null() throws NbpWebApiException {
		// Given
		String responseBody = null;
		int responseCode = 200;
		Date date = new Date();
		BigDecimal rate = new BigDecimal(2.15);
		NbpClient client = prepareNbpClient(responseBody, responseCode, date, rate,
				new ExchangeRate(date, Currency.EURO, rate));

		// When
		NbpWebApiException exception = Assert.assertThrows(NbpWebApiException.class,
				() -> client.requestActualExchangeRate(Currency.EURO));

		// Then
		Assert.assertEquals(responseCode, exception.getResponseCode());
		Assert.assertEquals(responseBody, exception.getResponseBody());
		Assert.assertTrue(exception.getMessage().contains("Response body is null!"));
	}

	@Test
	public void test_request_actual_exchange_rate_result_is_null() throws NbpWebApiException {
		// Given
		String responseBody = "test body";
		int responseCode = 200;
		Date date = new Date();
		BigDecimal rate = new BigDecimal(2.15);
		NbpClient client = prepareNbpClient(responseBody, responseCode, date, rate, null);

		// When
		NbpWebApiException exception = Assert.assertThrows(NbpWebApiException.class,
				() -> client.requestActualExchangeRate(Currency.EURO));

		// Then
		Assert.assertEquals(responseCode, exception.getResponseCode());
		Assert.assertEquals(responseBody, exception.getResponseBody());
		Assert.assertTrue(exception.getMessage().contains("Wrong response body format!"));
	}

	@Test
	public void test_request_exchange_rate_incorect_date() {
		// Given
		String responseBody = "404 NotFound - Not Found - Brak danych";
		int responseCode = 404;
		Date date = new Date();
		BigDecimal rate = new BigDecimal(2.15);
		NbpClient client = prepareNbpClient(responseBody, responseCode, date, rate,
				new ExchangeRate(date, Currency.EURO, rate));

		// When
		NbpWebApiException exception = Assert.assertThrows(NbpWebApiException.class,
				() -> client.requestExchangeRate(Currency.EURO, date));

		// Then
		Assert.assertEquals(responseCode, exception.getResponseCode());
		Assert.assertEquals(responseBody, exception.getResponseBody());
		Assert.assertTrue(exception.getMessage().contains("Rate not found for that date!"));
	}

	@Test
	public void test_request_exchange_rate_correct_on_first() throws NbpWebApiException {
		// Given
		String responseBody = "test body";
		int responseCode = 200;
		Date date = new Date();
		BigDecimal rate = new BigDecimal(2.15);
		NbpClient client = prepareNbpClient(responseBody, responseCode, date, rate,
				new ExchangeRate(date, Currency.EURO, rate));

		// When
		ExchangeRate result = client.requestExchangeRate(Currency.EURO, date);

		// Then
		Assert.assertEquals(rate, result.getRate());
		Assert.assertEquals(date, result.getDate());
		Assert.assertEquals(Currency.EURO, result.getCurrency());
	}

	@Test
	public void test_request_exchange_rate_correct_on_third() throws NbpWebApiException {
		// Given
		LocalDate now = LocalDate.now();
		LocalDate minusThreeDay = now.minusDays(3);

		String url = "Correct on third url";
		String correctResponseBody = "Correct body";

		String responseBody = "404 NotFound - Not Found - Brak danych";
		int responseCode = 404;
		Date date = Date.from(now.atStartOfDay(ZoneId.systemDefault()).toInstant());
		BigDecimal rate = new BigDecimal(2.15);
		NbpClient client = prepareNbpClient(responseBody, responseCode, date, rate,
				new ExchangeRate(date, Currency.EURO, rate));

		Mockito.when(strategyMock.getCurrencyRateUrl(Mockito.any(),
				Mockito.eq(Date.from(minusThreeDay.atStartOfDay(ZoneId.systemDefault()).toInstant())))).thenReturn(url);
		WebResponse webResponse = new WebResponse(correctResponseBody, 200);
		Mockito.when(webClientMock.request(testUrl)).thenReturn(webResponse);
		Mockito.when(converterMock.convertResponse(webResponse.getBody())).thenReturn(new ExchangeRate(
				Date.from(minusThreeDay.atStartOfDay(ZoneId.systemDefault()).toInstant()), Currency.EURO, rate));

		// When
		ExchangeRate result = client.requestExchangeRate(Currency.EURO, date);

		// Then
		Assert.assertEquals(rate, result.getRate());
		Assert.assertEquals(Date.from(minusThreeDay.atStartOfDay(ZoneId.systemDefault()).toInstant()),
				result.getDate());
		Assert.assertEquals(Currency.EURO, result.getCurrency());
	}

	private NbpClient prepareNbpClient(String responseBody, int responseCode, Date date, BigDecimal rate,
			ExchangeRate exchangeRate) {

		WebResponse webResponse = new WebResponse(responseBody, responseCode);
		Mockito.when(webClientMock.request(testUrl)).thenReturn(webResponse);
		Mockito.when(converterMock.convertResponse(webResponse.getBody())).thenReturn(exchangeRate);
		return new NbpClient(strategyMock);
	}

}