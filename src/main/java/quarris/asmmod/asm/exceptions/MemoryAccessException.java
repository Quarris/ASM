package quarris.asmmod.asm.exceptions;

public class MemoryAccessException extends ExecuteException {

	public MemoryAccessException() {
		super();
	}

	public MemoryAccessException(String message) {
		super(message);
	}

	public MemoryAccessException(String message, Throwable cause) {
		super(message, cause);
	}

	public MemoryAccessException(Throwable cause) {
		super(cause);
	}
}
