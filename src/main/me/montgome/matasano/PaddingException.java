package me.montgome.matasano;

public class PaddingException extends RuntimeException {
    public PaddingException() {
        super();
    }

    public PaddingException(String message) {
        super(message);
    }

    public PaddingException(Throwable cause) {
        super(cause);
    }

    public PaddingException(String message, Throwable cause) {
        super(message, cause);
    }
}
