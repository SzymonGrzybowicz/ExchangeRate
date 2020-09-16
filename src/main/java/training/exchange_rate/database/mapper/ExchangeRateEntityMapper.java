package training.exchange_rate.database.mapper;

import org.springframework.stereotype.Component;

import training.exchange_rate.database.entity.ExchangeRateEntity;
import training.exchange_rate.dto.ExchangeRate;

@Component
public class ExchangeRateEntityMapper {

	public ExchangeRate map(ExchangeRateEntity entity) {
		return new ExchangeRate(entity.getDate(), entity.getCurrency(), entity.getRate());
	}

	public ExchangeRateEntity map(ExchangeRate rate) {
		return new ExchangeRateEntity(rate.getRate(), rate.getDate(), rate.getCurrency());
	}
}
