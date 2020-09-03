package exchange_rate.nbp_api_client.converter;

import exchange_rate.nbp_api_client.dto.ExchangeRate;

public interface RateConverter {
	ExchangeRate convertResponse(String response);
}
