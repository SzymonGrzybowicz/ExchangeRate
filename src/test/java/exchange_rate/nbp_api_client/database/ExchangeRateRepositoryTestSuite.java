package exchange_rate.nbp_api_client.database;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.database.exchange_rate.ExchangeRateRepository;
import exchange_rate.nbp_api_client.dto.ExchangeRate;

public class ExchangeRateRepositoryTestSuite {

	private ExchangeRateRepository repository = new ExchangeRateRepository();

	@Test
	public void test_saveRead() {
		// Given
		Date exceptedDate = new Date(new Random().nextInt());
		Currency exceptedCurrency = Currency.EURO;
		BigDecimal exceptedRate = new BigDecimal("2.14");
		ExchangeRate exceptedResult = new ExchangeRate(exceptedDate, exceptedCurrency, exceptedRate);
		repository.save(exceptedResult);

		// When
		ExchangeRate result = repository.getOrNull(exceptedCurrency, exceptedDate);

		// Then
		assertThat(exceptedResult).isNotNull();
		assertThat(exceptedResult).isEqualTo(result);

		// Clean up
		repository.delete(exceptedResult);
	}

	@Test
	public void test_delete() {
		// Given
		Date testDate = new Date(new Random().nextInt());
		Currency testCurrency = Currency.EURO;
		BigDecimal testRate = new BigDecimal("2.14");
		ExchangeRate rate = new ExchangeRate(testDate, testCurrency, testRate);
		repository.save(rate);

		// When
		repository.delete(rate);
		rate = repository.getOrNull(testCurrency, testDate);

		// Then
		assertThat(rate).isNull();
	}

	@Test
	public void test_update() {
		// Given
		Date testDate = new Date(new Random().nextInt());
		Currency testCurrency = Currency.EURO;
		BigDecimal testRate = new BigDecimal("2.14");
		ExchangeRate rate = new ExchangeRate(testDate, testCurrency, testRate);
		repository.save(rate);
		BigDecimal updatedRate = testRate.multiply(new BigDecimal(100));

		// When
		rate.setRate(updatedRate);
		repository.update(rate);
		rate = repository.getOrNull(testCurrency, testDate);

		// Then
		assertThat(rate).isNotNull();
		assertThat(rate.getRate().doubleValue()).isEqualTo(updatedRate.doubleValue());
		assertThat(DateUtils.isSameDay(rate.getDate(), testDate)).isTrue();
		assertThat(rate.getCurrency()).isEqualTo(testCurrency);

		// Clean up
		repository.delete(rate);
	}
}