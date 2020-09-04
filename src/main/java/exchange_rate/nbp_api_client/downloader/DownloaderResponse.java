package exchange_rate.nbp_api_client.downloader;

import com.sun.istack.NotNull;

public class DownloaderResponse {

	private String body;
	private DownloaderStatus status;

	public DownloaderResponse(String body, DownloaderStatus status) {
		super();
		this.body = body;
		this.status = status;
	}

	public String getBody() {
		return body;
	}

	@NotNull
	public DownloaderStatus getStatus() {
		return status;
	}
}
