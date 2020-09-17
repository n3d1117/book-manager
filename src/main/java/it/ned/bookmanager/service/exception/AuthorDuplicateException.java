package it.ned.bookmanager.service.exception;

public class AuthorDuplicateException extends RuntimeException {
    public AuthorDuplicateException(String message) {
        super(message);
    }
}
