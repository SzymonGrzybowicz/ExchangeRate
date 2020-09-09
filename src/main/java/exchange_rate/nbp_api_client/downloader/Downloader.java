package exchange_rate.nbp_api_client.downloader;

import java.util.Date;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.dto.ExchangeRate;

public interface Downloader {
	ExchangeRate get(Currency currency);

	ExchangeRate get(Currency currency, Date date);
}
