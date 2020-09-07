package exchange_rate.nbp_api_client;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import exchange_rate.nbp_api_client.converter.Converter;
import exchange_rate.nbp_api_client.converter.json.JsonConverter;
import exchange_rate.nbp_api_client.downloader.Downloader;
import exchange_rate.nbp_api_client.downloader.Path;
import exchange_rate.nbp_api_client.downloader.http.HttpDownloader;
import exchange_rate.nbp_api_client.dto.ExchangeRate;
import exchange_rate.nbp_api_client.exception.checked.NotFoundException;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class NbpClientTestSuite {

	private static MockWebServer webServer;
	private String testUrl;
	private String testPath = "/testPath";
	private BigDecimal testResponseRate = new BigDecimal("4.4181");
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private String testResponseDate = "2020-05-03";
	private Currency testResponseCurrency = Currency.EURO;
	private final String CORRECT_RESPONSE = "{\"table\":\"A\",\"currency\":\""
			+ testResponseCurrency.getAlphabeticCode() + "\",\"code\":\"EUR\",\"rates\": "
			+ "[{\"no\":\"172/A/NBP/2020\",\"effectiveDate\":\"" + testResponseDate + "\",\"mid\":\"" + testResponseRate
			+ "\"}]}";

	@BeforeClass
	public static void initClass() throws IOException {
		webServer = new MockWebServer();
		webServer.start();
	}

	@AfterClass
	public static void afterClass() throws IOException {
		webServer.shutdown();
	}

	@Before
	public void before() {
		testUrl = "http://localhost:" + webServer.getPort() + testPath;
	}

	@Test
	public void test_requestActualExchangeRate_correctResponse() {
		// Given
		Path path = Mockito.mock(Path.class);
		Mockito.when(path.get(Mockito.any(), Mockito.any())).thenReturn(testUrl);
		Downloader downloader = new HttpDownloader();
		Converter converter = new JsonConverter();
		NbpClient nbpClient = new NbpClient(downloader, path, converter);

		Dispatcher dispacher = new Dispatcher() {

			@Override
			public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
				if (request.getPath().contains(testPath)) {
					return new MockResponse().setResponseCode(200).setBody(CORRECT_RESPONSE);
				}
				return new MockResponse().setResponseCode(404);
			}
		};
		webServer.setDispatcher(dispacher);

		// When
		ExchangeRate result = nbpClient.requestActualExchangeRate(testResponseCurrency);

		// Then
		assertThat(result.getCurrency()).isEqualTo(testResponseCurrency);
		assertThat(result.getRate()).isEqualTo(testResponseRate);
		assertThat(dateFormat.format(result.getDate())).isEqualTo(testResponseDate);
	}

	@Test
	public void test_requestActualExchangeRate_wrongResponse() {
		// Given
		Path path = Mockito.mock(Path.class);
		Mockito.when(path.get(Mockito.any(), Mockito.any())).thenReturn(testUrl);
		Downloader downloader = new HttpDownloader();
		Converter converter = new JsonConverter();
		NbpClient nbpClient = new NbpClient(downloader, path, converter);

		Dispatcher dispacher = new Dispatcher() {

			@Override
			public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
				return new MockResponse().setResponseCode(404);
			}
		};
		webServer.setDispatcher(dispacher);

		// When
		Exception result = Assert.assertThrows(NotFoundException.class,
				() -> nbpClient.requestActualExchangeRate(testResponseCurrency));

		// Then
		assertThat(result).hasMessage("Cannot find exchange rate! Make sure that data is correct.");
	}

	@Test
	public void test_requestExchangeRate_correctOnFirstDay() {
		// Given
		Path path = Mockito.mock(Path.class);
		Mockito.when(path.get(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(testUrl);
		Downloader downloader = new HttpDownloader();
		Converter converter = new JsonConverter();
		NbpClient nbpClient = new NbpClient(downloader, path, converter);

		Dispatcher dispacher = new Dispatcher() {

			@Override
			public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
				if (request.getPath().contains(testPath)) {
					return new MockResponse().setResponseCode(200).setBody(CORRECT_RESPONSE);
				}
				return new MockResponse().setResponseCode(404);
			}
		};
		webServer.setDispatcher(dispacher);

		// When
		ExchangeRate result = nbpClient.requestExchangeRate(testResponseCurrency, new Date());

		// Then
		assertThat(result.getCurrency()).isEqualTo(testResponseCurrency);
		assertThat(result.getRate()).isEqualTo(testResponseRate);
		assertThat(dateFormat.format(result.getDate())).isEqualTo(testResponseDate);
	}

	@Test
	public void test_requestExchangeRate_noCorrectResponse() {
		// Given
		Path path = Mockito.mock(Path.class);
		Downloader downloader = new HttpDownloader();
		Converter converter = new JsonConverter();
		NbpClient nbpClient = new NbpClient(downloader, path, converter);
		Mockito.when(path.get(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(testUrl);

		Dispatcher dispacher = new Dispatcher() {

			@Override
			public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
				return new MockResponse().setResponseCode(404);
			}
		};

		webServer.setDispatcher(dispacher);

		// When
		Exception result = Assert.assertThrows(NotFoundException.class,
				() -> nbpClient.requestExchangeRate(testResponseCurrency, new Date()));

		// Then
		assertThat(result).hasMessage("Cannot find exchange rate for that date! Make sure that data is correct.");
	}

	@Test
	public void test_requestExchangeRate_correctOnFifthDay() {
		// Given
		Path path = Mockito.mock(Path.class);
		Downloader downloader = new HttpDownloader();
		Converter converter = new JsonConverter();
		NbpClient nbpClient = new NbpClient(downloader, path, converter);

		Date date = new Date();
		LocalDateTime localDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
		localDate = localDate.minusDays(4);
		Date thirdDay = Date.from(localDate.atZone(ZoneId.systemDefault()).toInstant());

		String correctPath = "correctPath";
		Mockito.when(path.get(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(testUrl + "/" + "test_wrong_path");
		Mockito.when(path.get(Mockito.any(), Mockito.eq(thirdDay), Mockito.any()))
				.thenReturn(testUrl + "/" + correctPath);

		Dispatcher dispacher = new Dispatcher() {

			@Override
			public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
				if (request.getPath().contains(correctPath)) {
					return new MockResponse().setBody(CORRECT_RESPONSE);
				}
				return new MockResponse().setResponseCode(404);
			}
		};

		webServer.setDispatcher(dispacher);

		// When
		ExchangeRate result = nbpClient.requestExchangeRate(testResponseCurrency, date);

		// Then
		assertThat(result.getCurrency()).isEqualTo(testResponseCurrency);
		assertThat(result.getRate()).isEqualTo(testResponseRate);
		assertThat(dateFormat.format(result.getDate())).isEqualTo(testResponseDate);
	}

	@Test
	public void test_requestExchangeRate_correctOnSixthDay() {
		// Given
		Path path = Mockito.mock(Path.class);
		Downloader downloader = new HttpDownloader();
		Converter converter = new JsonConverter();
		NbpClient nbpClient = new NbpClient(downloader, path, converter);

		Date date = new Date();
		LocalDateTime localDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
		localDate = localDate.minusDays(5);
		Date thirdDay = Date.from(localDate.atZone(ZoneId.systemDefault()).toInstant());

		String correctPath = "correctPath";
		Mockito.when(path.get(Mockito.any(), Mockito.any(), Mockito.any()))
				.thenReturn(testUrl + "/" + "test_wrong_path");
		Mockito.when(path.get(Mockito.any(), Mockito.eq(thirdDay), Mockito.any()))
				.thenReturn(testUrl + "/" + correctPath);

		Dispatcher dispacher = new Dispatcher() {

			@Override
			public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
				if (request.getPath().contains(correctPath)) {
					return new MockResponse().setBody(CORRECT_RESPONSE);
				}
				return new MockResponse().setResponseCode(404);
			}
		};

		webServer.setDispatcher(dispacher);

		// When
		Exception result = Assert.assertThrows(NotFoundException.class,
				() -> nbpClient.requestExchangeRate(testResponseCurrency, date));

		// Then
		assertThat(result).hasMessage("Cannot find exchange rate for that date! Make sure that data is correct.");
	}

}