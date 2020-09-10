package exchange_rate.nbp_api_client.database;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.dto.ExchangeRate;
import exchange_rate.nbp_api_client.exception.checked.NotFoundException;
import exchange_rate.nbp_api_client.repository.ExchangeRateRepository;

public class ExchangeRateRepositoryTestSuite {

	private ExchangeRateRepository repository = new ExchangeRateRepository();

	@Test
	public void test_saveRead() {
		// Given
		Date date = new Date(1111);
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
		Date date = new Date(1111);
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
		Date date = new Date(1111);
		Currency currency = Currency.EURO;
		BigDecimal testRate = new BigDecimal("2.14");
		ExchangeRate exchangeRate = new ExchangeRate(date, currency, testRate);
		repository.save(exchangeRate);
		BigDecimal updatedRate = testRate.multiply(new BigDecimal(100));

		// When
		exchangeRate.setRate(updatedRate);
		repository.update(exchangeRate);
		exchangeRate = repository.get(currency, date);

		// Then
		assertThat(exchangeRate).isNotNull();
		assertThat(exchangeRate.getRate().doubleValue()).isEqualTo(updatedRate.doubleValue());
		assertThat(DateUtils.isSameDay(exchangeRate.getDate(), date)).isTrue();
		assertThat(exchangeRate.getCurrency()).isEqualTo(currency);

		// Clean up
		repository.delete(exchangeRate);
	}
}