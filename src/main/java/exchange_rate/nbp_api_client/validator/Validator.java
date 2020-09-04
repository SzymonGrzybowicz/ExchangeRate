package exchange_rate.nbp_api_client.validator;

import exchange_rate.nbp_api_client.downloader.DownloaderResponse;
import exchange_rate.nbp_api_client.exception.checked.NotFoundException;
import exchange_rate.nbp_api_client.exception.unchecked.BadRequestException;
import exchange_rate.nbp_api_client.exception.unchecked.ConnectionException;

public class Validator {

	@SuppressWarnings("incomplete-switch")
	public void validate(DownloaderResponse downloaderResponse) {
		switch (downloaderResponse.getStatus()) {
		case CONNECTION_PROBLEM:
			throw new ConnectionException("Failed to connect with data source.");
		case NOT_FOUND:
			throw new NotFoundException("Cannot find exchange rate! Make sure that data is correct.");
		case BAD_REQUEST:
			throw new BadRequestException();
		}

	}

}
