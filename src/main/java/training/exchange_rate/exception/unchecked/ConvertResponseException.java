package training.exchange_rate.exception.unchecked;

import training.exchange_rate.exception.UncheckedException;

public class ConvertResponseException extends UncheckedException {

	private static final long serialVersionUID = 1L;

	public ConvertResponseException() {
		super();
	}

	public ConvertResponseException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ConvertResponseException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConvertResponseException(String message) {
		super(message);
	}

	public ConvertResponseException(Throwable cause) {
		super(cause);
	}
}
