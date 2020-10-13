package it.ned.bookmanager.service.exception;

import it.ned.bookmanager.model.Author;

public class AuthorDuplicateException extends RuntimeException {

	private static final long serialVersionUID = -5855446916055978653L;
	private final transient Author existingAuthor;

	public AuthorDuplicateException(String message, Author existingAuthor) {
		super(message);
		this.existingAuthor = existingAuthor;
	}

	public Author getExistingAuthor() {
		return existingAuthor;
	}
}
