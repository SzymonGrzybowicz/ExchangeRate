package training.exchange_rate.downloader.http.converter;

import training.exchange_rate.dto.ExchangeRate;

public interface Converter {

	DataFormat getDataFormat();

	ExchangeRate convertCurrencyResponse(String response);

	public enum DataFormat {
		JSON, XML, FILE;
	}

}
