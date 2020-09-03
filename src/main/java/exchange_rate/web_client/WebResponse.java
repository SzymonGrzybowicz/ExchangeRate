package exchange_rate.web_client;

import com.sun.istack.NotNull;

public class WebResponse {

	private String body;
	private int code;

	public WebResponse(String body, int code) {
		super();
		this.body = body;
		this.code = code;
	}

	public String getBody() {
		return body;
	}

	@NotNull
	public int getCode() {
		return code;
	}
}
