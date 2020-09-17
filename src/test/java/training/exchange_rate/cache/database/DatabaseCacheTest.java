package training.exchange_rate.cache.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import training.enums.Currency;
import training.exchange_rate.cache.Cache;
import training.exchange_rate.dto.ExchangeRate;
import training.exchange_rate.exception.unchecked.BadRequestException;
import training.exchange_rate.repository.ExchangeRateRepository;

public class DatabaseCacheTest {

	@Mock
	private ExchangeRateRepository repositoryMock;

	private Cache cache;

	@Before
	public void init() {
		MockitoAnnotations.openMocks(this);
		cache = new DatabaseCache(repositoryMock);
	}

	@Test
	public void test_get() {
		// Given
		ExchangeRate expectedRate = new ExchangeRate(LocalDate.MIN, Currency.AMERICAN_DOLAR,
				new BigDecimal("1234.1234"));
		when(repositoryMock.get(any(), any())).thenReturn(expectedRate);

		// When
		ExchangeRate result = cache.get(Currency.AMERICAN_DOLAR, LocalDate.MAX);

		// Then
		assertThat(result).isEqualTo(expectedRate);
	}

	@Test
	public void test_save() {
		// Given
		ExchangeRate expectedRate = new ExchangeRate(LocalDate.MIN, Currency.AMERICAN_DOLAR,
				new BigDecimal("1234.1234"));
		doNothing().when(repositoryMock).save(any());

		// When
		cache.saveOrUpdateIfExists(expectedRate);

		// Then
		verify(repositoryMock).save(expectedRate);
		verify(repositoryMock, never()).update(Mockito.any());
	}

	@Test
	public void test_update() {
		// Given
		ExchangeRate expectedRate = new ExchangeRate(LocalDate.MIN, Currency.AMERICAN_DOLAR,
				new BigDecimal("1234.1234"));
		doThrow(BadRequestException.class).when(repositoryMock).save(any());

		// When
		cache.saveOrUpdateIfExists(expectedRate);

		// Then
		verify(repositoryMock).save(expectedRate);
		verify(repositoryMock).update(expectedRate);
	}

}
