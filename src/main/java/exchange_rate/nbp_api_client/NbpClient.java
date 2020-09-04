package exchange_rate.nbp_api_client;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import exchange_rate.nbp_api_client.converter.Converter;
import exchange_rate.nbp_api_client.downloader.Downloader;
import exchange_rate.nbp_api_client.downloader.DownloaderResponse;
import exchange_rate.nbp_api_client.downloader.Path;
import exchange_rate.nbp_api_client.dto.ExchangeRate;
import exchange_rate.nbp_api_client.exception.checked.NotFoundException;
import exchange_rate.nbp_api_client.validator.Validator;

public class NbpClient {

	private final Downloader downloader;
	private final Path path;
	private final Converter converter;
	private final Validator validator = new Validator();

	public NbpClient(Downloader downloader, Path path, Converter converter) {
		this.downloader = downloader;
		this.path = path;
		this.converter = converter;
	}

	public ExchangeRate requestActualExchangeRate(Currency currency) {
		DownloaderResponse response = downloader.get(path.get(currency, converter.getDataFormat()));
		validator.validate(response);
		return converter.convert(response.getBody());
	}

	public ExchangeRate requestExchangeRate(Currency currency, Date date) {

		LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

		int counter = 0;
		DownloaderResponse response;
		while (counter < 5) {
			try {
				response = downloader.get(path.get(currency,
						Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()), converter.getDataFormat()));
				validator.validate(response);
				return converter.convert(response.getBody());
			} catch (NotFoundException e) {
				counter++;
				dateTime = dateTime.minusDays(1);
			}
		}

		throw new NotFoundException("Cannot find exchange rate for that date! Make sure that data is correct.");
	}
}
