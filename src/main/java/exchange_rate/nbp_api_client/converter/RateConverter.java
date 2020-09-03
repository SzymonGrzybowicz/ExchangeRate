package exchange_rate.nbp_api_client.converter;

import com.sun.istack.Nullable;

import exchange_rate.nbp_api_client.dto.ExchangeRate;

public interface RateConverter {

	@Nullable
	ExchangeRate convertResponse(String response);

}
