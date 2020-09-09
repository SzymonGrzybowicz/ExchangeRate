package exchange_rate.nbp_api_client.exception.unchecked;

import exchange_rate.nbp_api_client.exception.UncheckedException;

public class DataParseException extends UncheckedException {

	private static final long serialVersionUID = 1L;

	public DataParseException() {
		super();
	}

	public DataParseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DataParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataParseException(String message) {
		super(message);
	}

	public DataParseException(Throwable cause) {
		super(cause);
	}
}
