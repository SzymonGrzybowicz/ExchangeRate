package exchange_rate.nbp_api_client.exception;

public class NbpWebApiException extends Exception {

	public NbpWebApiException(String description, Integer responseCode, String responseBody) {
		super(description + ", responseCode: " + responseCode + ", responseBody: " + responseBody);
		this.responseCode = responseCode;
		this.responseBody = responseBody;
	}

	private Integer responseCode;
	private String responseBody;

	public Integer getResponseCode() {
		return responseCode;
	}

	public String getResponseBody() {
		return responseBody;
	}

	private static final long serialVersionUID = 1L;
}
