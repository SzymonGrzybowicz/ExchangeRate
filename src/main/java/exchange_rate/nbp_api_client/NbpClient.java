package exchange_rate.nbp_api_client;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import exchange_rate.nbp_api_client.dto.ExchangeRate;
import exchange_rate.nbp_api_client.exception.NbpWebApiException;
import exchange_rate.nbp_api_client.strategy.NbpClientStrategy;
import exchange_rate.web_client.WebResponse;

public class NbpClient {

	private NbpClientStrategy strategy;

	public NbpClient(NbpClientStrategy strategy) {
		this.strategy = strategy;
	}

	public ExchangeRate requestActualExchangeRate(Currency currency) throws NbpWebApiException {

		WebResponse response = strategy.getWebClient().request(strategy.getActualCurrencyRateUrl(currency));
		if (response.getCode() != 200)
			throw new NbpWebApiException("Response code not equal to 200!", response.getCode(), response.getBody());

		if (response.getBody() == null)
			throw new NbpWebApiException("Response body is null!", response.getCode(), null);

		ExchangeRate rate = strategy.getRateConverter().convertResponse(response.getBody());
		if (rate == null)
			throw new NbpWebApiException("Wrong response body format! Response: ", response.getCode(),
					response.getBody());

		return rate;
	}

	public ExchangeRate requestExchangeRate(Currency currency, Date date) throws NbpWebApiException {

		LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());

		WebResponse response = sendExchangeRateRequestForDate(currency, dateTime);

		int counter = 0;
		while (response.getCode() == 404 && response.getBody().equals("404 NotFound - Not Found - Brak danych")
				&& counter < 5) {
			counter++;
			dateTime = dateTime.minusDays(1);
			response = sendExchangeRateRequestForDate(currency, dateTime);
		}

		if (response.getCode() == 404 && response.getBody().equals("404 NotFound - Not Found - Brak danych"))
			throw new NbpWebApiException("Rate not found for that date!", response.getCode(), response.getBody());

		System.out.println(response.getCode() + response.getBody());
		if (response.getCode() != 200)
			throw new NbpWebApiException("Response code not equal to 200!", response.getCode(), response.getBody());

		if (response.getBody() == null)
			throw new NbpWebApiException("Response body is null!", response.getCode(), null);

		ExchangeRate rate = strategy.getRateConverter().convertResponse(response.getBody());
		if (rate == null)
			throw new NbpWebApiException("Wrong response body format! Response: ", response.getCode(),
					response.getBody());

		return rate;
	}

	private WebResponse sendExchangeRateRequestForDate(Currency currency, LocalDateTime dateTime) {
		return strategy.getWebClient().request(
				strategy.getCurrencyRateUrl(currency, Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant())));
	}

//	public Single<Double> getMinimumExchangeRateInPeroid(Currency currency, Date from, Date to) {
//
//		long daysBetweenDates = ChronoUnit.DAYS.between(from.toInstant(), to.toInstant());
//
//		if (daysBetweenDates > 366) {
//			return Single.error(new IllegalArgumentException("Peroid must be less than 367 days"));
//		}
//
//		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//
//		HttpUrl url = new HttpUrl.Builder().scheme("http").host("api.nbp.pl").addPathSegment("api")
//				.addPathSegment("exchangerates").addPathSegment("rates").addPathSegment("A") // table A
//				.addPathSegment(currency.getAlphabeticCode()).addPathSegment(df.format(from))
//				.addPathSegment(df.format(to)).build();
//
//		log.debug("getMinimumExchangeRateInPeroid(" + currency + ", " + from + ", " + to + ") url: " + url.toString());
//
//		Request request = new Request.Builder().url(url).build();
//
//		System.out.println(url.toString());
//
//		Single<Double> result = Single.create(emitter -> {
//			Callback responseCallback = new Callback() {
//
//				@Override
//				public void onResponse(Response response) throws IOException {
//					String responseBody = response.body().string();
//					log.debug("getMinimumExchangeRateInPeroid(" + currency + ", " + from + ", " + to + ") response: "
//							+ responseBody);
//					Gson gson = new Gson();
//					CurrencyExchangeRateResponse responseObject = gson.fromJson(responseBody,
//							CurrencyExchangeRateResponse.class);
//
//					double minimum = Double.MAX_VALUE;
//
//					for (int i = 0; i < responseObject.getRates().length; i++) {
//						Rate rate = responseObject.getRates()[i];
//						if (rate.getMid() < minimum) {
//							minimum = rate.getMid();
//						}
//					}
//
//					emitter.onSuccess(minimum);
//				}
//
//				@Override
//				public void onFailure(Request request, IOException e) {
//					emitter.onError(e);
//				}
//			};
//
//			client.newCall(request).enqueue(responseCallback);
//		});
//
//		return result;
//	}
}
