package exchange_rate.nbp_api_client.database;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.database.exchange_rate.ExchangeRateRepository;
import exchange_rate.nbp_api_client.dto.ExchangeRate;

public class ExchangeRateRepositoryTestSuite {

	private ExchangeRateRepository repository = new ExchangeRateRepository();

	private Date testDate = new Date(1234);
	private Currency testCurrency = Currency.EURO;
	private BigDecimal testRate = new BigDecimal("2.14");

	@Test
	public void test_save() {
		// Given
		ExchangeRate rate = new ExchangeRate(testDate, testCurrency, testRate);
		repository.save(rate);

		// When
		List<ExchangeRate> result = repository.readAllRecords();

		// Then
		assertThat(result).isNotEmpty().anyMatch(e -> e.equals(rate));

		// Clean up
		repository.delete(rate);
	}

	@Test
	public void test_read_byCurrencyAndDate() {
		// Given
		ExchangeRate rate = new ExchangeRate(testDate, testCurrency, testRate);
		repository.save(rate);

		// When
		rate = repository.read(testCurrency, testDate);

		// Then
		assertThat(rate).isNotNull();
		assertThat(rate.getRate().doubleValue()).isEqualTo(testRate.doubleValue());
		assertThat(rate.getCurrency()).isEqualTo(testCurrency);
		assertThat(DateUtils.isSameDay(rate.getDate(), testDate)).isTrue();

		// Clean up
		repository.delete(rate);
	}

	@Test
	public void test_delete() {
		// Given
		ExchangeRate rate = new ExchangeRate(testDate, testCurrency, testRate);
		repository.save(rate);

		// When
		repository.delete(rate);
		rate = repository.read(testCurrency, testDate);

		// Then
		assertThat(rate).isNull();
	}

	@Test
	public void test_update() {
		// Given
		ExchangeRate rate = new ExchangeRate(testDate, testCurrency, testRate);
		repository.save(rate);
		BigDecimal updatedRate = testRate.multiply(new BigDecimal(100));

		// When
		rate.setRate(updatedRate);
		repository.update(rate);
		rate = repository.read(testCurrency, testDate);

		// Then
		assertThat(rate).isNotNull();
		assertThat(rate.getRate().doubleValue()).isEqualTo(updatedRate.doubleValue());
		assertThat(DateUtils.isSameDay(rate.getDate(), testDate)).isTrue();
		assertThat(rate.getCurrency()).isEqualTo(testCurrency);

		// Clean up
		repository.delete(rate);
	}
}