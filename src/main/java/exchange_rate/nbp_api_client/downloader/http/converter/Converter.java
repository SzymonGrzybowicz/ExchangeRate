package exchange_rate.nbp_api_client.downloader.http.converter;

import java.util.List;

import exchange_rate.nbp_api_client.dto.ExchangeRate;

public interface Converter {

	DataFormat getDataFormat();

	ExchangeRate convertCurrencyResponse(String response);

	List<ExchangeRate> convertTableResponse(String response);

	public enum DataFormat {
		JSON, XML;
	}

}
