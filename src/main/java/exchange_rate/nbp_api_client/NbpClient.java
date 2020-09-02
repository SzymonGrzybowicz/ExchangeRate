package exchange_rate.nbp_api_client;

import java.io.IOException;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import exchange_rate.nbp_api_client.dto.CurrencyExchangeRateResponse;
import io.reactivex.rxjava3.core.Single;

public class NbpClient {

	public NbpClient(OkHttpClient client) {
		this.client = client;
	}

	private OkHttpClient client;

	public Single<Double> requestExchangeRate(Currency currency) {
		HttpUrl url = new HttpUrl.Builder().scheme("http").host("api.nbp.pl").addPathSegment("api")
				.addPathSegment("exchangerates").addPathSegment("rates").addPathSegment("A") // table A
				.addPathSegment(currency.getAlphabeticCode()).build();

		Request request = new Request.Builder().url(url).build();

		Single<Double> result = Single.create(emitter -> {
			Callback responseCallback = new Callback() {

				@Override
				public void onResponse(Response response) throws IOException {
					Gson gson = new Gson();
					CurrencyExchangeRateResponse value = gson.fromJson(response.body().string(),
							CurrencyExchangeRateResponse.class);

					emitter.onSuccess(value.getRates()[0].getMid());
				}

				@Override
				public void onFailure(Request request, IOException e) {
					emitter.onError(e);
				}
			};

			client.newCall(request).enqueue(responseCallback);
		});

		return result;
	}
}
