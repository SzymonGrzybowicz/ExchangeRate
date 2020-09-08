package exchange_rate.nbp_api_client.downloader;

import java.util.Date;

import exchange_rate.nbp_api_client.Currency;

public interface Path {

	String get(Currency currency, DataFormat dataFormat);

	String get(Currency currency, Date date, DataFormat dataFormat);

	String getAll(DataFormat dataFormat);

	String getAll(Date date, DataFormat dataFormat);

}
