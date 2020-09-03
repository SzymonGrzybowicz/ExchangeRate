package exchange_rate.nbp_api_client;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import exchange_rate.nbp_api_client.Currency;
import exchange_rate.nbp_api_client.NbpClient;

@RunWith(MockitoJUnitRunner.class)
public class NbpClientTestSuite {

	@Captor
	private ArgumentCaptor<Callback> captor;

	@Before
	public void init() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	public void testRequestExchangeRateSuccesfull() throws IOException, InterruptedException {
		OkHttpClient okHttpClient = Mockito.mock(OkHttpClient.class);
		Call remoteCall = Mockito.mock(Call.class);
		Mockito.when(okHttpClient.newCall(Mockito.any())).thenReturn(remoteCall);

		NbpClient nbpClient = new NbpClient(okHttpClient);

		nbpClient.requestActualExchangeRate(Currency.AMERICAN_DOLAR).subscribe(v -> Assert.assertEquals((Double) 4.3946, v),
				e -> Assert.assertTrue(false));

		Mockito.verify(remoteCall).enqueue(captor.capture());

		captor.getValue().onResponse(new Response.Builder().body(ResponseBody.create(
				MediaType.parse("application/json"),
				"{\"table\":\"A\",\"currency\":\"euro\",\"code\":\"EUR\",\"rates\":[{\"no\":\"170/A/NBP/2020\",\"effectiveDate\":\"2020-09-01\",\"mid\":4.3946}]}"))
				.request(new Request.Builder().url(new HttpUrl.Builder().scheme("http").host("some.some").build())
						.build())
				.protocol(Protocol.HTTP_1_1).code(200).build());

		new CountDownLatch(1).await(1, TimeUnit.SECONDS); // wait for finish RxJava
	}

	@Test
	public void testRequestExchangeRateFailed() throws IOException, InterruptedException {
		OkHttpClient okHttpClient = Mockito.mock(OkHttpClient.class);
		Call remoteCall = Mockito.mock(Call.class);
		Mockito.when(okHttpClient.newCall(Mockito.any())).thenReturn(remoteCall);

		NbpClient nbpClient = new NbpClient(okHttpClient);

		nbpClient.requestActualExchangeRate(Currency.AMERICAN_DOLAR).subscribe(v -> Assert.assertTrue(false),
				e -> Assert.assertTrue(e instanceof TestException));

		Mockito.verify(remoteCall).enqueue(captor.capture());

		captor.getValue().onFailure(
				new Request.Builder().url(new HttpUrl.Builder().scheme("http").host("some.some").build()).build(),
				new TestException("Test failure exception"));

		new CountDownLatch(1).await(1, TimeUnit.SECONDS); // wait for finish RxJava

	}

	class TestException extends IOException {

		public TestException(String description) {
			super(description);
		}
	}
}