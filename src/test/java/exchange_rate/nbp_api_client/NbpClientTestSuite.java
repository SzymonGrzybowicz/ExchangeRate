package exchange_rate.nbp_api_client;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import exchange_rate.nbp_api_client.cache.Cache;
import exchange_rate.nbp_api_client.converter.Converter;
import exchange_rate.nbp_api_client.converter.json.JsonConverter;
import exchange_rate.nbp_api_client.country.CountryRepository;
import exchange_rate.nbp_api_client.downloader.Downloader;
import exchange_rate.nbp_api_client.downloader.Path;
import exchange_rate.nbp_api_client.downloader.http.HttpDownloader;
import exchange_rate.nbp_api_client.dto.Country;
import exchange_rate.nbp_api_client.dto.ExchangeRate;
import exchange_rate.nbp_api_client.exception.checked.NotFoundException;
import exchange_rate.nbp_api_client.exception.unchecked.BadRequestException;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class NbpClientTestSuite {

	private static MockWebServer webServer;
	private String testUrl;
	private String testPath = "/testPath";
	private BigDecimal testResponseRate = new BigDecimal("4.4181");
	private Date testResponseDate = new Date(1234);
	private Currency testResponseCurrency = Currency.EURO;
	private Set<Currency> testCurrencySet = new HashSet<>(Arrays.asList(Currency.AMERICAN_DOLAR, Currency.EURO));
	private Country testCountry = new Country(CountryName.WAKANDA, testCurrencySet);

	//@formatter:off
	private final String CORRECT_CURRENCY_RESPONSE = 
	"{" +
		"\"table\":\"A\"," +
		"\"currency\":\"" + testResponseCurrency.name() + "\"," +
		"\"code\":\"" + testResponseCurrency.getAlphabeticCode() +"\"," +
		"\"rates\": " +
		"[" +
			"{" +
				"\"no\":\"172/A/NBP/2020\"," +
				"\"effectiveDate\":\"" + new SimpleDateFormat("yyyy-MM-dd").format(testResponseDate) + "\"," +
				"\"mid\":\"" + testResponseRate +"\"" +
			"}" +
		"]" +
	"}";
	
	private final String CORRECT_TABLE_RESPONSE = 
			"[" +
			   "{" +
			      "\"table\":\"A\"," +
			      "\"no\":\"174/A/NBP/2020\"," +
			      "\"effectiveDate\":\"" + new SimpleDateFormat("yyyy-MM-dd").format(testResponseDate) + "\"," +
			      "\"rates\":" +
		    	  "[" +
					"{" +
					    "\"currency\":\"dolar amerykañski\"," +
					    "\"code\":\"USD\"," +
					    "\"mid\":3.7666" +
					"}," +
					"{" +
				            "\"currency\":\"" + testResponseCurrency.name() + "\"," +
				            "\"code\":\"" + testResponseCurrency.getAlphabeticCode() + "\"," +
				            "\"mid\":" + testResponseRate +
		            "}," +
					"{" +
			            "\"currency\":\"frank szwajcarski\"," +
			            "\"code\":\"CHF\"," +
			            "\"mid\":4.1253" +
		            "}," +
		            "{" +
			            "\"currency\":\"funt szterling\"," +
			            "\"code\":\"GBP\"," +
			            "\"mid\":4.9688" +
			         "}" +
			       "]" +
			   "}" +
			 "]"; 

	//@formatter:on

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
	public void test_requestActualExchangeRateForCurrency_correctResponse() {
		// Given
		Path path = Mockito.mock(Path.class);
		Downloader downloader = new HttpDownloader();
		Converter converter = new JsonConverter();
		NbpClient nbpClient = new NbpClient(downloader, path, converter);

		Mockito.when(path.get(Mockito.any(), Mockito.any())).thenReturn(testUrl);

		Dispatcher dispacher = new Dispatcher() {

			@Override
			public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
				if (request.getPath().contains(testPath)) {
					return new MockResponse().setResponseCode(200).setBody(CORRECT_CURRENCY_RESPONSE);
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
		assertThat(DateUtils.isSameDay(result.getDate(), testResponseDate)).isTrue();
	}

	@Test
	public void test_requestActualExchangeRateForCurrency_wrongResponse() {
		// Given
		Path path = Mockito.mock(Path.class);
		Downloader downloader = new HttpDownloader();
		Converter converter = new JsonConverter();
		NbpClient nbpClient = new NbpClient(downloader, path, converter);

		Mockito.when(path.get(Mockito.any(), Mockito.any())).thenReturn(testUrl);

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
	public void test_requestActualExchangeRateForCurrency_objectFromCache() {
		// Given
		Path path = Mockito.mock(Path.class);
		Downloader downloader = Mockito.mock(Downloader.class);
		Cache cache = Mockito.mock(Cache.class);
		Converter converter = new JsonConverter();

		ExchangeRate testExchangeRate = new ExchangeRate(testResponseDate, testResponseCurrency, testResponseRate);
		Mockito.when(cache.getOrNull(Mockito.any(), Mockito.any())).thenReturn(testExchangeRate);

		NbpClient nbpClient = new NbpClient(downloader, path, converter, cache);

		// When
		ExchangeRate result = nbpClient.requestActualExchangeRate(testResponseCurrency);

		// Then
		Mockito.verify(downloader, Mockito.never()).get(Mockito.any());
		assertThat(result.getCurrency()).isEqualTo(testResponseCurrency);
		assertThat(result.getRate()).isEqualTo(testResponseRate);
		assertThat(DateUtils.isSameDay(result.getDate(), testResponseDate)).isTrue();
	}

	@Test
	public void test_requestActualExchangeRateForCurrency_nullFromCache() {
		// Given
		Path path = Mockito.mock(Path.class);
		Cache cache = Mockito.mock(Cache.class);
		Downloader downloader = new HttpDownloader();
		Converter converter = new JsonConverter();
		NbpClient nbpClient = new NbpClient(downloader, path, converter, cache);

		Mockito.when(path.get(Mockito.any(), Mockito.any())).thenReturn(testUrl);
		Mockito.when(cache.getOrNull(Mockito.any(), Mockito.any())).thenReturn(null);

		Dispatcher dispacher = new Dispatcher() {

			@Override
			public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
				if (request.getPath().contains(testPath)) {
					return new MockResponse().setResponseCode(200).setBody(CORRECT_CURRENCY_RESPONSE);
				}
				return new MockResponse().setResponseCode(404);
			}
		};
		webServer.setDispatcher(dispacher);

		// When
		ExchangeRate result = nbpClient.requestActualExchangeRate(testResponseCurrency);

		// Then
		Mockito.verify(cache).saveOrUpdateIfExists(result);
		assertThat(result.getCurrency()).isEqualTo(testResponseCurrency);
		assertThat(result.getRate()).isEqualTo(testResponseRate);
		assertThat(DateUtils.isSameDay(result.getDate(), testResponseDate)).isTrue();
	}

	@Test
	public void test_requestExchangeRateForCurrency_correctOnFirstDay() {
		// Given
		Path path = Mockito.mock(Path.class);
		Downloader downloader = new HttpDownloader();
		Converter converter = new JsonConverter();
		NbpClient nbpClient = new NbpClient(downloader, path, converter);

		Mockito.when(path.get(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(testUrl);

		Dispatcher dispacher = new Dispatcher() {

			@Override
			public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
				if (request.getPath().contains(testPath)) {
					return new MockResponse().setResponseCode(200).setBody(CORRECT_CURRENCY_RESPONSE);
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
		assertThat(DateUtils.isSameDay(result.getDate(), testResponseDate)).isTrue();
	}

	@Test
	public void test_requestExchangeRateForCurrency_wrongResponse() {
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
	public void test_requestExchangeRateForCurrency_correctOnFifthDay() {
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
					return new MockResponse().setBody(CORRECT_CURRENCY_RESPONSE);
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
		assertThat(DateUtils.isSameDay(result.getDate(), testResponseDate)).isTrue();
	}

	@Test
	public void test_requestExchangeRateForCurrency_correctOnSixthDay() {
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
					return new MockResponse().setBody(CORRECT_CURRENCY_RESPONSE);
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

	@Test
	public void test_requestExchangeRateForCurrency_objectFromCache() {
		// Given
		Path path = Mockito.mock(Path.class);
		Downloader downloader = Mockito.mock(Downloader.class);
		Cache cache = Mockito.mock(Cache.class);
		Converter converter = new JsonConverter();

		ExchangeRate testExchangeRate = new ExchangeRate(testResponseDate, testResponseCurrency, testResponseRate);
		Mockito.when(cache.getOrNull(Mockito.any(), Mockito.any())).thenReturn(testExchangeRate);

		NbpClient nbpClient = new NbpClient(downloader, path, converter, cache);

		// When
		ExchangeRate result = nbpClient.requestExchangeRate(testResponseCurrency, testResponseDate);

		// Then
		Mockito.verify(downloader, Mockito.never()).get(Mockito.any());
		assertThat(result.getCurrency()).isEqualTo(testResponseCurrency);
		assertThat(result.getRate()).isEqualTo(testResponseRate);
		assertThat(DateUtils.isSameDay(result.getDate(), testResponseDate)).isTrue();
	}

	@Test
	public void test_requestExchangeRateForCurrency_nullFromCache() {
		// Given
		Path path = Mockito.mock(Path.class);
		Cache cache = Mockito.mock(Cache.class);
		Downloader downloader = new HttpDownloader();
		Converter converter = new JsonConverter();
		NbpClient nbpClient = new NbpClient(downloader, path, converter, cache);

		Mockito.when(path.get(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(testUrl);
		Mockito.when(cache.getOrNull(Mockito.any(), Mockito.any())).thenReturn(null);

		Dispatcher dispacher = new Dispatcher() {

			@Override
			public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
				if (request.getPath().contains(testPath)) {
					return new MockResponse().setResponseCode(200).setBody(CORRECT_CURRENCY_RESPONSE);
				}
				return new MockResponse().setResponseCode(404);
			}
		};
		webServer.setDispatcher(dispacher);

		// When
		ExchangeRate result = nbpClient.requestExchangeRate(testResponseCurrency, new Date());

		// Then
		Mockito.verify(cache).saveOrUpdateIfExists(result);
		assertThat(result.getCurrency()).isEqualTo(testResponseCurrency);
		assertThat(result.getRate()).isEqualTo(testResponseRate);
		assertThat(DateUtils.isSameDay(result.getDate(), testResponseDate)).isTrue();
	}

	@Test
	public void test_requestActualExchangeRateForCountry_nullCountryRepository() {
		// Given
		Path path = Mockito.mock(Path.class);
		Cache cache = Mockito.mock(Cache.class);
		Downloader downloader = new HttpDownloader();
		Converter converter = new JsonConverter();

		NbpClient nbpClient = new NbpClient(downloader, path, converter, cache);

		// When
		Exception e = Assert.assertThrows(BadRequestException.class,
				() -> nbpClient.requestActualExchangeRate(testCountry.getName()));

		// Then
		assertThat(e).hasMessage("Country repository didn't set, cannot request by country");
	}

	@Test
	public void test_requestActualExchangeRateForCountry_correctResponse() {
		// Given
		Path path = Mockito.mock(Path.class);
		Cache cache = Mockito.mock(Cache.class);
		CountryRepository countryRepository = Mockito.mock(CountryRepository.class);
		Downloader downloader = new HttpDownloader();
		Converter converter = new JsonConverter();
		NbpClient nbpClient = new NbpClient(downloader, path, converter, countryRepository, cache);

		Mockito.when(path.getAll(Mockito.any())).thenReturn(testUrl);
		Mockito.when(countryRepository.get(Mockito.any())).thenReturn(testCountry);
		Mockito.doNothing().when(cache).saveOrUpdateIfExists(Mockito.any());

		Dispatcher dispacher = new Dispatcher() {

			@Override
			public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
				if (request.getPath().contains(testPath)) {
					return new MockResponse().setResponseCode(200).setBody(CORRECT_TABLE_RESPONSE);
				}
				return new MockResponse().setResponseCode(404);
			}
		};
		webServer.setDispatcher(dispacher);

		// When
		List<ExchangeRate> result = nbpClient.requestActualExchangeRate(testCountry.getName());

		// Then
		assertThat(result).hasSize(2);

		Optional<ExchangeRate> optionalRate = result.stream().filter(e -> e.getCurrency().equals(testResponseCurrency))
				.findFirst();
		assertThat(optionalRate).isNotEmpty();

		ExchangeRate testRate = optionalRate.get();
		assertThat(testRate.getCurrency()).isEqualTo(testResponseCurrency);
		assertThat(testRate.getRate()).isEqualTo(testResponseRate);
		assertThat(DateUtils.isSameDay(testRate.getDate(), testResponseDate)).isTrue();
		Mockito.verify(cache, Mockito.times(2)).saveOrUpdateIfExists(Mockito.any());
	}

	@Test
	public void test_requestActualExchangeRateForCountry_wrongResponse() {
		// Given
		Path path = Mockito.mock(Path.class);
		Cache cache = Mockito.mock(Cache.class);
		CountryRepository countryRepository = Mockito.mock(CountryRepository.class);
		Downloader downloader = new HttpDownloader();
		Converter converter = new JsonConverter();
		NbpClient nbpClient = new NbpClient(downloader, path, converter, countryRepository, cache);

		Mockito.when(path.getAll(Mockito.any())).thenReturn(testUrl);
		Mockito.when(countryRepository.get(Mockito.any())).thenReturn(testCountry);
		Mockito.doNothing().when(cache).saveOrUpdateIfExists(Mockito.any());

		Dispatcher dispacher = new Dispatcher() {

			@Override
			public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
				return new MockResponse().setResponseCode(404);
			}
		};
		webServer.setDispatcher(dispacher);

		// When
		Exception result = Assert.assertThrows(NotFoundException.class,
				() -> nbpClient.requestActualExchangeRate(testCountry.getName()));

		// Then
		assertThat(result).hasMessage("Cannot find exchange rate! Make sure that data is correct.");
	}

	@Test
	public void test_requestExchangeRateForCountry_correctOnFirstDay() {
		// Given
		Path path = Mockito.mock(Path.class);
		Cache cache = Mockito.mock(Cache.class);
		CountryRepository countryRepository = Mockito.mock(CountryRepository.class);
		Downloader downloader = new HttpDownloader();
		Converter converter = new JsonConverter();
		NbpClient nbpClient = new NbpClient(downloader, path, converter, countryRepository, cache);

		Mockito.when(path.getAll(Mockito.any(), Mockito.any())).thenReturn(testUrl);
		Mockito.when(countryRepository.get(Mockito.any())).thenReturn(testCountry);
		Mockito.doNothing().when(cache).saveOrUpdateIfExists(Mockito.any());

		Dispatcher dispacher = new Dispatcher() {

			@Override
			public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
				if (request.getPath().contains(testPath)) {
					return new MockResponse().setResponseCode(200).setBody(CORRECT_TABLE_RESPONSE);
				}
				return new MockResponse().setResponseCode(404);
			}
		};
		webServer.setDispatcher(dispacher);

		// When
		List<ExchangeRate> result = nbpClient.requestExchangeRate(testCountry.getName(), new Date());

		// Then
		assertThat(result).hasSize(2);

		Optional<ExchangeRate> optionalRate = result.stream().filter(e -> e.getCurrency().equals(testResponseCurrency))
				.findFirst();
		assertThat(optionalRate).isNotEmpty();

		ExchangeRate testRate = optionalRate.get();

		assertThat(testRate.getRate()).isEqualTo(testResponseRate);
		assertThat(DateUtils.isSameDay(testRate.getDate(), testResponseDate)).isTrue();
	}

	@Test
	public void test_requestExchangeRateForCountry_wrongResponse() {
		// Given
		Path path = Mockito.mock(Path.class);
		Cache cache = Mockito.mock(Cache.class);
		CountryRepository countryRepository = Mockito.mock(CountryRepository.class);
		Downloader downloader = new HttpDownloader();
		Converter converter = new JsonConverter();
		NbpClient nbpClient = new NbpClient(downloader, path, converter, countryRepository, cache);

		Mockito.when(path.getAll(Mockito.any(), Mockito.any())).thenReturn(testUrl);
		Mockito.when(countryRepository.get(Mockito.any())).thenReturn(testCountry);
		Mockito.doNothing().when(cache).saveOrUpdateIfExists(Mockito.any());

		Dispatcher dispacher = new Dispatcher() {

			@Override
			public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
				return new MockResponse().setResponseCode(404);
			}
		};

		webServer.setDispatcher(dispacher);

		// When
		Exception result = Assert.assertThrows(NotFoundException.class,
				() -> nbpClient.requestExchangeRate(testCountry.getName(), new Date()));

		// Then
		assertThat(result).hasMessage("Cannot find exchange rate for that date! Make sure that data is correct.");
	}

	@Test
	public void test_requestExchangeRateForCountry_correctOnFifthDay() {
		// Given
		Path path = Mockito.mock(Path.class);
		Cache cache = Mockito.mock(Cache.class);
		CountryRepository countryRepository = Mockito.mock(CountryRepository.class);
		Downloader downloader = new HttpDownloader();
		Converter converter = new JsonConverter();
		NbpClient nbpClient = new NbpClient(downloader, path, converter, countryRepository, cache);

		Mockito.when(countryRepository.get(Mockito.any())).thenReturn(testCountry);
		Mockito.doNothing().when(cache).saveOrUpdateIfExists(Mockito.any());

		Date date = new Date();
		LocalDateTime localDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
		localDate = localDate.minusDays(4);
		Date thirdDay = Date.from(localDate.atZone(ZoneId.systemDefault()).toInstant());

		String correctPath = "correctPath";
		Mockito.when(path.getAll(Mockito.any(), Mockito.any())).thenReturn(testUrl + "/" + "test_wrong_path");
		Mockito.when(path.getAll(Mockito.eq(thirdDay), Mockito.any())).thenReturn(testUrl + "/" + correctPath);

		Dispatcher dispacher = new Dispatcher() {

			@Override
			public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
				if (request.getPath().contains(correctPath)) {
					return new MockResponse().setBody(CORRECT_TABLE_RESPONSE);
				}
				return new MockResponse().setResponseCode(404);
			}
		};

		webServer.setDispatcher(dispacher);

		// When
		List<ExchangeRate> result = nbpClient.requestExchangeRate(testCountry.getName(), date);

		// Then
		assertThat(result).hasSize(2);

		Optional<ExchangeRate> optionalRate = result.stream().filter(e -> e.getCurrency().equals(testResponseCurrency))
				.findFirst();
		assertThat(optionalRate).isNotEmpty();

		ExchangeRate testRate = optionalRate.get();

		assertThat(testRate.getRate()).isEqualTo(testResponseRate);
		assertThat(DateUtils.isSameDay(testRate.getDate(), testResponseDate)).isTrue();
	}

	@Test
	public void test_requestExchangeRateForCountry_correctOnSixthDay() {
		// Given
		Path path = Mockito.mock(Path.class);
		Cache cache = Mockito.mock(Cache.class);
		CountryRepository countryRepository = Mockito.mock(CountryRepository.class);
		Downloader downloader = new HttpDownloader();
		Converter converter = new JsonConverter();
		NbpClient nbpClient = new NbpClient(downloader, path, converter, countryRepository, cache);

		Mockito.when(countryRepository.get(Mockito.any())).thenReturn(testCountry);
		Mockito.doNothing().when(cache).saveOrUpdateIfExists(Mockito.any());

		Date date = new Date();
		LocalDateTime localDate = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
		localDate = localDate.minusDays(5);
		Date thirdDay = Date.from(localDate.atZone(ZoneId.systemDefault()).toInstant());

		String correctPath = "correctPath";
		Mockito.when(path.getAll(Mockito.any(), Mockito.any())).thenReturn(testUrl + "/" + "test_wrong_path");
		Mockito.when(path.getAll(Mockito.eq(thirdDay), Mockito.any())).thenReturn(testUrl + "/" + correctPath);

		Dispatcher dispacher = new Dispatcher() {

			@Override
			public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
				if (request.getPath().contains(correctPath)) {
					return new MockResponse().setBody(CORRECT_TABLE_RESPONSE);
				}
				return new MockResponse().setResponseCode(404);
			}
		};

		webServer.setDispatcher(dispacher);

		// When
		Exception result = Assert.assertThrows(NotFoundException.class,
				() -> nbpClient.requestExchangeRate(testCountry.getName(), date));

		// Then
		assertThat(result).hasMessage("Cannot find exchange rate for that date! Make sure that data is correct.");
	}

}