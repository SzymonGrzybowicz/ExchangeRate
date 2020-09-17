package training.exchange_rate.exception.unchecked;

public class ConvertResponseException extends RuntimeException {

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
