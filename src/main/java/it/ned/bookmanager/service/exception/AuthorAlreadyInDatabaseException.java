package it.ned.bookmanager.service.exception;

public class AuthorAlreadyInDatabaseException extends RuntimeException {
    public AuthorAlreadyInDatabaseException(String message) {
        super(message);
    }
}
