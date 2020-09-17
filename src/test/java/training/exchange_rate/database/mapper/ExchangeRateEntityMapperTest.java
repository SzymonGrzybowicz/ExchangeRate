package training.exchange_rate.database.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.Test;

import training.enums.Currency;
import training.exchange_rate.database.entity.ExchangeRateEntity;
import training.exchange_rate.dto.ExchangeRate;

public class ExchangeRateEntityMapperTest {

	@Test
	public void test_map_entity() {
		// Given
		BigDecimal rate = new BigDecimal("123.123");
		Currency currency = Currency.AMERICAN_DOLAR;
		LocalDate date = LocalDate.MAX;
		ExchangeRateEntity entity = new ExchangeRateEntity(rate, date, currency);

		ExchangeRateEntityMapper mapper = new ExchangeRateEntityMapper();

		// When
		ExchangeRate result = mapper.map(entity);

		// Then
		assertThat(result.getCurrency()).isEqualTo(currency);
		assertThat(result.getDate()).isEqualTo(date);
		assertThat(result.getRate()).isEqualTo(rate);
	}

	@Test
	public void test_map_country() {
		BigDecimal rate = new BigDecimal("123.123");
		Currency currency = Currency.AMERICAN_DOLAR;
		LocalDate date = LocalDate.MAX;
		ExchangeRate exchangeRate = new ExchangeRate(date, currency, rate);

		ExchangeRateEntityMapper mapper = new ExchangeRateEntityMapper();

		// When
		ExchangeRateEntity result = mapper.map(exchangeRate);

		// Then
		assertThat(result.getCurrency()).isEqualTo(currency);
		assertThat(result.getDate()).isEqualTo(date);
		assertThat(result.getRate()).isEqualTo(rate);
	}
}
