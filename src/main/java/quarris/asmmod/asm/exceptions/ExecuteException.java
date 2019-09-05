package quarris.asmmod.asm.exceptions;

public class ExecuteException extends ProcessorException {

	public ExecuteException() {
		super();
	}

	public ExecuteException(String message) {
		super(message);
	}

	public ExecuteException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExecuteException(Throwable cause) {
		super(cause);
	}
}
