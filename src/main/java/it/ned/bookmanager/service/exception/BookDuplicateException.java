package it.ned.bookmanager.service.exception;

import it.ned.bookmanager.model.Book;

public class BookDuplicateException extends RuntimeException {

	private static final long serialVersionUID = -3903222207483890924L;
	private final transient Book existingBook;

	public BookDuplicateException(String message, Book existingBook) {
		super(message);
		this.existingBook = existingBook;
	}

	public Book getExistingBook() {
		return existingBook;
	}
}
