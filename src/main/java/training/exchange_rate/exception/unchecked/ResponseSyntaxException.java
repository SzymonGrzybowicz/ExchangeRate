package training.exchange_rate.exception.unchecked;

public class ResponseSyntaxException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ResponseSyntaxException() {
		super();
	}

	public ResponseSyntaxException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ResponseSyntaxException(String message, Throwable cause) {
		super(message, cause);
	}

	public ResponseSyntaxException(String message) {
		super(message);
	}

	public ResponseSyntaxException(Throwable cause) {
		super(cause);
	}
}
