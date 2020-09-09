package exchange_rate.nbp_api_client.exception.unchecked;

import exchange_rate.nbp_api_client.exception.UncheckedException;

public class DataFormatException extends UncheckedException {

	private static final long serialVersionUID = 1L;

	public DataFormatException() {
	}

	public DataFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DataFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataFormatException(String message) {
		super(message);
	}

	public DataFormatException(Throwable cause) {
		super(cause);
	}
}
