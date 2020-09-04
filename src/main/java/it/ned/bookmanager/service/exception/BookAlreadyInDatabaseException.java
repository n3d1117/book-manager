package it.ned.bookmanager.service.exception;

public class BookAlreadyInDatabaseException extends RuntimeException {
    public BookAlreadyInDatabaseException(String message) {
        super(message);
    }
}
