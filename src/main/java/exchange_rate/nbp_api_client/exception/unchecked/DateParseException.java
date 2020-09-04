package exchange_rate.nbp_api_client.exception.unchecked;

import exchange_rate.nbp_api_client.exception.UncheckedException;

public class DateParseException extends UncheckedException {

	private static final long serialVersionUID = 1L;

	public DateParseException() {
		super();
	}

	public DateParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DateParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public DateParseException(String message) {
		super(message);
	}

	public DateParseException(Throwable cause) {
		super(cause);
	}
}
