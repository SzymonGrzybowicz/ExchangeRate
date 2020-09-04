package exchange_rate.nbp_api_client.downloader;

public interface Downloader {
	DownloaderResponse get(String url);
}
