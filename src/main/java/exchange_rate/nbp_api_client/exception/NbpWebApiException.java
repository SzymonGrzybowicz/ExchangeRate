package exchange_rate.nbp_api_client.exception;

public class NbpWebApiException extends Exception {

	public NbpWebApiException(String description, int responseCode, String responseBody) {
		super(description + ", responseCod: " + responseCode + ", responseBody: " + responseBody);
		this.responseCode = responseCode;
		this.responseBody = responseBody;
	}

	private final int responseCode;
	private final String responseBody;

	public int getResponseCode() {
		return responseCode;
	}

	public String getResponseBody() {
		return responseBody;
	}

	private static final long serialVersionUID = 1L;
}
