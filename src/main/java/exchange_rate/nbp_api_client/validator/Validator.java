package exchange_rate.nbp_api_client.validator;

import com.sun.istack.NotNull;

import exchange_rate.nbp_api_client.dto.ExchangeRate;
import exchange_rate.nbp_api_client.exception.NbpWebApiException;
import exchange_rate.web_client.WebResponse;

public interface Validator {

	void validateWebResponse(WebResponse webResponse) throws NbpWebApiException;

	void validateExchangeRate(ExchangeRate exchangeRate, @NotNull WebResponse webResponse) throws NbpWebApiException;

	boolean isNoDataStatus(WebResponse webResponse);

}
