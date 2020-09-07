package exchange_rate.nbp_api_client.validator.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import exchange_rate.nbp_api_client.downloader.DownloaderResponse;
import exchange_rate.nbp_api_client.exception.checked.NotFoundException;
import exchange_rate.nbp_api_client.exception.unchecked.BadRequestException;
import exchange_rate.nbp_api_client.validator.Validator;

public class HttpValidatorTestSuite {

	@Test
	public void test_validateWebResponse_correctData() {
		// Given
		String responseBody = "test body";
		DownloaderResponse.Status downloaderStatus = DownloaderResponse.Status.OK;
		DownloaderResponse response = new DownloaderResponse(responseBody, downloaderStatus);
		Validator validator = new Validator();

		// When && Then
		assertThatCode(() -> validator.validate(response)).doesNotThrowAnyException();
	}

	@Test
	public void test_validateWebResponse_notFoundStatus() {
		// Given
		String responseBody = "test body";
		DownloaderResponse.Status downloaderStatus = DownloaderResponse.Status.NOT_FOUND;
		DownloaderResponse response = new DownloaderResponse(responseBody, downloaderStatus);
		Validator validator = new Validator();

		// When
		Exception exception = assertThrows(NotFoundException.class, () -> validator.validate(response));

		// Then
		assertThat(exception).hasMessageContaining("Cannot find exchange rate! Make sure that data is correct.");
	}

	@Test
	public void test_validateWebResponse_badRequest() {
		// Given
		String responseBody = "test body";
		DownloaderResponse.Status downloaderStatus = DownloaderResponse.Status.BAD_REQUEST;
		DownloaderResponse response = new DownloaderResponse(responseBody, downloaderStatus);
		Validator validator = new Validator();

		// When && Then
		assertThrows(BadRequestException.class, () -> validator.validate(response));
	}
}
