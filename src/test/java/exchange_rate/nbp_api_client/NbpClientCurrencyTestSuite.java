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
import java.time.LocalDate;

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
		LocalDate date = LocalDate.of(1, 1, 1);
		Currency currency = Currency.EURO;
		BigDecimal rate = new BigDecimal("321.321");
		ExchangeRate exceptedResult = new ExchangeRate(date, currency, rate);

		when(cacheMock.get(eq(currency), any())).thenReturn(exceptedResult);

		NbpClient client = new NbpClient(downloaderMock);
		client.setCache(cacheMock);

		// When
		ExchangeRate result = client.requestActualExchangeRate(currency);

		// Then
		verify(downloaderMock, never()).get(any());
		assertThat(result).isEqualTo(exceptedResult);
	}

	@Test
	public void test_requestActualExchangeRate_noObjectInCache() {
		// Given
		LocalDate date = LocalDate.of(1, 1, 1);
		Currency currency = Currency.EURO;
		BigDecimal rate = new BigDecimal("321.321");
		ExchangeRate exceptedResult = new ExchangeRate(date, currency, rate);

		when(cacheMock.get(eq(currency), any())).thenThrow(NotFoundException.class);
		when(downloaderMock.get(eq(currency))).thenReturn(exceptedResult);

		NbpClient client = new NbpClient(downloaderMock);
		client.setCache(cacheMock);

		// When
		ExchangeRate result = client.requestActualExchangeRate(currency);

		// Then
		assertThat(result).isEqualTo(exceptedResult);
	}

	@Test
	public void test_requestActualExchangeRate_withoutCache() {
		// Given
		LocalDate date = LocalDate.of(1, 1, 1);
		Currency currency = Currency.EURO;
		BigDecimal rate = new BigDecimal("321.321");
		ExchangeRate exceptedResult = new ExchangeRate(date, currency, rate);

		when(downloaderMock.get(eq(currency))).thenReturn(exceptedResult);

		NbpClient client = new NbpClient(downloaderMock);

		// When
		ExchangeRate result = client.requestActualExchangeRate(currency);

		// Then
		assertThat(result).isEqualTo(exceptedResult);
	}

	@Test
	public void test_requestExchangeRate_objectFromCache() {
		// Given
		LocalDate date = LocalDate.of(1, 1, 1);
		Currency currency = Currency.EURO;
		BigDecimal rate = new BigDecimal("321.321");
		ExchangeRate exceptedResult = new ExchangeRate(date, currency, rate);

		when(cacheMock.get(eq(currency), eq(date))).thenReturn(exceptedResult);

		NbpClient client = new NbpClient(downloaderMock);
		client.setCache(cacheMock);

		// When
		ExchangeRate result = client.requestExchangeRate(currency, date);

		// Then
		verify(downloaderMock, never()).get(any(), any());
		assertThat(result).isEqualTo(exceptedResult);
	}

	@Test
	public void test_requestExchangeRate_noObjectInCache() {
		// Given
		LocalDate date = LocalDate.of(1, 1, 1);
		Currency currency = Currency.EURO;
		BigDecimal rate = new BigDecimal("321.321");
		ExchangeRate exceptedResult = new ExchangeRate(date, currency, rate);

		when(cacheMock.get(eq(currency), eq(date))).thenThrow(NotFoundException.class);
		when(downloaderMock.get(eq(currency), eq(date))).thenReturn(exceptedResult);

		NbpClient client = new NbpClient(downloaderMock);
		client.setCache(cacheMock);

		// When
		ExchangeRate result = client.requestExchangeRate(currency, date);

		// Then
		assertThat(result).isEqualTo(exceptedResult);
	}

	@Test
	public void test_requestExchangeRate_withoutCache() {
		// Given
		LocalDate date = LocalDate.of(1, 1, 1);
		Currency currency = Currency.EURO;
		BigDecimal rate = new BigDecimal("321.321");
		ExchangeRate exceptedResult = new ExchangeRate(date, currency, rate);

		when(downloaderMock.get(eq(currency), eq(date))).thenReturn(exceptedResult);

		NbpClient client = new NbpClient(downloaderMock);

		// When
		ExchangeRate result = client.requestExchangeRate(currency, date);

		// Then
		assertThat(result).isEqualTo(exceptedResult);
	}

	@Test
	public void test_requestExchangeRate_fourDayHolidayRequest() throws ParseException {
		// Given
		LocalDate holiday = LocalDate.parse("2020-03-10"); // before that day was three days without rate publication
		LocalDate dayWithRate = LocalDate.parse("2020-03-06");

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
		LocalDate holiday = LocalDate.parse("2020-03-10"); // before that day was four days without rate publication
		LocalDate dayWithRate = LocalDate.parse("2020-03-05");

		Currency currency = Currency.EURO;
		BigDecimal rate = new BigDecimal("321.321");
		ExchangeRate exceptedResult = new ExchangeRate(dayWithRate, currency, rate);

		when(downloaderMock.get(eq(currency), any())).thenThrow(NotFoundException.class);
		when(downloaderMock.get(eq(currency), eq(dayWithRate))).thenReturn(exceptedResult);

		NbpClient client = new NbpClient(downloaderMock);

		// When
		assertThrows(NotFoundException.class, () -> client.requestExchangeRate(currency, holiday));

		// Then
		verify(downloaderMock, times(5)).get(eq(currency), any());
	}

}