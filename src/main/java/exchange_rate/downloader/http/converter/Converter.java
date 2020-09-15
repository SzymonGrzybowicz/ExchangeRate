package exchange_rate.downloader.http.converter;

import java.util.List;

import exchange_rate.dto.ExchangeRate;

public interface Converter {

	DataFormat getDataFormat();

	ExchangeRate convertCurrencyResponse(String response);

	List<ExchangeRate> convertCurrencyListResponse(String response);

	public enum DataFormat {
		JSON, XML;
	}

}
