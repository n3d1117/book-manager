package it.ned.bookmanager.service.exception;

import it.ned.bookmanager.model.Author;

public class AuthorDuplicateException extends RuntimeException {

    private final Author existingAuthor;

    public AuthorDuplicateException(String message, Author existingAuthor) {
        super(message);
        this.existingAuthor = existingAuthor;
    }

    public Author getExistingAuthor() {
        return existingAuthor;
    }
}
