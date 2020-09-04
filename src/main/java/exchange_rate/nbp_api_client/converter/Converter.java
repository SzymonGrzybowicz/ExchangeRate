package exchange_rate.nbp_api_client.converter;

import exchange_rate.nbp_api_client.downloader.DataFormat;
import exchange_rate.nbp_api_client.dto.ExchangeRate;

public interface Converter {

	DataFormat getDataFormat();

	ExchangeRate convert(String response);

}
