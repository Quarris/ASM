package quarris.asmmod.asm.exceptions;

public class FetchException extends ProcessorException {

	public FetchException() {
		super();
	}

	public FetchException(String message) {
		super(message);
	}

	public FetchException(String message, Throwable cause) {
		super(message, cause);
	}

	public FetchException(Throwable cause) {
		super(cause);
	}
}
