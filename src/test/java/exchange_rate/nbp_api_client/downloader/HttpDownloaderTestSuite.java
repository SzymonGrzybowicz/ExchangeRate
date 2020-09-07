package exchange_rate.nbp_api_client.downloader;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import exchange_rate.nbp_api_client.downloader.http.HttpDownloader;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class HttpDownloaderTestSuite {

	private static MockWebServer webServer;
	private String testUrl;

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
		testUrl = String.format("http://localhost:%s", webServer.getPort());
	}

	@Test
	public void test_get_responseCode418() {
		// Given
		String testBody = "Test body";
		HttpDownloader downloader = new HttpDownloader();
		webServer.enqueue(new MockResponse().setResponseCode(418).setBody(testBody));
		// When
		DownloaderResponse response = downloader.get(testUrl);

		// Then
		assertThat(response.getStatus()).isEqualTo(DownloaderResponse.Status.BAD_REQUEST);
		assertThat(response.getBody()).isEqualTo(testBody);
	}

	@Test
	public void test_get_responseCode200() {
		// Given
		String testBody = "Test body";
		HttpDownloader downloader = new HttpDownloader();
		webServer.enqueue(new MockResponse().setResponseCode(200).setBody(testBody));
		// When
		DownloaderResponse response = downloader.get(testUrl);

		// Then
		assertThat(response.getStatus()).isEqualTo(DownloaderResponse.Status.OK);
		assertThat(response.getBody()).isEqualTo(testBody);
	}

	@Test
	public void test_get_responseCode404() {
		// Given
		String testBody = "Test body";
		HttpDownloader downloader = new HttpDownloader();
		webServer.enqueue(new MockResponse().setResponseCode(404).setBody(testBody));
		// When
		DownloaderResponse response = downloader.get(testUrl);

		// Then
		assertThat(response.getStatus()).isEqualTo(DownloaderResponse.Status.NOT_FOUND);
		assertThat(response.getBody()).isEqualTo(testBody);
	}
}
