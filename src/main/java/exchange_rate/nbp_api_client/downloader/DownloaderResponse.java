package exchange_rate.nbp_api_client.downloader;

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

	public Status getStatus() {
		return status;
	}

	public enum Status {
		OK, NOT_FOUND, CONNECTION_PROBLEM, BAD_REQUEST
	}
}
