package exchange_rate.downloader.nbp.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.Test;

import exchange_rate.database.Database;
import exchange_rate.downloader.nbp.exception.checked.NotFoundException;
import exchange_rate.dto.ExchangeRate;
import exchange_rate.enums.Currency;

public class ExchangeRateRepositoryTestSuite {

	private ExchangeRateRepository repository = new ExchangeRateRepository(Database.getInstance());

	@Test
	public void test_saveRead() {
		// Given
		LocalDate date = LocalDate.of(1, 1, 1);
		Currency currency = Currency.EURO;
		BigDecimal rate = new BigDecimal("2.14");
		ExchangeRate exceptedResult = new ExchangeRate(date, currency, rate);
		repository.save(exceptedResult);

		// When
		ExchangeRate result = repository.get(currency, date);

		// Then
		assertThat(exceptedResult).isNotNull();
		assertThat(exceptedResult).isEqualTo(result);

		// Clean up
		repository.delete(exceptedResult);
	}

	@Test
	public void test_delete() {
		// Given
		LocalDate date = LocalDate.of(1, 1, 1);
		Currency currency = Currency.EURO;
		BigDecimal rate = new BigDecimal("2.14");
		ExchangeRate exchangeRate = new ExchangeRate(date, currency, rate);
		repository.save(exchangeRate);

		// When
		repository.delete(exchangeRate);

		// Then
		assertThrows(NotFoundException.class, () -> repository.get(currency, date));
	}

	@Test
	public void test_update() {
		// Given
		LocalDate date = LocalDate.of(1, 1, 1);
		Currency currency = Currency.EURO;
		BigDecimal rate = new BigDecimal("2.14");
		ExchangeRate exchangeRate = new ExchangeRate(date, currency, rate);
		repository.save(exchangeRate);
		BigDecimal updatedRate = rate.multiply(new BigDecimal(100));

		// When
		exchangeRate.setRate(updatedRate);
		repository.update(exchangeRate);
		exchangeRate = repository.get(currency, date);

		// Then
		assertThat(exchangeRate).isNotNull();
		assertThat(exchangeRate.getRate().doubleValue()).isEqualTo(updatedRate.doubleValue());
		assertThat(exchangeRate.getDate()).isEqualTo(date);
		assertThat(exchangeRate.getCurrency()).isEqualTo(currency);

		// Clean up
		repository.delete(exchangeRate);
	}
}