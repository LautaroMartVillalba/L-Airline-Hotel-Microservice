package ar.com.l_airline.exceptionHandler.custom_exceptions;

public class ConflictStateException extends RuntimeException {
    public ConflictStateException(String message) {
        super(message);
    }
}
