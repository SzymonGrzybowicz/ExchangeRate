package exchange_rate.nbp_api_client.exception;

public class CheckedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CheckedException() {
		super();
	}

	public CheckedException(String message) {
		super(message);
	}

	public CheckedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CheckedException(String message, Throwable cause) {
		super(message, cause);
	}

	public CheckedException(Throwable cause) {
		super(cause);
	}
}
