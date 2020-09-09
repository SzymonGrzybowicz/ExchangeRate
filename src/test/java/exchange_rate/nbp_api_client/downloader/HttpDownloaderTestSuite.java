package exchange_rate.nbp_api_client.downloader;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.downloader.http.NbpHttpDownloader;
import exchange_rate.nbp_api_client.downloader.http.converter.Converter;
import exchange_rate.nbp_api_client.downloader.http.converter.Converter.DataFormat;
import exchange_rate.nbp_api_client.exception.checked.NotFoundException;
import exchange_rate.nbp_api_client.exception.unchecked.BadRequestException;

public class HttpDownloaderTestSuite {

	@Mock
	private Converter converterMock;

	@Before
	public void init() {
		MockitoAnnotations.openMocks(this);
		when(converterMock.getDataFormat()).thenReturn(DataFormat.JSON);
	}

	@Test
	public void test_getActual_responseCode200() throws IOException {
		// Given
		String testBody = "testBody";
		Response responseMock = mockResponse(200, testBody);
		OkHttpClient clientMock = mockHttpClient(responseMock);

		NbpHttpDownloader downloader = new NbpHttpDownloader();
		downloader.setConverter(converterMock);
		downloader.setHttpClient(clientMock);

		// When
		downloader.get(Currency.EURO);

		// Then
		verify(converterMock).convertCurrencyResponse(testBody);
	}

	@Test
	public void test_getActual_responseCode418() throws IOException {
		// Given
		Response responseMock = mockResponse(418, "");
		OkHttpClient clientMock = mockHttpClient(responseMock);

		NbpHttpDownloader downloader = new NbpHttpDownloader();
		downloader.setHttpClient(clientMock);

		// When && Then
		assertThrows(BadRequestException.class, () -> downloader.get(Currency.EURO));
	}

	@Test
	public void test_getActual_responseCode404() throws IOException {
		// Given
		Response responseMock = mockResponse(404, "");
		OkHttpClient clientMock = mockHttpClient(responseMock);

		NbpHttpDownloader downloader = new NbpHttpDownloader();
		downloader.setHttpClient(clientMock);

		// When && Then
		assertThrows(NotFoundException.class, () -> downloader.get(Currency.EURO));
	}

	@Test
	public void test_getForDate_responseCode200() throws IOException {
		// Given
		String testBody = "testBody";
		Response responseMock = mockResponse(200, testBody);
		OkHttpClient clientMock = mockHttpClient(responseMock);

		NbpHttpDownloader downloader = new NbpHttpDownloader();
		downloader.setConverter(converterMock);
		downloader.setHttpClient(clientMock);

		// When
		downloader.get(Currency.EURO, new Date());

		// Then
		verify(converterMock).convertCurrencyResponse(testBody);
	}

	@Test
	public void test_getForDate_responseCode418() throws IOException {
		// Given
		Response responseMock = mockResponse(418, "");
		OkHttpClient clientMock = mockHttpClient(responseMock);

		NbpHttpDownloader downloader = new NbpHttpDownloader();
		downloader.setHttpClient(clientMock);

		// When && Then
		assertThrows(BadRequestException.class, () -> downloader.get(Currency.EURO, new Date()));
	}

	@Test
	public void test_getForDate_responseCode404() throws IOException {
		// Given
		Response responseMock = mockResponse(404, "");
		OkHttpClient clientMock = mockHttpClient(responseMock);

		NbpHttpDownloader downloader = new NbpHttpDownloader();
		downloader.setHttpClient(clientMock);

		// When && Then
		assertThrows(NotFoundException.class, () -> downloader.get(Currency.EURO, new Date()));
	}

	private Response mockResponse(int code, String body) {
		return new Response.Builder().request(new Request.Builder().url("http://url.com").build())
				.protocol(Protocol.HTTP_1_1).code(code).message("")
				.body(ResponseBody.create(MediaType.parse("application/json"), body)).build();
	}

	private OkHttpClient mockHttpClient(Response response) throws IOException {
		Call callMock = Mockito.mock(Call.class);
		when(callMock.execute()).thenReturn(response);

		OkHttpClient httpClientMock = Mockito.mock(OkHttpClient.class);
		when(httpClientMock.newCall(any())).thenReturn(callMock);

		return httpClientMock;
	}
}