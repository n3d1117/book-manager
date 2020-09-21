package it.ned.bookmanager.service.exception;

import it.ned.bookmanager.model.Book;

public class BookDuplicateException extends RuntimeException {

    private final Book existingBook;

    public BookDuplicateException(String message, Book existingBook) {
        super(message);
        this.existingBook = existingBook;
    }

    public Book getExistingBook() {
        return existingBook;
    }
}
