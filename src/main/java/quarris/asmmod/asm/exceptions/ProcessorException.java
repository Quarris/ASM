package quarris.asmmod.asm.exceptions;

public class ProcessorException extends Exception {

	public ProcessorException() {
		super();
	}

	public ProcessorException(String message) {
		super(message);
	}

	public ProcessorException(String message, Throwable cause) {
		super(message, cause);
	}

	public ProcessorException(Throwable cause) {
		super(cause);
	}
}
