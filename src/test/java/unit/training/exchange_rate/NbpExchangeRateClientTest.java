package unit.training.exchange_rate;

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

import training.enums.Currency;
import training.exchange_rate.NbpExchangeRateClient;
import training.exchange_rate.cache.Cache;
import training.exchange_rate.downloader.NbpDownloader;
import training.exchange_rate.dto.ExchangeRate;
import training.exchange_rate.exception.checked.NotFoundException;

public class NbpExchangeRateClientTest {

	@Mock
	private NbpDownloader downloaderMock;

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

		NbpExchangeRateClient client = new NbpExchangeRateClient(downloaderMock, cacheMock);

		// When
		ExchangeRate result = client.getActualExchangeRate(currency);

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

		NbpExchangeRateClient client = new NbpExchangeRateClient(downloaderMock, cacheMock);

		// When
		ExchangeRate result = client.getActualExchangeRate(currency);

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

		NbpExchangeRateClient client = new NbpExchangeRateClient(downloaderMock, cacheMock);

		// When
		ExchangeRate result = client.getExchangeRate(currency, date);

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

		NbpExchangeRateClient client = new NbpExchangeRateClient(downloaderMock, cacheMock);

		// When
		ExchangeRate result = client.getExchangeRate(currency, date);

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

		when(cacheMock.get(eq(currency), any())).thenThrow(NotFoundException.class);
		when(downloaderMock.get(eq(currency), any())).thenThrow(NotFoundException.class);
		when(downloaderMock.get(eq(currency), eq(dayWithRate))).thenReturn(exceptedResult);

		NbpExchangeRateClient client = new NbpExchangeRateClient(downloaderMock, cacheMock);

		// When
		ExchangeRate result = client.getExchangeRate(currency, holiday);

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

		when(cacheMock.get(eq(currency), any())).thenThrow(NotFoundException.class);
		when(downloaderMock.get(eq(currency), any())).thenThrow(NotFoundException.class);
		when(downloaderMock.get(eq(currency), eq(dayWithRate))).thenReturn(exceptedResult);

		NbpExchangeRateClient client = new NbpExchangeRateClient(downloaderMock, cacheMock);

		// When
		assertThrows(NotFoundException.class, () -> client.getExchangeRate(currency, holiday));

		// Then
		verify(downloaderMock, times(5)).get(eq(currency), any());
	}

}