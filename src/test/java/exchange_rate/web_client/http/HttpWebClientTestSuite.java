package exchange_rate.web_client.http;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import exchange_rate.nbp_api_client.downloader.DownloaderResponse;
import exchange_rate.nbp_api_client.downloader.http.HttpDownloader;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class HttpWebClientTestSuite {

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
	public void testRequest() {
		// Given
		String testBody = "Test body";
		HttpDownloader webClient = new HttpDownloader();
		webServer.enqueue(new MockResponse().setResponseCode(418).setBody(testBody));
		// When
		DownloaderResponse response = webClient.request(testUrl);

		// Then
		Assert.assertEquals(418, response.getCode());
		Assert.assertEquals(testBody, response.getBody());
	}

}
