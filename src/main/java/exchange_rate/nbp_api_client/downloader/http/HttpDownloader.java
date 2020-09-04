package exchange_rate.nbp_api_client.downloader.http;

import java.io.IOException;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import exchange_rate.nbp_api_client.downloader.DownloaderStatus;
import exchange_rate.nbp_api_client.downloader.Downloader;
import exchange_rate.nbp_api_client.downloader.DownloaderResponse;

public class HttpDownloader implements Downloader {

	private OkHttpClient client = new OkHttpClient();

	@Override
	public DownloaderResponse get(String url) {
		Request request = new Request.Builder().url(url).build();
		try {
			Response response = client.newCall(request).execute();

			switch (response.code()) {
			case 200:
				return new DownloaderResponse(response.body().string(), DownloaderStatus.OK);
			case 404:
				return new DownloaderResponse(response.body().string(), DownloaderStatus.NOT_FOUND);
			default:
				return new DownloaderResponse(response.body().string(), DownloaderStatus.BAD_REQUEST);
			}

		} catch (IOException e) {
			e.printStackTrace();
			return new DownloaderResponse("connection broken", DownloaderStatus.CONNECTION_PROBLEM);
		}
	}
}
