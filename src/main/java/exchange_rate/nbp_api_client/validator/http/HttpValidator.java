package exchange_rate.nbp_api_client.validator.http;

import com.sun.istack.NotNull;

import exchange_rate.nbp_api_client.dto.ExchangeRate;
import exchange_rate.nbp_api_client.exception.NbpWebApiException;
import exchange_rate.nbp_api_client.validator.Validator;
import exchange_rate.web_client.WebResponse;

public class HttpValidator implements Validator {

	@Override
	public void validateWebResponse(WebResponse webResponse) throws NbpWebApiException {
		if (webResponse == null)
			throw new NbpWebApiException("Web response has wrong format!", null, null);

		if (webResponse.getCode() != 200)
			throw new NbpWebApiException("Response code not equal to 200!", webResponse.getCode(),
					webResponse.getBody());

		if (webResponse.getBody() == null)
			throw new NbpWebApiException("Response body is null!", webResponse.getCode(), null);
	}

	@Override
	public void validateExchangeRate(ExchangeRate exchangeRate, @NotNull WebResponse webResponse)
			throws NbpWebApiException {
		if (exchangeRate == null)
			throw new NbpWebApiException("Wrong response body format!", webResponse.getCode(), webResponse.getBody());
	}

	@Override
	public boolean isNoDataStatus(WebResponse webResponse) {
		return webResponse.getCode() == 404 && webResponse.getBody().equals("404 NotFound - Not Found - Brak danych");
	}

}
