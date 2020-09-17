package unit.training.exchange_rate.downloader.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.LocalDate;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
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

import training.enums.Currency;
import training.exchange_rate.downloader.NbpDownloader;
import training.exchange_rate.downloader.http.HttpNbpDownloader;
import training.exchange_rate.downloader.http.converter.Converter;
import training.exchange_rate.downloader.http.converter.Converter.DataFormat;
import training.exchange_rate.exception.checked.NotFoundException;
import training.exchange_rate.exception.unchecked.BadRequestException;
import training.exchange_rate.exception.unchecked.ConnectionException;
import training.exchange_rate.exception.unchecked.DataFormatException;

public class HttpNbpDownloaderTest {

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
		String body = "testBody";
		Response responseMock = mockResponse(200, body);
		OkHttpClient clientMock = mockHttpClient(responseMock);

		NbpDownloader downloader = new HttpNbpDownloader(clientMock, converterMock);

		// When
		downloader.get(Currency.EURO);

		// Then
		verify(converterMock).convertCurrencyResponse(body);
	}

	@Test
	public void test_getActual_responseCode418() throws IOException {
		// Given
		Response responseMock = mockResponse(418, "");
		OkHttpClient clientMock = mockHttpClient(responseMock);

		HttpNbpDownloader downloader = new HttpNbpDownloader(clientMock, converterMock);

		// When && Then
		assertThrows(BadRequestException.class, () -> downloader.get(Currency.EURO));
		verify(converterMock, never()).convertCurrencyResponse(Mockito.anyString());
	}

	@Test
	public void test_getActual_responseCode404() throws IOException {
		// Given
		Response responseMock = mockResponse(404, "");
		OkHttpClient clientMock = mockHttpClient(responseMock);

		HttpNbpDownloader downloader = new HttpNbpDownloader(clientMock, converterMock);

		// When && Then
		assertThrows(NotFoundException.class, () -> downloader.get(Currency.EURO));
		verify(converterMock, never()).convertCurrencyResponse(Mockito.anyString());
	}

	@Test
	public void test_getActual_IOException() throws IOException {
		// Given
		Call callMock = Mockito.mock(Call.class);
		when(callMock.execute()).thenThrow(IOException.class);

		OkHttpClient clientMock = Mockito.mock(OkHttpClient.class);
		when(clientMock.newCall(any())).thenReturn(callMock);

		HttpNbpDownloader downloader = new HttpNbpDownloader(clientMock, converterMock);

		// When && Then
		assertThrows(ConnectionException.class, () -> downloader.get(Currency.EURO));
		verify(converterMock, never()).convertCurrencyResponse(Mockito.anyString());
	}

	@Test
	public void test_getForDate_responseCode200() throws IOException {
		// Given
		String body = "testBody";
		Response responseMock = mockResponse(200, body);
		OkHttpClient clientMock = mockHttpClient(responseMock);

		HttpNbpDownloader downloader = new HttpNbpDownloader(clientMock, converterMock);

		// When
		downloader.get(Currency.EURO, LocalDate.MAX);

		// Then
		verify(converterMock).convertCurrencyResponse(body);
	}

	@Test
	public void test_getForDate_responseCode418() throws IOException {
		// Given
		Response responseMock = mockResponse(418, "");
		OkHttpClient clientMock = mockHttpClient(responseMock);

		HttpNbpDownloader downloader = new HttpNbpDownloader(clientMock, converterMock);

		// When && Then
		assertThrows(BadRequestException.class, () -> downloader.get(Currency.EURO, LocalDate.MAX));
		verify(converterMock, never()).convertCurrencyResponse(Mockito.anyString());
	}

	@Test
	public void test_getForDate_responseCode404() throws IOException {
		// Given
		Response responseMock = mockResponse(404, "");
		OkHttpClient clientMock = mockHttpClient(responseMock);

		HttpNbpDownloader downloader = new HttpNbpDownloader(clientMock, converterMock);

		// When && Then
		assertThrows(NotFoundException.class, () -> downloader.get(Currency.EURO, LocalDate.MAX));
		verify(converterMock, never()).convertCurrencyResponse(Mockito.anyString());
	}

	@Test
	public void test_getForDate_IOException() throws IOException {
		// Given
		Call callMock = Mockito.mock(Call.class);
		when(callMock.execute()).thenThrow(IOException.class);

		OkHttpClient clientMock = Mockito.mock(OkHttpClient.class);
		when(clientMock.newCall(any())).thenReturn(callMock);

		HttpNbpDownloader downloader = new HttpNbpDownloader(clientMock, converterMock);

		// When && Then
		assertThrows(ConnectionException.class, () -> downloader.get(Currency.EURO, LocalDate.MAX));
		verify(converterMock, never()).convertCurrencyResponse(Mockito.anyString());
	}

	@Test
	public void test_buildUrl_jsonFormat() throws IOException {
		// Given
		when(converterMock.getDataFormat()).thenReturn(DataFormat.JSON);

		Response response = mockResponse(200, "");
		OkHttpClient clientMock = mockHttpClient(response);
		HttpNbpDownloader downloader = new HttpNbpDownloader(clientMock, converterMock);

		// When
		downloader.get(Currency.EURO);

		// Then
		ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
		verify(clientMock).newCall(requestCaptor.capture());

		assertThat(requestCaptor.getValue().urlString()).contains("?format=json");

	}

	@Test
	public void test_buildUrl_xmlFormat() throws IOException {
		// Given
		when(converterMock.getDataFormat()).thenReturn(DataFormat.XML);

		Response response = mockResponse(200, "");
		OkHttpClient clientMock = mockHttpClient(response);
		HttpNbpDownloader downloader = new HttpNbpDownloader(clientMock, converterMock);

		// When
		downloader.get(Currency.EURO);

		// Then
		ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
		verify(clientMock).newCall(requestCaptor.capture());

		assertThat(requestCaptor.getValue().urlString()).contains("?format=xml");
	}

	@Test
	public void test_buildUrl_wrongFormat() throws IOException {
		// Given
		when(converterMock.getDataFormat()).thenReturn(DataFormat.FILE);

		Response response = mockResponse(200, "");
		OkHttpClient clientMock = mockHttpClient(response);
		HttpNbpDownloader downloader = new HttpNbpDownloader(clientMock, converterMock);

		// When
		Exception e = assertThrows(DataFormatException.class, () -> downloader.get(Currency.EURO));

		// Then
		assertThat(e).hasMessageContaining("is not supported in Http downloader");
		verify(clientMock, never()).newCall(any());
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