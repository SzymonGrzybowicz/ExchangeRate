package exchange_rate.web_client.http;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import exchange_rate.web_client.WebClient;
import exchange_rate.web_client.WebResponse;

public class HttpWebClient implements WebClient {

	private OkHttpClient client = new OkHttpClient();
	private Logger log = Logger.getLogger(this.getClass().getName());

	@Override
	public WebResponse request(String url) {
		log.debug("request(" + url + ")");
		Request request = new Request.Builder().url(url).build();
		try {
			Response response = client.newCall(request).execute();
			return new WebResponse(response.body().string(), response.code());
		} catch (IOException e) {
			e.printStackTrace();
			return new WebResponse("connection broken", 404);
		}
	}
}
