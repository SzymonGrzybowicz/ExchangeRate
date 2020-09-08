package exchange_rate.nbp_api_client.database.exchange_rate.mapper;

import exchange_rate.nbp_api_client.database.exchange_rate.entity.ExchangeRateEntity;
import exchange_rate.nbp_api_client.dto.ExchangeRate;

public class ExchangeRateEntityMapper {

	public ExchangeRate map(ExchangeRateEntity entity) {
		return new ExchangeRate(entity.getDate(), entity.getCurrency(), entity.getRate());
	}

	public ExchangeRateEntity map(ExchangeRate rate) {
		return new ExchangeRateEntity(rate.getRate(), rate.getDate(), rate.getCurrency());
	}
}
