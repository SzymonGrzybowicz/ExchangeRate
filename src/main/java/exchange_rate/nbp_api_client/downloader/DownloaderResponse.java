package exchange_rate.nbp_api_client.downloader;

import com.sun.istack.NotNull;

public class DownloaderResponse {

	private String body;
	private Status status;

	public DownloaderResponse(String body, Status status) {
		super();
		this.body = body;
		this.status = status;
	}

	public String getBody() {
		return body;
	}

	@NotNull
	public Status getStatus() {
		return status;
	}

	public enum Status {
		OK, NOT_FOUND, CONNECTION_PROBLEM, BAD_REQUEST
	}
}
