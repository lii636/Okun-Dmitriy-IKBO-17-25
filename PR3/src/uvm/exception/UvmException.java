package uvm.exception;

public class UvmException extends RuntimeException {
    public UvmException(String message) {
        super(message);
    }

    public UvmException(String message, Throwable cause) {
        super(message, cause);
    }
}
