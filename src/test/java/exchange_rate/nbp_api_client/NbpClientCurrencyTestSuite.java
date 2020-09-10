package exchange_rate.nbp_api_client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import exchange_rate.nbp_api_client.cache.Cache;
import exchange_rate.nbp_api_client.downloader.Downloader;
import exchange_rate.nbp_api_client.dto.ExchangeRate;
import exchange_rate.nbp_api_client.exception.checked.NotFoundException;

public class NbpClientCurrencyTestSuite {

	@Mock
	private Downloader downloaderMock;

	@Mock
	private Cache cacheMock;

	@Before
	public void init() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void test_requestActualExchangeRate_objectFromCache() {
		// Given
		Date exceptedDate = new Date(1234);
		Currency exceptedCurrency = Currency.EURO;
		BigDecimal exceptedRate = new BigDecimal("321.321");
		ExchangeRate exceptedResult = new ExchangeRate(exceptedDate, exceptedCurrency, exceptedRate);

		when(cacheMock.get(eq(exceptedCurrency), any())).thenReturn(exceptedResult);

		NbpClient client = new NbpClient(downloaderMock);
		client.setCache(cacheMock);

		// When
		ExchangeRate result = client.requestActualExchangeRate(exceptedCurrency);

		// Then
		verify(downloaderMock, never()).get(any());
		assertThat(result).isEqualTo(exceptedResult);
	}

	@Test
	public void test_requestActualExchangeRate_noObjectInCache() {
		// Given
		Date exceptedDate = new Date(1234);
		Currency exceptedCurrency = Currency.EURO;
		BigDecimal exceptedRate = new BigDecimal("321.321");
		ExchangeRate exceptedResult = new ExchangeRate(exceptedDate, exceptedCurrency, exceptedRate);

		when(cacheMock.get(eq(exceptedCurrency), any())).thenThrow(NotFoundException.class);
		when(downloaderMock.get(eq(exceptedCurrency))).thenReturn(exceptedResult);

		NbpClient client = new NbpClient(downloaderMock);
		client.setCache(cacheMock);

		// When
		ExchangeRate result = client.requestActualExchangeRate(exceptedCurrency);

		// Then
		assertThat(result).isEqualTo(exceptedResult);
	}

	@Test
	public void test_requestActualExchangeRate_withoutCache() {
		// Given
		Date exceptedDate = new Date(1234);
		Currency exceptedCurrency = Currency.EURO;
		BigDecimal exceptedRate = new BigDecimal("321.321");
		ExchangeRate exceptedResult = new ExchangeRate(exceptedDate, exceptedCurrency, exceptedRate);

		when(downloaderMock.get(eq(exceptedCurrency))).thenReturn(exceptedResult);

		NbpClient client = new NbpClient(downloaderMock);

		// When
		ExchangeRate result = client.requestActualExchangeRate(exceptedCurrency);

		// Then
		assertThat(result).isEqualTo(exceptedResult);
	}

	@Test
	public void test_requestExchangeRate_objectFromCache() {
		// Given
		Date exceptedDate = new Date(1234);
		Currency exceptedCurrency = Currency.EURO;
		BigDecimal exceptedRate = new BigDecimal("321.321");
		ExchangeRate exceptedResult = new ExchangeRate(exceptedDate, exceptedCurrency, exceptedRate);

		when(cacheMock.get(eq(exceptedCurrency), eq(exceptedDate))).thenReturn(exceptedResult);

		NbpClient client = new NbpClient(downloaderMock);
		client.setCache(cacheMock);

		// When
		ExchangeRate result = client.requestExchangeRate(exceptedCurrency, exceptedDate);

		// Then
		verify(downloaderMock, never()).get(any(), any());
		assertThat(result).isEqualTo(exceptedResult);
	}

	@Test
	public void test_requestExchangeRate_noObjectInCache() {
		// Given
		Date exceptedDate = new Date(1234);
		Currency exceptedCurrency = Currency.EURO;
		BigDecimal exceptedRate = new BigDecimal("321.321");
		ExchangeRate exceptedResult = new ExchangeRate(exceptedDate, exceptedCurrency, exceptedRate);

		when(cacheMock.get(eq(exceptedCurrency), eq(exceptedDate))).thenThrow(NotFoundException.class);
		when(downloaderMock.get(eq(exceptedCurrency), eq(exceptedDate))).thenReturn(exceptedResult);

		NbpClient client = new NbpClient(downloaderMock);
		client.setCache(cacheMock);

		// When
		ExchangeRate result = client.requestExchangeRate(exceptedCurrency, exceptedDate);

		// Then
		assertThat(result).isEqualTo(exceptedResult);
	}

	@Test
	public void test_requestExchangeRate_withoutCache() {
		// Given
		Date exceptedDate = new Date(1234);
		Currency exceptedCurrency = Currency.EURO;
		BigDecimal exceptedRate = new BigDecimal("321.321");
		ExchangeRate exceptedResult = new ExchangeRate(exceptedDate, exceptedCurrency, exceptedRate);

		when(downloaderMock.get(eq(exceptedCurrency), eq(exceptedDate))).thenReturn(exceptedResult);

		NbpClient client = new NbpClient(downloaderMock);

		// When
		ExchangeRate result = client.requestExchangeRate(exceptedCurrency, exceptedDate);

		// Then
		assertThat(result).isEqualTo(exceptedResult);
	}

	@Test
	public void test_requestExchangeRate_fourDayHolidayRequest() throws ParseException {
		// Given
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date holiday = df.parse("2020-03-10"); // before that day was three days without rate publication
		Date dayWithRate = df.parse("2020-03-06");

		Currency currency = Currency.EURO;
		BigDecimal rate = new BigDecimal("321.321");
		ExchangeRate exceptedResult = new ExchangeRate(dayWithRate, currency, rate);

		when(downloaderMock.get(eq(currency), any())).thenThrow(NotFoundException.class);
		when(downloaderMock.get(eq(currency), eq(dayWithRate))).thenReturn(exceptedResult);

		NbpClient client = new NbpClient(downloaderMock);

		// When
		ExchangeRate result = client.requestExchangeRate(currency, holiday);

		// Then
		verify(downloaderMock, times(5)).get(eq(currency), any());
		assertThat(result).isEqualTo(exceptedResult);
	}

	@Test
	public void test_requestExchangeRate_fiveDayHolidayRequest() throws ParseException {
		// Given
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date holiday = df.parse("2020-03-10"); // before that day was four days without rate publication
		Date dayWithRate = df.parse("2020-03-05");

		Currency exceptedCurrency = Currency.EURO;
		BigDecimal exceptedRate = new BigDecimal("321.321");
		ExchangeRate exceptedResult = new ExchangeRate(dayWithRate, exceptedCurrency, exceptedRate);

		when(downloaderMock.get(eq(exceptedCurrency), any())).thenThrow(NotFoundException.class);
		when(downloaderMock.get(eq(exceptedCurrency), eq(dayWithRate))).thenReturn(exceptedResult);

		NbpClient client = new NbpClient(downloaderMock);

		// When
		assertThrows(NotFoundException.class, () -> client.requestExchangeRate(exceptedCurrency, holiday));

		// Then
		verify(downloaderMock, times(5)).get(eq(exceptedCurrency), any());
	}

}