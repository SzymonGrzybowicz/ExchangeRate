package exchange_rate.database.mapper;

import exchange_rate.database.entity.ExchangeRateEntity;
import exchange_rate.dto.ExchangeRate;

public class ExchangeRateEntityMapper {

	public ExchangeRate map(ExchangeRateEntity entity) {
		return new ExchangeRate(entity.getDate(), entity.getCurrency(), entity.getRate());
	}

	public ExchangeRateEntity map(ExchangeRate rate) {
		return new ExchangeRateEntity(rate.getRate(), rate.getDate(), rate.getCurrency());
	}
}
