package ar.com.l_airline.exceptionHandler.custom_exceptions;

public class NotFoundInDatabaseException extends RuntimeException {
    public NotFoundInDatabaseException(String message) {
        super(message);
    }
}
