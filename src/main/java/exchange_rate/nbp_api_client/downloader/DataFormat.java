package exchange_rate.nbp_api_client.downloader;

public enum DataFormat {
	JSON("?format=json"), XML("?format=xml");

	private final String urlPostfix;

	private DataFormat(String urlPostfix) {
		this.urlPostfix = urlPostfix;
	}

	public String getHttpUrlPostfix() {
		return urlPostfix;
	}
}
