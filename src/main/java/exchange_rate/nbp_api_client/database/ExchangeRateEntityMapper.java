package exchange_rate.nbp_api_client.database;

import exchange_rate.nbp_api_client.database.entity.ExchangeRateEntity;
import exchange_rate.nbp_api_client.dto.ExchangeRate;

class ExchangeRateEntityMapper {

	ExchangeRate map(ExchangeRateEntity entity) {
		return new ExchangeRate(entity.getDate(), entity.getCurrency(), entity.getRate());
	}

	ExchangeRateEntity map(ExchangeRate rate) {
		return new ExchangeRateEntity(rate.getRate(), rate.getDate(), rate.getCurrency());
	}
}
