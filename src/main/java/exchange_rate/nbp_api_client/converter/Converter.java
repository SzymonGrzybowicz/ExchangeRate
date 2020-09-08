package exchange_rate.nbp_api_client.converter;

import java.util.List;

import exchange_rate.nbp_api_client.downloader.DataFormat;
import exchange_rate.nbp_api_client.dto.ExchangeRate;

public interface Converter {

	DataFormat getDataFormat();

	List<ExchangeRate> convertCurrencyResponse(String response);

	List<ExchangeRate> convertTableResponse(String response);

}
